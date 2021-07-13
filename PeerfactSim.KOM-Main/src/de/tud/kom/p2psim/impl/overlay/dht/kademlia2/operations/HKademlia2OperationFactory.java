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
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.HKademlia2Node;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.ClusterPreferenceLookupCoordinator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.LookupCoordinator.NonhierarchicalLookupCoordinator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * Operations as used in hierarchical Kademlia, that is mainly hierarchy-aware
 * routing. Instead of a customised bucket refresh lookup, a standard
 * (hierarchical) k closest nodes lookup is used.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademlia2OperationFactory<H extends HKademliaOverlayID> extends
		AbstractOperationFactory<H> {

	/**
	 * The node that owns this operation factory
	 */
	private final HKademlia2Node<H> myNode;

	/**
	 * Constructs a new hierarchical Kademlia operation factory for the given
	 * HKademliaNode.
	 * 
	 * @param myNode
	 *            the HKademlia2Node that owns this operation factory.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HKademlia2OperationFactory(final HKademlia2Node<H> myNode,
			final OperationsConfig conf) {
		super(conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<List<KademliaOverlayContact<H>>> getBucketLookupOperation(
			final KademliaOverlayKey key, final int bucketDepth,
			final OperationCallback<List<KademliaOverlayContact<H>>> opCallback) {
		return getKClosestNodesLookupOperation(key, Reason.MAINTENANCE,
				opCallback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final DataLookupOperation<DHTObject> getDataLookupOperation(
			final KademliaOverlayKey key,
			final OperationCallback<DHTObject> opCallback) {
		/*
		 * Although HKademliaOverlayIDs are used, no use is made of hierarchical
		 * lookup messages (with restriction to minimum common cluster depth)
		 * --> use NonhierarchicalLookupCoordinator
		 */
		final NonhierarchicalLookupCoordinator<H> coord = new ClusterPreferenceLookupCoordinator<H>(
				key, myNode, config);
		final DataLookupOperation<DHTObject> op = new de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.DataLookupOperation<H>(
				coord, myNode, opCallback, Reason.USER_INITIATED, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeLookupOperation<List<KademliaOverlayContact<H>>, H> getKClosestNodesLookupOperation(
			KademliaOverlayKey key, Reason why,
			OperationCallback<List<KademliaOverlayContact<H>>> opCallback) {
		/*
		 * Although HKademliaOverlayIDs are used, no use is made of hierarchical
		 * lookup messages (with restriction to minimum common cluster depth)
		 * --> use NonhierarchicalLookupCoordinator
		 */
		final NonhierarchicalLookupCoordinator<H> coord = new ClusterPreferenceLookupCoordinator<H>(
				key, myNode, config);
		final NodeLookupOperation<List<KademliaOverlayContact<H>>, H> op = new de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.KClosestNodesLookupOperation<H>(
				coord, myNode, opCallback, why, config);
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation getRepublishOperation(
			final OperationCallback opCallback) {
		final KademliaOperation repubOp = new RepublishOperation<H>(myNode,
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
		final KademliaOperation storeOp = new StoreOperation<H>(key, data,
				storeForeignData, myNode, opCallback, why, config);
		return storeOp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation<List<KademliaOverlayContact<H>>> getRefreshOperation(
			final boolean forceRefresh,
			final OperationCallback<List<KademliaOverlayContact<H>>> opCallback) {
		return new BucketRefreshOperation<H>(forceRefresh, myNode, opCallback,
				config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final KademliaOperation getBuildRoutingTableOperation(
			final OperationCallback opCallback) {
		return new RoutingTableBuildOperation<H>(myNode, opCallback, config);
	}

}
