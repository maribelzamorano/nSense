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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConstant;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;

/**
 * This message is used to acknowledge a receiving event
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AckMessage extends ChordMessage implements Message, ISetupMessage {

	private int commId;

	private boolean look;

	public boolean isLook() {
		return look;
	}

	public AckMessage(ChordContact senderContact, ChordContact receiverContact,
			int commId, boolean look) {
		super(senderContact, receiverContact);
		this.commId = commId;
		this.look = look;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return ChordConstant.INT_SIZE + ChordConstant.BOOLEAN_SIZE
				+ super.getSize();
	}

	public int getCommId() {
		return commId;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " commId = " + commId;
	}

}
