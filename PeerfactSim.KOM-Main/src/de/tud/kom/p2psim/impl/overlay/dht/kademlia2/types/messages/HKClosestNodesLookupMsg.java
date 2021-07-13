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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.TypesConfig;

/**
 * Format for a message that requests the k closest known nodes around a given
 * key and restricts the contacts that may be returned to have a given minimum
 * common cluster depth with the sender of this message.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class HKClosestNodesLookupMsg<H extends HKademliaOverlayID>
		extends KClosestNodesLookupMsg<H> {

	/**
	 * The minimum depth of the deepest common cluster between each returned
	 * contact and the sender's ID.
	 */
	private final int minClusterDepth;

	/**
	 * Constructs a new message used to look up the k closest known neighbours
	 * of <code>nodeKey</code> that have at least the cluster depth
	 * <code>minClusterDepth</code> with respect to the sender's ID.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param dataKey
	 *            the KademliaOverlayKey of the node of which the k closest
	 *            known neighbours are to be returned.
	 * @param minClusterDepth
	 *            the required minimum depth of the deepest common cluster
	 *            between each returned contact and the sender's ID.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public HKClosestNodesLookupMsg(final H sender, final H destination,
			final KademliaOverlayKey nodeKey, final int minClusterDepth,
			final Reason why, final TypesConfig conf) {
		super(sender, destination, nodeKey, why, conf);
		this.minClusterDepth = minClusterDepth;
	}

	/**
	 * @return the minimum required depth of the deepest common cluster between
	 *         each returned contact and the ID of the sender of this message.
	 */
	public final int getMinClusterDepth() {
		return minClusterDepth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final long getOtherFieldSize() {
		return super.getOtherFieldSize() + 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[HKClosestNodesLookupMsg|from:" + getSender() + "; to:"
				+ getDestination() + "; reason:" + getReason() + "; lookup:"
				+ getNodeKey() + "; minClusterDepth=" + minClusterDepth + "]";
	}

}
