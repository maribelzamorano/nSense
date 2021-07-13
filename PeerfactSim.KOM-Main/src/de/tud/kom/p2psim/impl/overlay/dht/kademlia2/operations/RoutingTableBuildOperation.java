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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations;

import java.util.List;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;

/**
 * An operation that initially builds the routing table. It is assumed that the
 * routing table already contains some (valid) contacts. Building the routing
 * table is currently implemented by forcing a refresh on all its buckets and
 * looking up the Node's K closest neighbours.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
/*
 * TODO: we should look up the peer's neighbours first and refresh the buckets
 * afterwards, as new buckets might be created in the lookup.
 */
public class RoutingTableBuildOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<Object, T> {

	/**
	 * The operation identifier of the currently executing bucket refresh
	 * operation.
	 */
	private int bucketRefreshOperationID;

	/**
	 * The operation identifier of the currently executing k closest nodes
	 * lookup operation.
	 */
	private int kClosestNodesLookupOperationID;

	/**
	 * Constructs a new routing table build operation. This operation is always
	 * for maintenance purposes.
	 * 
	 * @param node
	 *            the Node that initiates this routing table build operation.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public RoutingTableBuildOperation(final Node<T> node,
			final OperationCallback opCallback, final OperationsConfig conf) {
		super(node, opCallback, Reason.MAINTENANCE, conf);
		bucketRefreshOperationID = -1;
		kClosestNodesLookupOperationID = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		// start with bucket refresh operation
		final KademliaOperation<List<KademliaOverlayContact<T>>> bucketRefreshOp = getComponent()
				.getOperationFactory().getRefreshOperation(true, this);
		bucketRefreshOperationID = bucketRefreshOp.getOperationID();
		bucketRefreshOp.scheduleImmediately();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidFail(final Operation op) {
		// we ignore that the other operation failed (because failure
		// is not handled)
		operationReturned(op.getOperationID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void calledOperationDidSucceed(final Operation op) {
		operationReturned(op.getOperationID());
	}

	private void operationReturned(final int opID) {
		// check whether event was caused by the operation we are waiting for
		if (bucketRefreshOperationID == opID) {
			// bucket refresh finished -> look up k closest nodes
			bucketRefreshOperationID = -1;
			final KademliaOperation<List<KademliaOverlayContact<T>>> kClosestNodesLookupOp = getComponent()
					.getOperationFactory().getKClosestNodesLookupOperation(
							getComponent().getTypedOverlayID().toKey(),
							getReason(), this);
			kClosestNodesLookupOperationID = kClosestNodesLookupOp
					.getOperationID();
			kClosestNodesLookupOp.scheduleImmediately();
		} else if (kClosestNodesLookupOperationID == opID) {
			// k closest nodes lookup finished -> routing table built
			finishOperation(OperationState.SUCCESS);
			getComponent().routingTableBuilt();
		}
	}
}
