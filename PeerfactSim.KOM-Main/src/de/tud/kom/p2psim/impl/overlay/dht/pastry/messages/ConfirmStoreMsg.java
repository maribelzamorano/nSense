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

import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConfirmStoreMsg extends PastryBaseMsg {

	private PastryContact senderContact;

	private Set<PastryContact> storingPeers;

	private int operationID;

	public ConfirmStoreMsg(PastryContact sender, PastryContact receiver,
			Set<PastryContact> storingPeers, int operationID) {
		super(sender.getOverlayID(), receiver.getOverlayID());
		this.senderContact = sender;
		this.storingPeers = storingPeers;
		this.operationID = operationID;
	}

	@Override
	public long getSize() {
		return super.getSize() + 4 + senderContact.getTransmissionSize()
				* (storingPeers.size() + 1);
	}

	public PastryContact getSenderContact() {
		return senderContact;
	}

	public Set<PastryContact> getStoringPeers() {
		return storingPeers;
	}

	public int getOperationID() {
		return operationID;
	}

}
