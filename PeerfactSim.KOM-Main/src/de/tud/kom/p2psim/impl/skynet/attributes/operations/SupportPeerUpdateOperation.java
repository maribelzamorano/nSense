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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerUpdateACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerUpdateMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements the operation of a SupportPeerUpdate. Within this
 * operation a {@link SupportPeerUpdateMsg}, which contains the
 * <code>SkyNetNodeInfo</code> of a new Parent-Coordinator to which the
 * addressed Support Peer will transmit its attribute-updates, is sent to the
 * Support Peer, that answers with a {@link SupportPeerUpdateACKMsg}. As the
 * acknowledgment biggybacks no further information, the message is only used to
 * successfully terminate the operation. If no answer is received, the message
 * is retransmitted.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class SupportPeerUpdateOperation extends
		AbstractOperation<SkyNetNodeInterface, Object> implements
		TransMessageCallback {

	private static Logger log = SimLogger
			.getLogger(SupportPeerUpdateOperation.class);

	private SupportPeerUpdateMsg request;

	private SupportPeerUpdateACKMsg reply;

	private SkyNetNodeInfo receiverInfo;

	private int retry;

	private int msgID = -2;

	public SupportPeerUpdateOperation(SkyNetNodeInterface skyNetNode,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			SkyNetNodeInfo parentCoordinator, long skyNetMsgID,
			OperationCallback<Object> operationCallback) {
		super(skyNetNode, operationCallback);
		request = new SupportPeerUpdateMsg(senderInfo, receiverInfo,
				parentCoordinator, skyNetMsgID);
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
				.info(retry + ". SupportPeerUpdateOperation failed @ "
						+ SkyNetUtilities.getNetID(getComponent())
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
		reply = (SupportPeerUpdateACKMsg) msg;
		if (request.getSkyNetMsgID() == reply.getSkyNetMsgID()) {
			log.debug("TransMessage with ID = " + commId + " is received");
			operationFinished(true);
		} else {
			log
					.error("The SkyNetMsgID send does not equal the SkyNetMsgID received");
		}
	}

}
