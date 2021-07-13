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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetMessage;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This abstract class is the base-class for all types of message, which are
 * used within SkyNet. The class implements all methods, which are introduced by
 * the <code>SkyNetMessage</code>-interface to relieve every extending message
 * of implementing the methods again.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public abstract class AbstractSkyNetMessage implements SkyNetMessage {

	private long timestamp;

	private SkyNetNodeInfo senderNodeInfo;

	private SkyNetNodeInfo receiverNodeInfo;

	private long skyNetMsgID;

	private boolean ack;

	private boolean receiverSP;

	private boolean senderSP;

	private long size;

	public AbstractSkyNetMessage(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, long skyNetMsgID, boolean ack,
			boolean receiverSP, boolean senderSP) {
		timestamp = Simulator.getCurrentTime();
		this.senderNodeInfo = senderNodeInfo;
		this.receiverNodeInfo = receiverNodeInfo;
		this.skyNetMsgID = skyNetMsgID;
		this.ack = ack;
		this.receiverSP = receiverSP;
		this.senderSP = senderSP;
		this.size = SkyNetConstants.LONG_SIZE
				+ (SkyNetConstants.SKY_NET_NODE_INFO_SIZE) * 2
				+ SkyNetConstants.LONG_SIZE + 3 * SkyNetConstants.BOOLEAN_SIZE;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public SkyNetNodeInfo getSenderNodeInfo() {
		return senderNodeInfo;
	}

	public SkyNetNodeInfo getReceiverNodeInfo() {
		return receiverNodeInfo;
	}

	public long getSkyNetMsgID() {
		return skyNetMsgID;
	}

	public boolean isACK() {
		return ack;
	}

	public boolean isReceiverSP() {
		return receiverSP;
	}

	public boolean isSenderSP() {
		return senderSP;
	}

	public String toString() {
		return "[ SkyNetMsg "
				+ getSenderNodeInfo().getSkyNetID().getPlainSkyNetID() + " -> "
				+ getReceiverNodeInfo().getSkyNetID().getPlainSkyNetID()
				+ " | size: " + getSize() + " | MsgNo: " + getSkyNetMsgID()
				+ " | reply: " + isACK() + " ]";
	}

	public long getSize() {
		return size;
	}

	public Message getPayload() {
		// not needed
		return null;
	}

}
