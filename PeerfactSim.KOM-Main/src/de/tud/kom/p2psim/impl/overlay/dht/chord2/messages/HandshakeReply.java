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

import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConstant;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;

/**
 * Reply message for <code>HandshakeMsg</code>
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HandshakeReply extends AbstractReplyMsg implements
		IStabilizeMessage {

	private final ChordContact predecessor;

	private final Set<ChordContact> availableContacts;

	public HandshakeReply(ChordContact senderContact,
			ChordContact receiverContact, ChordContact predecessor,
			Set<ChordContact> availableContacts) {
		super(senderContact, receiverContact);
		this.predecessor = predecessor;
		this.availableContacts = availableContacts;
	}

	public ChordContact getPredecessor() {
		return predecessor;
	}

	public Set<ChordContact> getAvailableContacts() {
		return availableContacts;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " predecessor " + predecessor;
	}

	@Override
	public long getSize() {
		return (availableContacts.size() + 1)
				* ChordConstant.CHORD_CONTACT_SIZE + +super.getSize();
	}
}
