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



package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * In order to enable the communication between OverlayNodes, specific contact
 * information are required. Thus, this class contains information about the
 * transport address (IP address, UDP port) and the KademliaOverlayID of a
 * AbstractKademliaOverlayNode.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class KademliaOverlayContact<T extends KademliaOverlayID>
		implements OverlayContact<T> {

	private final T kademliaId;

	private final TransInfo transAddr;

	/**
	 * Constructs a contact with the specified transport address and overlay
	 * identifier. This constructor should be used for foreign nodes.
	 */
	public KademliaOverlayContact(final T overlayID, final TransInfo addr) {
		this.kademliaId = overlayID;
		this.transAddr = addr;
	}

	/**
	 * Returns the overlay identifier of this contact.
	 */
	public final T getOverlayID() {
		return this.kademliaId;
	}

	/**
	 * Returns the transport address of this contact.
	 */
	public final TransInfo getTransInfo() {
		return this.transAddr;
	}

	/**
	 * Returns <code>true</code> if <code>o</code> is a
	 * <code>KademliaOverlayContact</code> and has the same kademlia overlay
	 * identifier as this. Note that transport address is ignored in this
	 * comparison.
	 */
	@Override
	public final boolean equals(final Object o) {
		if (o instanceof KademliaOverlayContact) {
			return this.kademliaId
					.equals(((KademliaOverlayContact<?>) o).kademliaId);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return kademliaId.hashCode();
	}

	@Override
	public final String toString() {
		return "Contact[OID=" + this.kademliaId + ", TransAddr="
				+ this.transAddr + "]";
	}

}
