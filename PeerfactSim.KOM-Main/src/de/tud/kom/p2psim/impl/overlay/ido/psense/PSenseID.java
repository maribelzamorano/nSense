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

package de.tud.kom.p2psim.impl.overlay.ido.psense;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class represent the overlay ID of a node in pSense. It is used in pSense
 * to identifier a node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class PSenseID implements OverlayID, Transmitable {

	/**
	 * An empty overlay ID in pSense.
	 */
	public final static PSenseID EMPTY_PSENSE_ID = new PSenseID(-1);

	/**
	 * Not changeable overlayID for this instance. It describes a unique
	 * identifier for a node in the overlay.
	 */
	private final int overlayID;

	/**
	 * Constructor of this class. It sets the overlayID.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 */
	public PSenseID(int id) {
		this.overlayID = id;
	}

	@Override
	public int compareTo(Object id) {
		return new Integer(overlayID).compareTo(((PSenseID) id)
				.getUniqueValue());
	}

	@Override
	public int getTransmissionSize() {
		// returns the number of bytes
		return getBytes().length;
	}

	@Override
	public Integer getUniqueValue() {
		return this.overlayID;
	}

	@Override
	public byte[] getBytes() {

		// Convert integer to byte array
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
		if (obj instanceof PSenseID) {
			PSenseID o = (PSenseID) obj;

			return this.overlayID == o.overlayID;
		}
		return false;
	}
}
