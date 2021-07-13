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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinReply;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This event occurs when a new join node contacts with its successor at the
 * first time. The successor send to new join node its predecessor and set the
 * new join node as its new predecessor
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HandshakeCallback implements OperationCallback<List<ChordContact>> {

	private static Logger log = SimLogger.getLogger(HandshakeCallback.class);

	private ChordNode masterNode;

	private TransMsgEvent receivingEvent;

	private ChordContact joinNode;

	public HandshakeCallback(ChordContact joinNode, ChordNode masterNode,
			TransMsgEvent receivingEvent) {
		super();
		this.joinNode = joinNode;
		this.masterNode = masterNode;
		this.receivingEvent = receivingEvent;
	}

	@Override
	public void calledOperationFailed(Operation<List<ChordContact>> op) {
		log.info("Operation Failed node = " + masterNode);
	}

	@Override
	public void calledOperationSucceeded(Operation<List<ChordContact>> op) {

		List<ChordContact> responders = op.getResult();
		if (!responders.isEmpty()) {

			JoinReply reply = new JoinReply(
                    masterNode.getLocalChordContact(), joinNode,
                    responders.get(0));
			masterNode.getTransLayer()
					.sendReply(reply, receivingEvent, masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL);
		}

	}

	public ChordContact getJoinNode() {
		return joinNode;
	}

}
