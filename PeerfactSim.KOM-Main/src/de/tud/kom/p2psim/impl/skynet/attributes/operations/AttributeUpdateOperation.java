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


package de.tud.kom.p2psim.impl.skynet.attributes.operations;

import java.math.BigDecimal;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeEntry;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.AttributeUpdateACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.AttributeUpdateMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements the operation of an attribute-update. Within this
 * operation a {@link AttributeUpdateMsg} with the attribute-entries is sent to
 * a Parent-Coordinator, which answers with an {@link AttributeUpdateACKMsg}. As
 * the acknowledgment biggybacks no further information, the message is only
 * used to successfully terminate the operation. If no answer is received, the
 * message is retransmitted.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class AttributeUpdateOperation extends
		AbstractOperation<SkyNetNodeInterface, Object> implements
		TransMessageCallback {

	private static Logger log = SimLogger
			.getLogger(AttributeUpdateOperation.class);

	private AttributeUpdateMsg request;

	private AttributeUpdateACKMsg reply;

	private SkyNetNodeInfo receiverInfo;

	private int retry;

	private int msgID = -2;

	public AttributeUpdateOperation(SkyNetNodeInterface component,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			TreeMap<BigDecimal, AttributeEntry> attributeEntries,
			int numberOfUpdates, int maxEntries, boolean downSupportPeer,
			long skyNetMsgID, boolean receiverSP, boolean senderSP,
			OperationCallback<Object> callback) {
		super(component, callback);
		request = new AttributeUpdateMsg(senderInfo, receiverInfo,
				attributeEntries, numberOfUpdates, maxEntries, downSupportPeer,
				skyNetMsgID, receiverSP, senderSP);
		this.receiverInfo = receiverInfo;
		retry = 0;
	}

	@Override
	protected void execute() {
		long ackTime = getComponent().getAttributeUpdateStrategy()
				.getTimeForACK();
		msgID = getComponent().getTransLayer().sendAndWait(request,
				receiverInfo.getTransInfo(), getComponent().getPort(),
				TransProtocol.UDP, this, ackTime);
		log.debug("Initiating transMessage with id " + msgID
				+ "-->SkyNetMsgID " + request.getSkyNetMsgID());
	}

	@Override
	public Object getResult() {
		// not needed
		return null;
	}

	public void messageTimeoutOccured(int commId) {
		log
				.info(retry
						+ ". AttributeUpdateOperation failed @ "
						+ getComponent().getSkyNetNodeInfo().getTransInfo()
								.getNetId()
						+ " due to Message-timeout of transMessage with ID = "
						+ commId);
		if (retry < getComponent().getAttributeUpdateStrategy()
				.getNumberOfRetransmissions()) {
			retry = retry + 1;
			execute();
		} else {
			retry = 0;
			operationFinished(false);
		}
	}

	public void receive(Message msg, TransInfo senderInfo, int commId) {
		retry = 0;
		reply = (AttributeUpdateACKMsg) msg;
		if (request.getSkyNetMsgID() == reply.getSkyNetMsgID()) {
			log.debug("TransMessage with ID = " + commId + " is received");
			operationFinished(true);
		} else {
			log
					.error("The SkyNetMsgID send does not equal the SkyNetMsgID received");
		}
	}

	public SkyNetNodeInfo getReceiverInfo() {
		return receiverInfo;
	}

}
