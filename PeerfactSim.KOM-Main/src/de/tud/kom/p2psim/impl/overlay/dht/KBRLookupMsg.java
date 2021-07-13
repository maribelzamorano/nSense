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


package de.tud.kom.p2psim.impl.overlay.dht;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBRLookupMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * This message is used to perform lookups for nodes that are responsible for a
 * given <code>OverlayKey</code>.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * 
 * @version 05/06/2011
 */
public class KBRLookupMsg<T extends OverlayID, S extends OverlayContact<T>>
		implements KBRLookupMessage {

	/**
	 * The contact data of the sender
	 */
	private final S senderContact;

	/**
	 * The key that is looked up - used for identification of open lookup
	 * requests
	 */
	private final int operationID;

	/**
	 * @param senderContact
	 *            the contact information of the sender
	 * @param operationID
	 *            the ID of the operation that triggered the lookup
	 */
	public KBRLookupMsg(S senderContact, int operationID) {
		this.senderContact = senderContact;
		this.operationID = operationID;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		/*
		 * senderContact (type: OverlayContact) + operationID (type: int) =
		 * OverlayID (160 bit) + TransInfo (32 bit (IP-address) + 16 (Port) +
		 * int (32 bit) = 240 bit = 30 byte
		 */
		return 30;
	}

	/**
	 * @return the contact information of the sender
	 */
	public S getSenderContact() {
		return senderContact;
	}

	/**
	 * @return the ID of the operation that triggered the lookup
	 */
	public int getOperationID() {
		return operationID;
	}
}
