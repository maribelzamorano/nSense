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

import de.tu.darmstadt.dvs.nSense.overlay.util.Constants;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class encapsulates a nSenseID and the TransInfo of a node. It contains
 * the needed information about a node in pSense for the contact.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 */
public class NSenseContact implements OverlayContact<NSenseID>, Transmitable {

	/**
	 * The identifier of a node.
	 */
	private final NSenseID nSenseID;

	/**
	 * Contains the information to contact the overlay node.
	 */
	private final TransInfo transInfo;

	/**
	 * Constructor of this class. Sets the nSenseID and transInfo.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 * @param transInfo
	 *            The contact information of a node in the overlay.
	 */
	public NSenseContact(NSenseID id, TransInfo transInfo) {
		this.nSenseID = id;
		this.transInfo = transInfo;
	}

	@Override
	public NSenseID getOverlayID() {
		return nSenseID;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public int getTransmissionSize() {
		return nSenseID.getTransmissionSize() + Constants.BYTE_SIZE_OF_IP
				+ Constants.BYTE_SIZE_OF_PORT;
	}

	@Override
	public String toString() {
		return "[nSenseID=" + nSenseID + " transinfo=" + transInfo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSenseContact) {
			NSenseContact o = (NSenseContact) obj;

			return this.nSenseID.equals(o.nSenseID)
					&& this.transInfo.equals(o.transInfo);
		}
		return false;
	}

}
