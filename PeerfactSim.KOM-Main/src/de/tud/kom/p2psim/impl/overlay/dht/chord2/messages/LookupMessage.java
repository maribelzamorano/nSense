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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConstant;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This message is used to find the responder for specified key
 *  
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class LookupMessage extends AbstractRequestMessage implements
		IServiceMessage {

	private static Logger log = SimLogger.getLogger(LookupMessage.class);

	
	private final int lookupId;

	private ChordID target;
	
	/** 
	 * Notice
	 * @param senderContact : address of the lookup starter (do not always mean sender)
	 */
	public LookupMessage(ChordContact senderContact,
			ChordContact receiverContact, ChordID target, int lookupId, int hop) {
		super(senderContact, receiverContact);
		this.lookupId = lookupId;
		this.target = target;
		setHop(hop);
		log.trace("init LookupMessage id = " + lookupId);
	}


	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return ChordConstant.CHORD_ID_SIZE + ChordConstant.INT_SIZE
				+ super.getSize();
	}

	public int getLookupID() {
		return lookupId;
	}

	public ChordID getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
		+ " sender = " + getSender()
		+ " receiver = " + getReceiver()
		+ " target = " + target
		+ " id = " + lookupId;
	}
}
