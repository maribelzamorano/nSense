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

package de.tud.kom.p2psim.impl.overlay.ido.cs;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.ErrorMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.UpdatePositionClientMessage;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is the Message Handler for a client. It handles the incoming
 * messages, which are not send with a {@link TransMessageCallback}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientMessageHandler implements TransMessageListener {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(ClientMessageHandler.class);

	/**
	 * The node, which adds this Message Handler.
	 */
	private ClientNode node;

	public ClientMessageHandler(ClientNode clientNode) {
		this.node = clientNode;
	}

	/**
	 * Handles the incoming messages. The message types
	 * {@link UpdatePositionClientMessage} and {@link ErrorMessage} will be
	 * handled.
	 */
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();

		if (message instanceof UpdatePositionClientMessage) {
			UpdatePositionClientMessage msg = (UpdatePositionClientMessage) message;
			List<ClientNodeInfo> nodeInfos = msg.getClientNodeInfos();

			node.getStorage().replaceNeighbors(nodeInfos);
			node.setLastUpdate(Simulator.getCurrentTime());

		} else if (message instanceof ErrorMessage) {
			ErrorMessage msg = (ErrorMessage) message;
			log.error("It arrived an ErrorMessage with ErrorType: "
					+ msg.getErrorType());
		}

	}
}
