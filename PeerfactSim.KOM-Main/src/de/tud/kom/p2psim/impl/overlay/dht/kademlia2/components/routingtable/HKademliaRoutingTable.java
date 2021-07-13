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

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.AbstractKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.HierarchyRestrictableRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTableComparators.RoutingTableEntryHierarchyComparator;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTablePredicates.MinimumClusterDepthRestrictor;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * Hierarchy-aware routing table for Kademlia. Permits to store, lookup, and
 * mark contacts as unresponsive. Several strategies for optimisation and
 * hierarchy support can be configured.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademliaRoutingTable<H extends HKademliaOverlayID> extends
		KademliaRoutingTable<H> implements HierarchyRestrictableRoutingTable<H> {

	/**
	 * Comparator defining priorities on the bucket contents used for the
	 * replacement of less prioritised contacts when adding.
	 */
	private final Comparator<RoutingTableEntry<H>> addReplacementStrategy;

	/**
	 * Constructs a new hierarchy-aware routing table with the given contact
	 * information about the owning node (it will be inserted into the routing
	 * table).
	 * 
	 * @param ownContact
	 *            the KademliaOverlayContact of the node that owns this routing
	 *            table.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HKademliaRoutingTable(final KademliaOverlayContact<H> ownContact, final RoutingTableConfig conf, AbstractKademliaNode<H> owningOverlayNode) {
		super(ownContact, conf, owningOverlayNode);
		this.addReplacementStrategy = new RoutingTableEntryHierarchyComparator<H>(pseudoRoot.getOwnID());
	}

	public HKademliaRoutingTable(final KademliaOverlayContact<H> ownContact, final RoutingTableConfig conf) {
		this(ownContact, conf, null);
	}

	/**
	 * Adds the given contact to the routing table. Adding a contact may replace
	 * another contact that belongs to a cluster that is more distant to the
	 * routing table owner's cluster than that of the new node.
	 * 
	 * @param contact
	 *            the HKademliaOverlayContact that is to be added.
	 */
	@Override
	public final void addContact(final KademliaOverlayContact<H> contact) {
		final AddNodeVisitor<H> addVis = AddNodeVisitor.getAddNodeVisitor(
				contact, addReplacementStrategy, proxHandler, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localLookup(final KademliaOverlayKey id, final int num,
			final int minDepth, final H clusterRefID,
			final Collection<KademliaOverlayContact<H>> result) {
		result.clear();
		final GenericLookupNodeVisitor<H> lookupVis = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(id, num,
						new MinimumClusterDepthRestrictor<H>(clusterRefID,
								minDepth), result);
		pseudoRoot.accept(lookupVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<H>> localLookup(
			final KademliaOverlayKey id, final int num, final int minDepth,
			final H clusterRefID) {
		localLookup(id, num, minDepth, clusterRefID, sharedResultSet);
		return sharedResultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void markUnresponsiveContact(final H id) {
		/*
		 * Use the same replacement strategy as when adding contacts to select a
		 * contact from the replacement cache
		 */
		final MarkUnresponsiveNodeVisitor<H> unresVis = MarkUnresponsiveNodeVisitor
				.getMarkUnresponsiveNodeVisitor(id, addReplacementStrategy,
						proxHandler, config);
		pseudoRoot.accept(unresVis);
	}

}
