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


package de.tud.kom.p2psim.impl.overlay.gnutella.api;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Contact information of a peer participating in a Gnutella overlay.
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaLikeOverlayContact implements
		OverlayContact<GnutellaOverlayID> {

	TransInfo transInfo;

	GnutellaOverlayID id;

	public GnutellaLikeOverlayContact(GnutellaOverlayID id,
			TransInfo transInfo) {
		this.id = id;
		this.transInfo = transInfo;
	}

	/**
	 * Returns the overlay ID of the given Gnutella06 node
	 */
	@Override
	public GnutellaOverlayID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	public String toString() {
		return id.toString();
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof GnutellaLikeOverlayContact)) return false;
		return id.equals(((GnutellaLikeOverlayContact) o).id);
	}

	/**
	 * Returns the size of this contact information.
	 * @return
	 */
	public int getSize() {
		return 8; // TransInfo: 4, ID: 4
	}

}
