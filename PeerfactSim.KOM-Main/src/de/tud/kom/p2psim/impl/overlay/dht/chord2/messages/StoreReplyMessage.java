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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.messages;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConstant;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;

/**
 * This message is used in the context of the realization of the store
 * functionality, defined by the DHTNode interface. It is as reply when a
 * transmitted DHTObject was successfully stored. It includes the contact
 * information of the peer, the object was stored at.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StoreReplyMessage extends AbstractReplyMsg {

	private final ChordContact storedAt;

	public StoreReplyMessage(ChordContact senderContact,
			ChordContact receiverContact, ChordContact storedAt) {
		super(senderContact, receiverContact);
		this.storedAt = storedAt;
	}

	public ChordContact getStoredAt() {
		return storedAt;
	}

	@Override
	public long getSize() {
		return ChordConstant.CHORD_CONTACT_SIZE + super.getSize();
	}

}
