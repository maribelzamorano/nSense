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


package de.tud.kom.p2psim.impl.skynet.queries.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.skynet.SkyNetLayer;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.SupportPeer;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.queries.Query;
import de.tud.kom.p2psim.impl.skynet.queries.messages.QueryForwardACKMsg;
import de.tud.kom.p2psim.impl.skynet.queries.messages.QueryForwardMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements the operation of forwarding a query. Within this
 * operation a {@link QueryForwardMsg} containing an originated query of any
 * SkyNet-node is sent to a further node, which answers with a
 * {@link QueryForwardACKMsg}. As the acknowledgment biggybacks no further
 * information, the message is only used to successfully terminate the
 * operation. If no answer is received, the message is retransmitted. If this
 * operation fails, the instantiated <code>QueryForwardMsg</code> is retrieved
 * and sent to another node.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class QueryForwardOperation extends
		AbstractOperation<SkyNetLayer, Object> implements TransMessageCallback {

	private static Logger log = SimLogger
			.getLogger(QueryForwardOperation.class);

	private QueryForwardMsg request;

	private SkyNetNodeInfo receiverInfo;

	private int retry;

	private int msgID = -2;

	private long ackTime;

	public QueryForwardOperation(SkyNetLayer component,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			Query query, boolean isSolved, long skyNetMsgID,
			boolean receiverSP, boolean senderSP, long ackTime,
			OperationCallback<Object> callback) {
		super(component, callback);
		request = new QueryForwardMsg(senderInfo, receiverInfo, query.clone(),
				isSolved, skyNetMsgID, receiverSP, senderSP);
		this.receiverInfo = receiverInfo;
		retry = 0;
		this.ackTime = ackTime;
	}

	@Override
	protected void execute() {
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

	@Override
	public void messageTimeoutOccured(int commId) {
		log.debug(retry + ". QueryForwardOP to "
				+ SkyNetUtilities.getNetID(receiverInfo) + " failed @ "
				+ SkyNetUtilities.getNetID(getComponent().getSkyNetNodeInfo())
				+ " due to Msg-timeout of transMsg with ID = " + commId);
		if (getComponent() instanceof SkyNetNodeInterface) {
			if (retry < ((SkyNetNodeInterface) getComponent())
					.getAttributeUpdateStrategy().getNumberOfRetransmissions()) {
				retry = retry + 1;
				execute();
			} else {
				retry = 0;
				operationFinished(false);
			}
		} else {
			if (retry < ((SupportPeer) getComponent())
					.getSPAttributeUpdateStrategy()
					.getNumberOfRetransmissions()) {
				retry = retry + 1;
				execute();
			} else {
				retry = 0;
				operationFinished(false);
			}
		}

	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		retry = 0;
		if (request.getSkyNetMsgID() == ((QueryForwardACKMsg) msg)
				.getSkyNetMsgID()) {
			log.debug("TransMessage with ID = " + commId + " is received");
			operationFinished(true);
		} else {
			log.error(SkyNetUtilities.getTimeAndNetID(request
					.getSenderNodeInfo())
					+ "The expected SkyNetMsgID "
					+ request.getSkyNetMsgID()
					+ " does not equal the received SkyNetMsgID "
					+ ((QueryForwardACKMsg) msg).getSkyNetMsgID()
					+ ", which was sent from "
					+ SkyNetUtilities.getNetID(((QueryForwardACKMsg) msg)
							.getSenderNodeInfo()));
		}
	}

	/**
	 * This method is called, if the <code>QueryForwardOperation</code> failed.
	 * The method returns the <code>QueryForwardMsg</code>, which is utilized
	 * for further transmissions to other nodes.
	 * 
	 * @return the instantiated <code>QueryForwardMsg</code> of this operation
	 */
	public QueryForwardMsg getRequest() {
		return request;
	}

}
