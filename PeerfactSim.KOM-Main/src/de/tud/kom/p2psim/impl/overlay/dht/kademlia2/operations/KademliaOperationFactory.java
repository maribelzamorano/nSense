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

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.LookupCoordinator.NonhierarchicalLookupCoordinator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.StandardLookupCoordinator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * Operations as used in standard Kademlia.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaOperationFactory<T extends KademliaOverlayID> extends
		AbstractOperationFactory<T> {

	/**
	 * The node that owns this operation factory
	 */
	private final Node<T> myNode;

	/**
	 * Constructs a new standard Kademlia operation factory for the given
	 * KademliaNode.
	 * 
	 * @param myNode
	 *            the Node that owns this operation factory.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaOperationFactory(final Node<T> myNode,
			final OperationsConfig conf) {
		super(conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unused")
	public final KademliaOperation<List<KademliaOverlayContact<T>>> getBucketLookupOperation(
			final KademliaOverlayKey key, final int bucketDepth,
			final OperationCallback opCallback) {
		return getKClosestNodesLookupOperation(key, Reason.MAINTENANCE,
				opCallback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final DataLookupOperation<DHTObject> getDataLookupOperation(
			final KademliaOverlayKey key, final OperationCallback opCallback) {
		final NonhierarchicalLookupCoordinator<T> coord = new StandardLookupCoordinator<T>(
				key, myNode, config);
		final DataLookupOperation<DHTObject> op = new de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.DataLookupOperation<T>(
				coord, myNode, opCallback, Reason.USER_INITIATED, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeLookupOperation<List<KademliaOverlayContact<T>>, T> getKClosestNodesLookupOperation(
			KademliaOverlayKey key, Reason why, OperationCallback opCallback) {
		final NonhierarchicalLookupCoordinator<T> coord = new StandardLookupCoordinator<T>(
				key, myNode, config);
		final NodeLookupOperation<List<KademliaOverlayContact<T>>, T> op = new de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.KClosestNodesLookupOperation<T>(
				coord, myNode, opCallback, why, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation getRepublishOperation(
			final OperationCallback opCallback) {
		final KademliaOperation repubOp = new RepublishOperation<T>(myNode,
				opCallback, config);
		return repubOp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation getStoreOperation(final DHTObject data,
			final KademliaOverlayKey key, final boolean storeForeignData,
			final Reason why, final OperationCallback opCallback) {
		return new StoreOperation<T>(key, data, storeForeignData, myNode,
				opCallback, why, config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<List<KademliaOverlayContact<T>>> getRefreshOperation(
			final boolean forceRefresh,
			final OperationCallback<List<KademliaOverlayContact<T>>> opCallback) {
		return new BucketRefreshOperation<T>(forceRefresh, myNode, opCallback,
				config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation getBuildRoutingTableOperation(
			final OperationCallback opCallback) {
		return new RoutingTableBuildOperation<T>(myNode, opCallback, config);
	}
}
