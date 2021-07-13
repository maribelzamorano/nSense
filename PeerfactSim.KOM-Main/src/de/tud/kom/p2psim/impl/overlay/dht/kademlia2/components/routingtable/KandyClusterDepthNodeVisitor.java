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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable;

import java.util.Map;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;

/**
 * Visitor that determines for each LeafNode which contacts are visible
 * according to Kandy's visibility rule (a contact x with cluster depth d is
 * visible iff there is no contact c != ownID with cluster depth d' &gt; d on
 * the same level or a deeper level as x in the routing tree).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class KandyClusterDepthNodeVisitor<H extends HKademliaOverlayID> extends
		AbstractNodeVisitor<H> {

	/**
	 * Single, final KandyClusterDepthNodeVisitor instance (non-reentrant).
	 */
	private static final KandyClusterDepthNodeVisitor singleton = new KandyClusterDepthNodeVisitor();

	/**
	 * A Map with Nodes as keys and the permitted (exact) cluster depth for
	 * contacts that are visible from that LeafNode.
	 */
	private Map<Node<H>, Integer> clusterDepths;

	/**
	 * Determine for each bucket which contacts are visible in a Kandy routing
	 * table.
	 * 
	 * @param clusterDepths
	 *            a Map in which the cluster depths will be saved. It will be
	 *            cleared.
	 * @return an KandyClusterDepthNodeVisitor instance. Note that this instance
	 *         is statically shared among all clients of this class. That is, at
	 *         runtime only one KandyClusterDepthNodeVisitor instance exists.
	 *         Thus, it is non-reentrant and should not be saved by clients
	 *         (should used immediately).
	 */
	protected static final <H extends HKademliaOverlayID> KandyClusterDepthNodeVisitor<H> getKandyClusterDepthNodeVisitor(
			final Map<Node<H>, Integer> clusterDepths) {
		singleton.clusterDepths = clusterDepths;
		singleton.clusterDepths.clear();
		return singleton;
	}

	private KandyClusterDepthNodeVisitor() {
		// should not be called externally
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final BranchNode<H> node) {
		int currentDepth = 0, maxDepth = 0;

		// calculate depth for each child
		for (final Node<H> child : node.children.values()) {
			child.accept(this);
		}

		// calculate maximum depth of node's children
		for (final Node<H> child : node.children.values()) {
			if ((currentDepth = clusterDepths.get(child)) > maxDepth) {
				maxDepth = currentDepth;
			}
		}

		// set depth of node and its direct children to max. depth
		for (final Node<H> child : node.children.values()) {
			clusterDepths.put(child, maxDepth);
		}
		clusterDepths.put(node, maxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final LeafNode<H> node) {
		// calculate maximum depth of all contacts in bucket except own ID
		int maxDepth = 0, currentDepth = 0;
		for (final H contactID : node.kBucket.keySet()) {
			if (!contactID.equals(node.getOwnID())
					&& (currentDepth = contactID.getCommonClusterDepth(node
							.getOwnID())) > maxDepth) {
				maxDepth = currentDepth;
			}
		}
		clusterDepths.put(node, maxDepth);
	}

}
