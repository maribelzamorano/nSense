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

package de.tud.kom.p2psim.impl.overlay.ido.von;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class represents the IDs used by VON, which at the moment simply is an
 * integer.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonID implements OverlayID, Transmitable {

	public final static VonID EMPTY_ID = new VonID(-1);

	private final int overlayID;

	public VonID(int id) {
		overlayID = id;
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
	public Integer getUniqueValue() {
		return overlayID;
	}

	@Override
	public int compareTo(Object id) {
		return new Integer(overlayID).compareTo(((VonID) id).getUniqueValue());
	}

	@Override
	public int getTransmissionSize() {
		// size = siezOfInt = 4byte
		return 4;
	}

	@Override
	public String toString() {
		return overlayID + "";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VonID) {
			VonID o = (VonID) obj;

			return this.overlayID == o.overlayID;
		}
		return false;
	}
}
