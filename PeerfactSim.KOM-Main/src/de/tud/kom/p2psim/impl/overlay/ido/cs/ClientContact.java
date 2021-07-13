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

package de.tud.kom.p2psim.impl.overlay.ido.cs;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Provides a Container for id, ip and port for a Client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientContact implements OverlayContact<ClientID> {

	/**
	 * The identifier of a node.
	 */
	private final ClientID clientID;

	/**
	 * Contains the information to contact the overlay node.
	 */
	private final TransInfo transInfo;

	/**
	 * Constructor of this class. Sets the ClientID and transInfo.
	 * 
	 * @param id
	 *            The ID of a node in the overlay.
	 * @param transInfo
	 *            The contact information of a node in the overlay.
	 */
	public ClientContact(ClientID id, TransInfo transInfo) {
		this.clientID = id;
		this.transInfo = transInfo;
	}

	@Override
	public ClientID getOverlayID() {
		return clientID;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public String toString() {
		return "[clientID=" + clientID + " transinfo=" + transInfo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClientContact) {
			ClientContact o = (ClientContact) obj;

			return this.clientID.equals(o.clientID)
					&& this.transInfo.equals(o.transInfo);
		}
		return false;
	}
}
