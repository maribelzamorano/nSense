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

import java.util.Comparator;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.ProximityListener;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.util.toolkits.KSmallestMap;
import de.tud.kom.p2psim.impl.util.toolkits.KSortedLookupList;
import de.tud.kom.p2psim.impl.util.toolkits.Comparators.KademliaOverlayIDXORMaxComparator;

/**
 * Handles events related to nodes joining or leaving the set of the
 * {@link RoutingTableConfig#getBucketSize()} closest neighbours of the routing
 * table owner's ID.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class ProximityHandler<T extends KademliaOverlayID> {

	/**
	 * Data structure that contains the k closest <code>KademliaOverlayID</code>
	 * s. Sorted by increasing distance to this routing table owner's ID.
	 */
	private final KSortedLookupList<T, Object> kClosestNodes;

	/**
	 * A listener interested in join-events triggered by new contacts becoming
	 * part of this routing table owner's K closest neighbours.
	 */
	private ProximityListener<T> proxListener;

	/**
	 * Constructs a new ProximityHandler that keeps track of a node's K closest
	 * neighbours and informs a proximity listener if a new node joins these
	 * neighbours (which includes the case that a contact leaves the overlay
	 * network and a new, more distant node becomes part of the K closest
	 * neighbours of this node).
	 * 
	 * @param ownID
	 *            the KademliaOverlayID of the node of which the K closest
	 *            neighbours are to be observed.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	protected ProximityHandler(final T ownID, final RoutingTableConfig conf) {
		final Comparator<T> xorDist = new KademliaOverlayIDXORMaxComparator<T>(
				ownID.getBigInt());
		this.kClosestNodes = new KSmallestMap<T, Object>(conf.getBucketSize(),
				xorDist);
	}

	/**
	 * Registers <code>newListener</code> as listener for join-events triggered
	 * by new contacts becoming part of the {@KademliaConfig#K
	 * 
	 * 
	 * } closest nodes around this routing table owner's own
	 * ID.
	 * 
	 * @param newListener
	 *            the ProximityListener that is to be notified whenever a new
	 *            node becomes part of this routing table owner's K closest
	 *            neighbours.
	 */
	protected final void registerProximityListener(
			de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.ProximityListener<T> newListener) {
		// currently only one listener is supported
		proxListener = newListener;
	}

	/**
	 * Called whenever a contact is inserted into a bucket. This method should
	 * be called if a contact has been saved in a LeafNode's kBucket. This can
	 * happen if the contact has been seen or if it replaces another contact
	 * that has been deleted from the bucket.
	 * 
	 * @param newContact
	 *            the new KademliaOverlayContact that has been added to a
	 *            bucket.
	 */
	protected final void contactAdded(final KademliaOverlayContact<T> newContact) {
		// put with null value, put only if not already known
		final boolean added = kClosestNodes.put(newContact.getOverlayID(),
				null, false);

		if (added && proxListener != null) {
			// a contact has been added: notify listener
			proxListener.newCloseContactArrived(newContact);
		}
	}

	/**
	 * Called when a contact is removed from a bucket (for instance, after
	 * having been marked as unresponsive).
	 * 
	 * @param id
	 *            the KademliaOverlayID of the contact that has been deleted
	 *            from a bucket.
	 */
	protected final void contactRemoved(final T id) {
		kClosestNodes.remove(id);
	}

}
