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

import java.util.ArrayList;
import java.util.Collection;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.TypesConfig;

/**
 * Format for messages that carry up to k nodes (in response to a node / data /
 * bucket lookup).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class NodeListMsg<T extends KademliaOverlayID> extends
		KademliaMsg<T> {

	/**
	 * Up to {@link TypesConfig#getBucketSize()} nodes in a list.
	 */
	private final Collection<KademliaOverlayContact<T>> kNodes;

	/**
	 * Constructs a new message that may carry a list of up to
	 * {@link TypesConfig#getBucketSize()} nodes.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param kNodes
	 *            a Collection of up to K KademliaOverlayContacts. This
	 *            collection will be copied.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public NodeListMsg(final T sender, final T destination,
			final Collection<KademliaOverlayContact<T>> kNodes,
			final Reason why, final TypesConfig conf) {
		super(sender, destination, why, conf);
		this.kNodes = new ArrayList<KademliaOverlayContact<T>>(kNodes);
	}

	/**
	 * @return the list of nodes that is saved in this message.
	 */
	public final Collection<KademliaOverlayContact<T>> getNodes() {
		return this.kNodes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final long getOtherFieldSize() {
		return kNodes.size() * (config.getIDLength() / 8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[NodeListMsg|from:" + getSender() + "; to:" + getDestination()
				+ "; reason:" + getReason() + "; contents:" + kNodes + "]";
	}
}
