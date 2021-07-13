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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaOverlayID implements OverlayID<BigInteger> {

	private BigInteger overlayID;

	public GnutellaOverlayID(BigInteger overlayID) {
		this.overlayID = overlayID;
	}

	public byte[] getBytes() {
		return null;
	}

	public BigInteger getUniqueValue() {
		return this.overlayID;
	}

	public int compareTo(OverlayID arg0) {
		return this.overlayID.compareTo((BigInteger) arg0.getUniqueValue());
	}

	public boolean equals(GnutellaOverlayID overlayID) {
		return this.overlayID
				.compareTo((BigInteger) overlayID.getUniqueValue()) == 0;
	}

	public String toString() {
		return this.overlayID.toString();
	}

	public int hashCode() {
		return this.overlayID.hashCode();
	}
}
