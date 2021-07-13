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
import de.tud.kom.p2psim.impl.network.IPv4Message;

/**
 * This class is the default implementation of a transport layer message of type
 * TCP.
 * 
 * Note: If you implement a TCP message type on your own, be sure to assign the
 * protocol type <code>TransProtocol.TCP</code>.
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class TCPMessage extends AbstractTransMessage {
	/** Packet header size. */
	public static final int HEADER_SIZE = 20;

	private long sequenzNumber = 0;

	private int numberOfSegments = 0;

	public TCPMessage(Message payload, short srcPort, short dstPort,
			int commId, boolean isReply, long sequenzNumber) {
		this.payload = payload;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.commId = commId;
		this.protocol = TransProtocol.TCP;
		this.isReply = isReply;
		this.sequenzNumber = sequenzNumber;

		int maxPayloadPerSegment = IPv4Message.MTU_SIZE
				- IPv4Message.HEADER_SIZE - TCPMessage.HEADER_SIZE;

		this.numberOfSegments = (int) Math.ceil((double) getPayload().getSize()
				/ (double) (maxPayloadPerSegment));

	}

	@Override
	public Message getPayload() {
		return this.payload;
	}

	@Override
	public long getSize() {
		return numberOfSegments * HEADER_SIZE + this.payload.getSize();
	}

	/**
	 * @return the sequence number of this TCP message
	 */
	public long getSequenzNumber() {
		return sequenzNumber;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return ("[ TCP " + this.srcPort + " -> " + this.dstPort + " | sn: "
				+ sequenzNumber + " | size: " + numberOfSegments + "*"
				+ HEADER_SIZE + " + " + this.payload.getSize()
				+ " bytes | payload-hash: " + this.payload.hashCode() + " ]");
	}
}
