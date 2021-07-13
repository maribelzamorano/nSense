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
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.TypesConfig;

/**
 * Message format for data lookups.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class DataLookupMsg<T extends KademliaOverlayID> extends
		KademliaMsg<T> {

	/**
	 * The KademliaOverlayKey of the data to be looked up.
	 */
	private final KademliaOverlayKey key;

	/**
	 * Constructs a new message used to look up the data item associated with
	 * <code>dataKey</code>.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param dataKey
	 *            the KademliaOverlayKey associated with the data item to be
	 *            looked up.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public DataLookupMsg(final T sender, final T destination,
			final KademliaOverlayKey dataKey, final Reason why,
			final TypesConfig conf) {
		super(sender, destination, why, conf);
		this.key = dataKey;
	}

	/**
	 * @return the KademliaOverlayKey associated with the data to be looked up.
	 */
	public final KademliaOverlayKey getDataKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final long getOtherFieldSize() {
		return (config.getIDLength() / 8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[DataLookupMsg|from:" + getSender() + "; to:"
				+ getDestination() + "; reason:" + getReason() + "; lookup:"
				+ key + "]";
	}

}
