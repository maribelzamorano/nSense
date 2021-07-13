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

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.util.toolkits.Predicate;

/**
 * Predicates that are exclusively used in Kademlia routing tables.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class RoutingTablePredicates {

	/**
	 * A predicate that holds only for KademliaOverlayContacts (contained in
	 * RoutingTableEntries) that have a minimum cluster similarity with a preset
	 * value.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	static class MinimumClusterDepthRestrictor<H extends HKademliaOverlayID>
			implements Predicate<RoutingTableEntry<H>> {

		/**
		 * The reference identifier.
		 */
		private final H reference;

		/**
		 * The minimum "common cluster depth" with <code>reference</code>.
		 */
		private final int minDepth;

		/**
		 * Constructs a predicate that holds for all those contacts (contained
		 * in RoutingTableEntries) that share a "common cluster depth" of at
		 * least <code>minDepth</code> with contact <code>reference</code>.
		 * 
		 * @param reference
		 *            the reference HKademliaOverlayID that defines the
		 *            "perfect" cluster that other contacts should have (or
		 *            approach).
		 * @param minDepth
		 *            the minimum depth of a cluster that a contact needs to
		 *            have in common with <code>reference</code>. 0 effectively
		 *            allows all contacts as all contacts share a common cluster
		 *            with depth 0.
		 */
		public MinimumClusterDepthRestrictor(final H reference,
				final int minDepth) {
			this.reference = reference;
			this.minDepth = minDepth;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean isTrue(final RoutingTableEntry<H> object) {
			if (reference.getCommonClusterDepth(object.getContact()
					.getOverlayID()) >= minDepth) {
				return true;
			}
			return false;
		}

	}

}
