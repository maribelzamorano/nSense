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


package de.tud.kom.p2psim.impl.overlay.dht.pastry.messages;

import java.util.Collection;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;

/**
 * This class represents a message that is send as a reply to JoinMsgs. It
 * contains a flag to tell whether this reply was sent by the peer numerically
 * closest to the new peer's ID or not, as well as a list of peer contacts to be
 * inserted in the joining peer's state tables.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StateUpdateMsg extends PastryBaseMsg {

	private Collection<PastryContact> contacts;

	private long leafSetTimestamp = -1;

	/**
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param contacts
	 *            the list of peer contacts to be inserted into the receiving
	 *            peer's state tables
	 */
	public StateUpdateMsg(PastryID sender, PastryID receiver,
			Collection<PastryContact> contacts) {
		super(sender, receiver);
		this.contacts = contacts;
	}

	public StateUpdateMsg(PastryID sender, PastryID receiver,
			Collection<PastryContact> contacts, long leafSetTimestamp) {
		super(sender, receiver);
		this.contacts = contacts;
		this.leafSetTimestamp = leafSetTimestamp;
	}

	/**
	 * @return the list of contacts to be used by the joining peer to insert
	 *         into its state tables
	 */
	public Collection<PastryContact> getContacts() {
		return contacts;
	}

	@Override
	public Message getPayload() {
		// There is no payload message
		return null;
	}

	/**
	 * @return get the timestamp of the message creation
	 */
	public long getLeafSetTimestamp() {
		return leafSetTimestamp;
	}

	@Override
	public long getSize() {
		// size = sizeOfSuper + sizeOfContacts
		long size = super.getSize();
		if (contacts.size() > 0)
			size += contacts.iterator().next().getTransmissionSize()
					* contacts.size();

		return size;
	}
}
