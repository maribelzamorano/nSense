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
 * this message is used as reply for the <code>LookupMessage</code>
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupReply extends AbstractReplyMsg implements ISetupMessage {

	/*
	 * The result of the look up
	 */
	private final ChordContact responsibleContact;

	private final LookupMessage request;

	public LookupReply(ChordContact senderContact, ChordContact receiverContact,
                       ChordContact responsibleContact, LookupMessage request) {
        super(senderContact, receiverContact);
		this.responsibleContact = responsibleContact;
		this.request = request;
	}

	public ChordContact getResponsibleContact() {
		return responsibleContact;
	}

	public LookupMessage getRequest() {
		return request;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " successor "
				+ responsibleContact + " target " + request.getTarget();
	}

	@Override
	public long getSize() {
		return ChordConstant.CHORD_CONTACT_SIZE + super.getSize();
	}

}
