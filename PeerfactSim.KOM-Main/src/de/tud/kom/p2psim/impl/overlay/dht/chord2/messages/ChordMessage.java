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
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConstant;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class ChordMessage extends AbstractOverlayMessage<ChordID> implements Message {
    private ChordContact senderContact, receiverContact;

    public ChordMessage(ChordContact senderContact, ChordContact receiverContact) {
		super(senderContact.getOverlayID(), receiverContact.getOverlayID());
		this.senderContact = senderContact;
		this.receiverContact = receiverContact;
    }

	public ChordContact getSenderContact() {
		return senderContact;
	}

	public ChordContact getReceiverContact() {
		return receiverContact;
	}

    @Override
    public long getSize() {
		return 2 * ChordConstant.CHORD_CONTACT_SIZE;
    }

    @Override
    public abstract Message getPayload();
}
