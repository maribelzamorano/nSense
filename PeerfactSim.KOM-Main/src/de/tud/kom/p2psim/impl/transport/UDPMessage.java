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


package de.tud.kom.p2psim.impl.transport;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransProtocol;

/**
 * This class is the default implementation of a transport layer message of type
 * UDP.
 * 
 * Note: If you implement a UDP message type on your own, be sure to assign the
 * protocol type <code>TransProtocol.UDP</code>.
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class UDPMessage extends AbstractTransMessage {
	/** Packet header size. */
	public static final int HEADER_SIZE = 8;

	public UDPMessage(Message payload, short senderPort, short receiverPort,
			int commId, boolean isReply) {
		this.payload = payload;
		this.srcPort = senderPort;
		this.dstPort = receiverPort;
		this.commId = commId;
		this.protocol = TransProtocol.UDP;
		this.isReply = isReply;
	}

	@Override
	public Message getPayload() {
		return this.payload;
	}

	@Override
	public long getSize() {
		return HEADER_SIZE + this.payload.getSize();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return ("[ UDP " + this.srcPort + " -> " + this.dstPort + " | size: "
				+ HEADER_SIZE + " + " + this.payload.getSize()
				+ " bytes | payload-hash: " + this.payload.hashCode() + " ]");
	}

}
