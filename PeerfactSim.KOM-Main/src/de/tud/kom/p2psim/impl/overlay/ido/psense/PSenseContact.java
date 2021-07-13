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

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.Constants;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class encapsulates a pSenseID and the TransInfo of a node. It contains
 * the needed information about a node in pSense for the contact.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class PSenseContact implements OverlayContact<PSenseID>, Transmitable {

	/**
	 * The identifier of a node.
	 */
	private final PSenseID pSenseID;

	/**
	 * Contains the information to contact the overlay node.
	 */
	private final TransInfo transInfo;

	/**
	 * Constructor of this class. Sets the pSenseID and transInfo.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 * @param transInfo
	 *            The contact information of a node in the overlay.
	 */
	public PSenseContact(PSenseID id, TransInfo transInfo) {
		this.pSenseID = id;
		this.transInfo = transInfo;
	}

	@Override
	public PSenseID getOverlayID() {
		return pSenseID;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public int getTransmissionSize() {
		// size = sizeOfpSenseID + sizeOfTransInfo = sizeOfpSenseID + (sizeOfIP
		// + sizeOfPort)
		return pSenseID.getTransmissionSize() + Constants.BYTE_SIZE_OF_IP
				+ Constants.BYTE_SIZE_OF_PORT;
	}

	@Override
	public String toString() {
		return "[pSenseID=" + pSenseID + " transinfo=" + transInfo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PSenseContact) {
			PSenseContact o = (PSenseContact) obj;

			return this.pSenseID.equals(o.pSenseID)
					&& this.transInfo.equals(o.transInfo);
		}
		return false;
	}

}
