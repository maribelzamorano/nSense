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

package de.tu.darmstadt.dvs.nSense.overlay;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class represent the overlay ID of a node in nSense. It is used in nSense
 * to identifier a node.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 */
public class NSenseID implements OverlayID, Transmitable {

	/**
	 * An empty overlay ID in nSense.
	 */
	public final static NSenseID EMPTY_NSENSE_ID = new NSenseID(-1);

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
	public NSenseID(int id) {
		this.overlayID = id;
	}

	@Override
	public int compareTo(Object id) {
		return new Integer(overlayID).compareTo(((NSenseID) id)
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
		if (obj instanceof NSenseID) {
			NSenseID o = (NSenseID) obj;

			return this.overlayID == o.overlayID;
		}
		return false;
	}
}
