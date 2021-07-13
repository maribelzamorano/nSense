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

package de.tud.kom.p2psim.impl.overlay.ido.cs.operations;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientID;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.ErrorMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.JoinMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.JoinReplyMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The join operation of a client. It sends a join message to the server and
 * receive in this class the response from the server.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version
 */
public class JoinOperation extends AbstractOperation<ClientNode, Object>
		implements TransMessageCallback {
	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(JoinOperation.class);

	private ClientNode node;

	private int queryCommID;

	public JoinOperation(ClientNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.node = component;
	}

	/**
	 * Sends to the first entry in the BootstrapManager a join Message.
	 */
	@Override
	protected void execute() {
		List<TransInfo> transInfos = node.getBootstrapManager()
				.getBootstrapInfo();
		if (transInfos == null || transInfos.size() == 0) {
			log.warn("No bootstrap contact information");
		} else {
			int firstServer = 0;
			TransInfo serverTransInfo = transInfos.get(firstServer);

			JoinMessage msg = new JoinMessage(node.getPosition(), node.getAOI());

			// send the msg and wait. Callback is in this class.
			queryCommID = node.getTransLayer().sendAndWait(msg,
					serverTransInfo, node.getPort(),
					CSConfiguration.TRANSPORT_PROTOCOL, this,
					CSConfiguration.JOIN_TIME_OUT);
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

	/**
	 * Receive the response from the server to the joinMessage. If the message
	 * the right and contains the needed information, the operation is
	 * successful finished. Otherwise is the operation failed.
	 */
	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (node.getPeerStatus() == PeerStatus.TO_JOIN) {
			if (msg instanceof JoinReplyMessage) {
				if (this.queryCommID == commId) {
					JoinReplyMessage joinRe = (JoinReplyMessage) msg;
					ClientID id = joinRe.getClientId();

					// sets the needed Information
					node.setOverlayID(id);
					node.setServerTransInfo(joinRe.getServerTransInfo());
					operationFinished(true);
				} else {
					log.error(node.getOverlayID()
							+ " has get a JoinResponse with the wrong commId.");
					operationFinished(false);
				}
			} else if (msg instanceof ErrorMessage) {
				ErrorMessage errMsg = (ErrorMessage) msg;
				log.error(node.getOverlayID()
						+ " cannot connect with Server. The Server send the ErrorMessage with "
						+ errMsg.getErrorType());
				operationFinished(false);
			} else {
				log.warn(node.getOverlayID() + " has get a wrong Msg.");
				operationFinished(false);
			}
		}
		operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (log.isInfoEnabled())
			log.info(node.getOverlayID() + " msg timeout in JoinOperation.");

		if (this.queryCommID == commId)
			operationFinished(false);
	}

	/**
	 * Stops the operation.
	 */
	public void stop() {
		if (log.isInfoEnabled())
			log.info(node.getOverlayID() + " could not complete join.");
		operationFinished(false);
	}

}
