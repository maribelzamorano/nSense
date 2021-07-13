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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;

/**
 * This is the abstract class to all pastry messages.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class PastryBaseMsg extends AbstractOverlayMessage<PastryID> {

	/**
	 * This is the minimal size of a message in pastry. A message that does not
	 * contain any fields (e.g. the AckMsg) has at least this size.
	 */
	private final static int MIN_HEADER_SIZE = 1;

	/**
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 */
	public PastryBaseMsg(PastryID sender, PastryID receiver) {
		super(sender, receiver);
	}

	@Override
	public long getSize() {
		/*
		 * Determine the size dependent on the size of the IDs of the fields
		 * sender and receiver.
		 */

		long size = 0;
		PastryID sender = getSender();
		PastryID receiver = getReceiver();

		if (sender != null)
			size += sender.getTransmissionSize();
		if (receiver != null)
			size += receiver.getTransmissionSize();

		if (size == 0)
			return MIN_HEADER_SIZE;
		return size;
	}

	@Override
	public Message getPayload() {
		// There is no payload message
		return null;
	}
}
