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

import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;

/**
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 02/05/2011
 *
 */
public class ValueLookupReplyMsg extends PastryBaseMsg {

	private PastryContact senderContact;

	private DHTObject value;

	private int operationID;

	public ValueLookupReplyMsg(PastryContact sender, PastryID receiver,
			DHTObject value, int operationID) {
		super(sender.getOverlayID(), receiver);
		this.value = value;
		this.operationID = operationID;
	}

	@Override
	public long getSize() {
		// TODO add the size of the DHTObject
		return super.getSize() + senderContact.getTransmissionSize() + 4;
	}

	public PastryContact getSenderContact() {
		return senderContact;
	}

	public DHTObject getValue() {
		return value;
	}

	public int getOperationID() {
		return operationID;
	}

}
