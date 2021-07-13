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

import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryKey;

/**
 * This is the message send to join an overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinMsg extends PastryBaseMsg {

	private PastryKey key;

	private PastryContact senderContact;

	/**
	 * @param sender
	 *            the initial sender of the message
	 */
	public JoinMsg(PastryContact sender) {
		super(null, null);
		this.senderContact = sender;
		this.key = sender.getOverlayID().getCorrespondingKey();
	}

	@Override
	public long getSize() {
		// Size = SizeOfSuper + SizeOfKey
		return super.getSize() + key.getTransmissionSize();
	}

	/**
	 * @return the key this message is routed towards
	 */
	public PastryKey getKey() {
		return key;
	}

	/**
	 * @return the contact of the initial sender of this message
	 */
	public PastryContact getSenderContact() {
		return senderContact;
	}

}
