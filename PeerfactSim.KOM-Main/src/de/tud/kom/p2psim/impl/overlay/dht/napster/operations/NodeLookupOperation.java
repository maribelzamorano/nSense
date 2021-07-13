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


package de.tud.kom.p2psim.impl.overlay.dht.napster.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.NodeLookupReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.NodeLookupRequestMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NodeLookupOperation extends
		AbstractOperation<NapsterClientNode, OverlayContact> implements
		TransMessageCallback {

	private static Logger log = SimLogger.getLogger(NodeLookupOperation.class);

	private NodeLookupRequestMsg request = null;

	private NodeLookupReplyMsg reply;

	private int msgID = -2;

	private int retry;

	public NodeLookupOperation(NapsterClientNode component,
			NapsterOverlayID key, OperationCallback<OverlayContact> callback) {
		super(component, callback);
		NapsterOverlayID ownOID = getComponent().getOwnOverlayID();

		if (getComponent().getServerOverlayContact() != null
				&& getComponent().getServerOverlayContact().getOverlayID() != null) {
			NapsterOverlayID serverOID = getComponent()
					.getServerOverlayContact().getOverlayID();
			request = new NodeLookupRequestMsg(ownOID, serverOID, key,
					getOperationID());
			retry = 0;
		}
	}

	@Override
	protected void execute() {
		if (request == null)
			return;

		TransInfo serverTransInfo = getComponent().getServerOverlayContact()
				.getTransInfo();
		msgID = getComponent().getTransLayer().sendAndWait(request,
				serverTransInfo, getComponent().getPort(), TransProtocol.UDP,
				this, 5 * Simulator.SECOND_UNIT);
		log.info("[Client] Initiating transMessage with id " + msgID
				+ "-->opID " + getOperationID());
	}

	@Override
	public OverlayContact getResult() {
		return reply.getCastedResult();
	}

	public void messageTimeoutOccured(int commId) {
		log
				.info(retry
						+ ". NodeLookupOperation failed @ "
						+ getComponent().getOwnOverlayContact().getTransInfo()
								.getNetId()
						+ " due to Message-timeout of transMessage with ID = "
						+ commId);
		if (retry < 3) {
			retry = retry + 1;
			execute();
		} else {
			retry = 0;
			operationFinished(false);
		}
	}

	public void receive(Message msg, TransInfo senderInfo, int commId) {
		retry = 0;
		reply = (NodeLookupReplyMsg) msg;
		if (request.getOpID() == reply.getOpID()) {
			log.info("[Client] transMessage with ID = " + commId
					+ " is received");
			operationFinished(true);
		} else {
			log
					.error("[Client] The opID send does not equal the opID received");
		}

	}

}
