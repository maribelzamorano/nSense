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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * This class provides an identifier for the mercury overlay. Every peer has an
 * identifier. So it is possible to associate a received information with a
 * peer.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class MercuryIDOID implements OverlayID<Integer> {

	/**
	 * An empty identifier
	 */
	public final static MercuryIDOID EMPTY_ID = new MercuryIDOID(-1);

	/**
	 * The identifier as integer
	 */
	private final int overlayID;

	/**
	 * Create a Mercury IDO id with the given id.
	 * 
	 * @param id
	 *            The identifier for this instance
	 */
	public MercuryIDOID(int id) {
		overlayID = id;
	}

	@Override
	public Integer getUniqueValue() {
		return overlayID;
	}

	@Override
	public byte[] getBytes() {
		/*
		 * Convert the integer to an byte array
		 */
		byte[] buffer = new byte[4];
		buffer[0] = (byte) (overlayID >> 24);
		buffer[1] = (byte) ((overlayID << 8) >> 24);
		buffer[2] = (byte) ((overlayID << 16) >> 24);
		buffer[3] = (byte) ((overlayID << 24) >> 24);

		return buffer;
	}

	@Override
	public String toString() {
		return new Integer(overlayID).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MercuryIDOID) {
			MercuryIDOID o = (MercuryIDOID) obj;

			return this.overlayID == o.overlayID;
		}
		return false;
	}

	@Override
	public int compareTo(OverlayID id) {
		return new Integer(overlayID).compareTo(((MercuryIDOID) id)
				.getUniqueValue());
	}

}
