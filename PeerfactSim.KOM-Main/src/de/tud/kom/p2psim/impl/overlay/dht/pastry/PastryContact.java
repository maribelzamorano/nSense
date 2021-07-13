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


package de.tud.kom.p2psim.impl.overlay.dht.pastry;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.Transmitable;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * This class represents the overlay contact for pastry.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryContact implements OverlayContact<PastryID>, Transmitable {

	private PastryID id;

	private TransInfo transinfo;

	/**
	 * @param id
	 *            the peer's ID
	 * @param transinfo
	 *            the peer's transport information
	 */
	public PastryContact(PastryID id, TransInfo transinfo) {
		this.id = id;
		this.transinfo = transinfo;
	}

	@Override
	public PastryID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transinfo;
	}

	@Override
	public int getTransmissionSize() {
		// size = sizeOfId + sizeOfTransinfo = sizeOfId + (4 +2)
		return id.getTransmissionSize() + 4 + 2;
	}

	@Override
	public String toString() {
		return id.toString();
		// return "Contact[OID=" + this.id + ", TransAddr=" + this.transinfo +
		// "]";
	}

	// Implementation of equals and hashCode

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		PastryContact c = (PastryContact) obj;
		return id.equals(c.id) && (transinfo.equals(c.transinfo));
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == id ? 0 : id.hashCode());
		hash = 31 * hash + (null == transinfo ? 0 : transinfo.hashCode());
		return hash;
	}

}
