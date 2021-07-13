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

package de.tud.kom.p2psim.impl.overlay.ido.cs.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientID;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.MSG_TYPE;

/**
 * The leave message from the client to the server. It contains only the ID of
 * the client, which will be leave.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class LeaveMessage extends CSAbstractMessage {

	/**
	 * The id of the leaving client.
	 */
	private ClientID clientID;

	/**
	 * Sets the id in the message, of the leaving client.
	 * 
	 * @param id
	 *            The ID of the leaving client.
	 */
	public LeaveMessage(ClientID id) {
		super(MSG_TYPE.LEAVE_MESSAGE);
		this.clientID = id;
	}

	@Override
	public long getSize() {
		return super.getSize() + clientID.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the ID of the leaving client.
	 * 
	 * @return The clientID.
	 */
	public ClientID getClientID() {
		return clientID;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LeaveMessage) {
			LeaveMessage l = (LeaveMessage) o;
			return this.clientID.equals(l.clientID) && super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientID: ");
		temp.append(getClientID());
		temp.append(" ]");
		return temp.toString();
	}

}
