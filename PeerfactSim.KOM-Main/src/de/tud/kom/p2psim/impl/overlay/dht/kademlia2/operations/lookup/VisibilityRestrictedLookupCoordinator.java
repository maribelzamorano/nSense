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

import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.AbstractKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node.VisibilityRestrictableNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.OperationsConfig;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * A lookup coordinator that initially fills the set of the k closest known
 * nodes with a visibility restricted lookup to the local routing table.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class VisibilityRestrictedLookupCoordinator<H extends HKademliaOverlayID>
		extends StandardLookupCoordinator<H> {

	/**
	 * The VisibilityRestrictableNode that has initiated this lookup.
	 */
	protected final VisibilityRestrictableNode<H> myNode;

	/**
	 * Constructs a new lookup coordinator that carries out visibility
	 * restricted local lookups.
	 * 
	 * @param lookupKey
	 *            the KademliaOverlayKey that is to be looked up.
	 * @param node
	 *            the VisibilityRestrictableNode that initiates this lookup.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public VisibilityRestrictedLookupCoordinator(
			final KademliaOverlayKey lookupKey,
			final VisibilityRestrictableNode<H> node,
			final OperationsConfig conf) {
		super(lookupKey, node, conf);
		myNode = node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<H>> localLookup(final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<H>) getNode()).getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable().visibilityRestrictedLocalLookup(key, localBucketSize);
	}
}
