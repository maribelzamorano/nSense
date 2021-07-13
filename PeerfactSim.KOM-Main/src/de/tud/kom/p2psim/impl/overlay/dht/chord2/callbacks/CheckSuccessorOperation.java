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
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorReply;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to check a successor of <code>ChordNode</code> The
 * successor is required to deliver its next direct successor. If the successor
 * was offline, the operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class CheckSuccessorOperation extends
		AbstractOperation<ChordNode, ChordContact> implements
		TransMessageCallback {

	final static Logger log = SimLogger
			.getLogger(CheckSuccessorOperation.class);

	private final ChordNode masterNode;

	private ChordContact updateSuccessor, succOfSuccessor;

	private RetrieveSuccessorMsg msg;

	private int resendCounter = 0;

	public CheckSuccessorOperation(ChordNode component) {
		super(component);
		masterNode = component;

	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			ChordRoutingTable routingTable = masterNode.getChordRoutingTable();
			updateSuccessor = routingTable.getNextUpdateSuccessor();

			log.debug("check successor node " + masterNode + " update pred = "
					+ updateSuccessor + " simTime = "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);

			msg = new RetrieveSuccessorMsg(masterNode.getLocalChordContact(),
					updateSuccessor);
			if (updateSuccessor.equals(masterNode.getLocalChordContact())) {

				// catch exception that the first host has itself as successor
				succOfSuccessor = routingTable.getSuccessor();
				operationFinished(true);
				masterNode.getChordRoutingTable().updateDistantSuccessor(
						updateSuccessor, succOfSuccessor);

			} else {
				sendRetrieveSuccessorMsg();
			}
		} else {
			operationFinished(false);
		}

	}

	@Override
	public ChordContact getResult() {
		return succOfSuccessor;
	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (!masterNode.isPresent())
			return;

		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			sendRetrieveSuccessorMsg();
		} else {
			operationFinished(false);
			log.warn("inform offline node = " + masterNode);
			masterNode.getChordRoutingTable().updateDistantSuccessor(
					updateSuccessor, null);

		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {

		if (!masterNode.isPresent())
			return;

		RetrieveSuccessorReply reply = (RetrieveSuccessorReply) msg;
		succOfSuccessor = reply.getSuccessor();
		operationFinished(true);
		masterNode.getChordRoutingTable().updateDistantSuccessor(
				updateSuccessor, succOfSuccessor);
	}

	private int sendRetrieveSuccessorMsg() {

		if (masterNode.getOverlayID().compareTo(updateSuccessor.getOverlayID()) == 0)
			return -1;

		log.debug("sendRetrieveSuccessor " + msg + " times = " + resendCounter);

		return masterNode.getTransLayer().sendAndWait(msg,
				updateSuccessor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}
}
