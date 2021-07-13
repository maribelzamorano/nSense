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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components;

import java.util.List;

import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.KademliaRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.KademliaOperationFactory;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.OperationFactory.NodeLookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * A standard Kademlia node. For details about nodes, see
 * {@link AbstractKademliaNode}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaNode<T extends KademliaOverlayID> extends
		AbstractKademliaNode<T> implements DHTNode<KademliaOverlayContact<T>> {

	/**
	 * This node's routing table.
	 */
	private final RoutingTable<T> routingTable;

	/**
	 * This node's operation factory (used to construct lookup, store etc.
	 * operations)
	 */
	private final KademliaOperationFactory<T> operationFactory;

	/**
	 * Constructs a new standard Kademlia node.
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaNode(final KademliaOverlayContact<T> myContact,
			final TransLayer msgMgr, final ComponentsConfig conf) {
		super(myContact, msgMgr, conf);
		routingTable = new KademliaRoutingTable<T>(myContact, conf, this);
		operationFactory = new KademliaOperationFactory<T>(this, conf);

		// construct handler for incoming requests & new neighbours
		final RequestHandler<T> requestHandler = new RequestHandler<T>(
				getMessageManager(), this, conf);
		getMessageManager().addTransMsgListener(requestHandler, getPort());
		routingTable.registerProximityListener(requestHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperationFactory<T> getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RoutingTable<T> getKademliaRoutingTable() {
		return this.routingTable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[KademliaNode|" + getLocalContact().getOverlayID()
				+ "; status=" + getPeerStatus() + "]";
	}

	@Override
	public int nodeLookup(OverlayKey key,
			OperationCallback<List<KademliaOverlayContact<T>>> callback,
			boolean returnSingleNode) {

		if (!(key instanceof KademliaOverlayKey))
			return -1;

		NodeLookupOperation<List<KademliaOverlayContact<T>>, T> op = getOperationFactory()
				.getKClosestNodesLookupOperation((KademliaOverlayKey) key,
						Reason.USER_INITIATED, callback);

		return op.getOperationID();
	}

}
