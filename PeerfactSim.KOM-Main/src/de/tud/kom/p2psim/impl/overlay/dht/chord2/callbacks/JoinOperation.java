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

import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.AbstractChordOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class represents a join event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinOperation extends AbstractChordOperation<Object> implements
		TransMessageCallback {

	final static Logger log = SimLogger.getLogger(JoinOperation.class);

	private int joinMsgResendCount = 0, handshakeMsgResendCount = 0;

	private final ChordNode joinNode;

	private ChordContact successorContact = null, predecessorContact = null;

	private Set<ChordContact> succFingerTable = null;

	private ChordContact joinContact, handShakeContact;

	private JoinMessage joinMsg;

	private HandshakeMsg handShakeMsg;

	private OperationCallback callback;

	public JoinOperation(ChordNode component, OperationCallback<Object> callback) {
		super(component, callback);
		joinNode = getComponent();
		this.callback = callback;
	}

	/*
	 * join contains the following steps 1. pick a random node in
	 * ChordBootstrapManager as first contact 2. The first node look the
	 * successor for join node 3. join node send handshake message to its
	 * successor 4. successor inform join node about its predecessor,
	 * FingerTable 5. finish, join node receive successor, predecessor contact
	 * and FingerTable of successor
	 */

	@Override
	protected void execute() {
		if (getComponent().isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Peer "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ " wants to join, although being already present ");
			operationFinished(true);
			return;
		}
		ChordBootstrapManager cbm = joinNode.getBootstrapManager();

		if (cbm.isEmpty()) {
			log.info("Create initial ring structure");
			predecessorContact = joinNode.getLocalChordContact();
			successorContact = joinNode.getLocalChordContact();
			finish();
		} else {
			// use firstContactNode to find successor for new joining node
			joinContact = cbm.getRandomAvailableNode();
			joinMsg = new JoinMessage(joinNode.getLocalChordContact(),
					joinContact);
			sendJoinMsg();
		}
	}

	private void sendJoinMsg() {
		joinNode.getTransLayer().sendAndWait(joinMsg,
				joinContact.getTransInfo(), joinNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	public void receiveSuccessorContact(ChordContact successor) {
		successorContact = successor;

		// retrieve predecessor of successor
		if (successorContact.compareTo(joinNode.getLocalChordContact()) != 0) {

			handShakeContact = successor;
			handShakeMsg = new HandshakeMsg(joinNode.getLocalChordContact(),
					handShakeContact);
			sendHandShakeMsg();
		} else
			abort();
	}

	private void sendHandShakeMsg() {
		joinNode.getTransLayer().sendAndWait(handShakeMsg,
				handShakeContact.getTransInfo(), joinNode.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	/**
	 * Inform JoinOperation that join process successfully ended
	 */
	private void finish() {

		if (predecessorContact.between(joinNode.getLocalChordContact(),
				successorContact)) {
			log.error("first successor and predecessor inconsitent node = "
					+ joinNode.getHost().getNetLayer().getNetID() + " succ = "
					+ successorContact + " pred = " + predecessorContact);

			// if (joinMsgResendCount < ChordConfiguration.OPERATION_MAX_REDOS)
			// {
			// joinMsgResendCount++;
			// // send Lookup message to predecessor contact
			// JoinMessage msg = new JoinMessage(joinNode
			// .getLocalChordContact(), predecessorContact);
			// joinNode.getTransLayer().sendAndWait(msg,
			// predecessorContact.getTransInfo(), joinNode.getPort(),
			// ChordConfiguration.TRANSPORT_PROTOCOL, this,
			// ChordConfiguration.LOOKUP_TIMEOUT);
			// if (joinNode.getLocalChordContact().equals(successorContact)) {
			// // critical fall
			// log.error("churn and rejoin so fast node = " + joinNode);
			// abort();
			// }
			// } else {
			abort();
			// }

		} else if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Peer "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ " wants to set state to Present, although being already Present ");
			operationFinished(true);
			return;
		} else {
			joinNode.setPeerStatus(PeerStatus.PRESENT);
			log.debug("Join successful (node = " + joinNode + " succ = "
					+ successorContact + " pred = " + predecessorContact
					+ " time = " + Simulator.getCurrentTime()
					/ Simulator.SECOND_UNIT + ")");
			ChordBootstrapManager cbm = joinNode.getBootstrapManager();
			cbm.registerNode(joinNode);
			operationFinished(true);

			joinNode.joinOperationFinished(successorContact,
					predecessorContact, succFingerTable);
		}

	}

	private void abort() {
		if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Node "
					+ joinNode.getHost().getNetLayer().getNetID()
					+ " already present, but abort was called!");
			operationFinished(true);
			return;
		}

		log.error(Simulator.getSimulatedRealtime() + " Node "
				+ joinNode.getHost().getNetLayer().getNetID()
				+ " - Join operation failed. Try to do a rejoin. "
				+ joinNode.getBootstrapManager().getNumOfAvailableNodes()
				+ " Available nodes");
		joinNode.setPeerStatus(PeerStatus.ABSENT);
		joinNode.joinWithDelay(
				callback,
				Simulator.getRandom().nextInt(
						(int) ChordConfiguration.MAX_WAIT_BEFORE_JOIN_RETRY));
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log.debug("Message timeout during node join (node= "
				+ joinNode.getHost().getNetLayer().getNetID() + ")");

		if (successorContact == null) {
			if (joinMsgResendCount < ChordConfiguration.MESSAGE_RESEND) {
				joinMsgResendCount++;
				sendJoinMsg();
			} else
				abort();
		} else {
			if (handshakeMsgResendCount < ChordConfiguration.MESSAGE_RESEND) {
				handshakeMsgResendCount++;
				sendHandShakeMsg();
			} else
				abort();
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (isFinished()) {
			log.info("op is finished before receive reply msg node =  "
					+ joinNode);
			return;
		}
		if (joinNode.isPresent()) {
			log.warn(Simulator.getSimulatedRealtime() + " Node "
					+ joinNode.getHost().getNetLayer().getNetID()
					+ " already present, but received a JoinReply or HandshakeReply message!");
			operationFinished(true);
			return;
		}
		joinMsgResendCount = 0;

		if (msg instanceof JoinReply) {
			JoinReply joinReply = (JoinReply) msg;
			ChordContact successor = joinReply.getSuccessorContact();
			receiveSuccessorContact(successor);
		} else if (msg instanceof HandshakeReply) {
			HandshakeReply reply = (HandshakeReply) msg;
			predecessorContact = reply.getPredecessor();
			succFingerTable = reply.getAvailableContacts();
			finish();
		}
	}

	@Override
	public Object getResult() {
		return this.isSuccessful();
	}
}
