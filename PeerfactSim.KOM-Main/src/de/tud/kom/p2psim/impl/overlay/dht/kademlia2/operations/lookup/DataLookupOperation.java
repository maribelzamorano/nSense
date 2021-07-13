/*
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup;

import java.util.Collections;
import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.OperationsConfig;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.LookupCoordinator.NonhierarchicalLookupCoordinator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.LookupCoordinatorClient.NonhierarchicalLookupCoordinatorClient;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.setup.KademliaSetup;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.DataLookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.DataMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.KademliaMsg;

/**
 * An operation that looks up the data item associated with a given key (the key
 * is held by the lookup coordinator). It has to be customised with a
 * LookupCoordinator that adapts the lookup process to a certain flavour of
 * Kademlia (standard, Kandy, or hierarchical).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DataLookupOperation<T extends KademliaOverlayID> extends
		AbstractLookupOperation<DHTObject, T>
		implements
		NonhierarchicalLookupCoordinatorClient<T>,
		de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.OperationFactory.DataLookupOperation<DHTObject> {

	/**
	 * The data item that is to be looked up.
	 */
	private DHTObject data;

	/**
	 * The KademliaOverlayID of the sender that sent the data item (used for
	 * measurement purposes).
	 */
	private T sender;

	/**
	 * Constructs a new data lookup operation.
	 * 
	 * @param coordinator
	 *            the NonhierarchicalLookupCoordinator that implements the
	 *            details of the lookup process in a certains flavour of
	 *            Kademlia (for example standard Kademlia, Kandy, or
	 *            hierarchical Kademlia).
	 * @param node
	 *            the Node that initiates this lookup.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            why this operation is to be executed - either user-initiated
	 *            or for maintenance.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public DataLookupOperation(
			final NonhierarchicalLookupCoordinator<T> coordinator,
			final Node<T> node, final OperationCallback opCallback,
			final Reason reason, final OperationsConfig conf) {
		super(coordinator, node, opCallback, reason, conf);
		coordinator.setClient(this);
		data = null;
		sender = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		KademliaSetup.getMonitor().dataLookupInitiated(this);
		super.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final DHTObject getData() {
		return data;
	}

	@Override
	public DHTObject getResult() {
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void messageReceived(final DataMsg<T> message) {
		// accept data only if previously unknown, != null and correct key
		if (data == null && message.getKey().equals(coordinator.getKey())
				&& message.getValue() != null) {
			data = message.getValue();
			sender = message.getSender();
		}
		// notify coordinator no matter what has happened
		final List<KademliaOverlayContact<T>> noResult = Collections
				.emptyList();
		coordinator.contactListReceived(noResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void coordinatorFinished() {
		// success iff data has been found
		if (data == null) {
			finishOperation(OperationState.ERROR);
		} else {
			finishOperation(OperationState.SUCCESS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void operationJustFinished() {
		KademliaSetup.getMonitor().dataLookupCompleted(coordinator.getKey(),
				data, sender, coordinator.getCurrentlyKnownKClosestNodes(),
				this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isContinuationNecessary() {
		if (isFinished()) {
			// operation has been set as finished (possibly externally)
			return false;
		} else if (data == null) {
			// continue until data != null
			return true;
		} else {
			// operation not yet set as finished, but data has been found
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void sendLookupMessage(
			final KademliaOverlayContact<T> destination) {
		final KademliaMsg<T> message = new DataLookupMsg<T>(getComponent()
				.getTypedOverlayID(), destination.getOverlayID(),
				coordinator.getKey(), getReason(), config);
		sendAndWait(message, destination);
		KademliaSetup.getMonitor().dataLookupMsgSent(message, this);
	}

}
