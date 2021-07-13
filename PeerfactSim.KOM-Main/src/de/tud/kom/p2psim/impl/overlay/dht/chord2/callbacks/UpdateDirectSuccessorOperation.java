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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.AbstractChordOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to check a next direct successor. The successor is
 * required to deliver its predecessor. If the successor was offline, the
 * operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateDirectSuccessorOperation extends
		AbstractChordOperation<ChordContact> implements TransMessageCallback {

	private final ChordNode masterNode;

	private int resendCounter = 0;

	private RetrievePredecessorMsg msg;

	private ChordContact successor;

	private ChordContact predecessorContact;

	protected long beginTime;

	public UpdateDirectSuccessorOperation(ChordNode component,
			OperationCallback<ChordContact> callback) {
		super(component, callback);
		masterNode = component;
	}

	final static Logger log = SimLogger
			.getLogger(UpdateDirectSuccessorOperation.class);

	@Override
	protected void execute() {
		beginTime = Simulator.getCurrentTime();

		if (masterNode.isPresent()) {
			// send request to retrieve predecessor from successor
			successor = masterNode.getChordRoutingTable().getSuccessor();
			msg = new RetrievePredecessorMsg(masterNode.getLocalChordContact(),
					successor);

			if (successor.equals(masterNode.getLocalChordContact())) {
				// catch exception that the first host has itself as predecessor
				predecessorContact = masterNode.getChordRoutingTable()
						.getPredecessor();
				operationFinished(true);
				masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
						successor, predecessorContact);
			} else {
				sendRetrievePredecessorMsg();
			}

		} else {
			operationFinished(false);
		}
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (!masterNode.isPresent())
			return;

		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			sendRetrievePredecessorMsg();
		} else {
			operationFinished(false);
			log.debug("Update Direct Successor failed");
			// masterNode.getChordRoutingTable().receiveOfflineEvent(successor);
			masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
					successor, null);
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (!masterNode.isPresent())
			return;

		operationFinished(true);
		RetrievePredecessorReply reply = (RetrievePredecessorReply) msg;
		predecessorContact = reply.getPredecessor();
		masterNode.getChordRoutingTable().updatePredecessorOfSuccessor(
				successor, predecessorContact);
	}

	@Override
	public ChordContact getResult() {

		return predecessorContact;
	}

	private int sendRetrievePredecessorMsg() {
		log.debug("sendRetrievePredecessor " + msg + " times = "
				+ resendCounter);
		return masterNode.getTransLayer().sendAndWait(msg,
				successor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	public long getBeginTime() {
		return beginTime;
	}
}
