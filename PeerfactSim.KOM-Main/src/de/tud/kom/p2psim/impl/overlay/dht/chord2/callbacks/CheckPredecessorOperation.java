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
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorReply;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to check a predecessor of <code>ChordNode</code> The
 * predecessor is required to deliver its next direct predecessor. If the
 * predecessor was offline, the operation returns null as result.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class CheckPredecessorOperation extends
		AbstractOperation<ChordNode, ChordContact> implements
		TransMessageCallback {

	final static Logger log = SimLogger
			.getLogger(CheckPredecessorOperation.class);

	private final ChordNode masterNode;

	private ChordContact updatePredecessor, predOfPredecessor;

	private RetrievePredecessorMsg msg;

	private int resendCounter = 0;

	public CheckPredecessorOperation(ChordNode component) {
		super(component);
		masterNode = component;

	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			ChordRoutingTable routingTable = masterNode.getChordRoutingTable();
			updatePredecessor = routingTable.getNextUpdatePredecessor();

			log.debug("check predecessor node " + masterNode
					+ " update pred = " + updatePredecessor + " simTime = "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);

			msg = new RetrievePredecessorMsg(masterNode.getLocalChordContact(),
					updatePredecessor);

			if (updatePredecessor.equals(masterNode.getLocalChordContact())) {
				// catch exception that the first host has itself as predecessor
				predOfPredecessor = routingTable.getPredecessor();
				operationFinished(true);
				masterNode.getChordRoutingTable().updateDistantPredecessor(
						updatePredecessor, predOfPredecessor);
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
			log.debug("inform offline node = " + masterNode);
			operationFinished(false);
			masterNode.getChordRoutingTable().updateDistantPredecessor(
					updatePredecessor, null);
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (!masterNode.isPresent())
			return;

		RetrievePredecessorReply reply = (RetrievePredecessorReply) msg;
		predOfPredecessor = reply.getPredecessor();
		operationFinished(true);
		masterNode.getChordRoutingTable().updateDistantPredecessor(
				updatePredecessor, predOfPredecessor);
	}

	private int sendRetrievePredecessorMsg() {

		if (masterNode.getOverlayID().compareTo(
				updatePredecessor.getOverlayID()) == 0)
			return -1;

		log.debug("sendRetrievePredecessorMsg " + msg + " times = "
				+ resendCounter);
		return masterNode.getTransLayer().sendAndWait(msg,
				updatePredecessor.getTransInfo(), masterNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	@Override
	public ChordContact getResult() {

		return predOfPredecessor;
	}

}
