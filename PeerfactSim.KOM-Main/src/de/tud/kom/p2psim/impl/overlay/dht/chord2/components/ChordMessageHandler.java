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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.HandshakeCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.MessageTimer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.AbstractRequestMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.AckMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ChordMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LeaveMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LookupReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyOfflineMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyPredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifySuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.StoreMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.StoreReplyMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupReplyMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.AbstractChordOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.LookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.RoutingTableContructor;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * ChordMessageHandler handle incoming Overlay Messages.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordMessageHandler implements TransMessageListener {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordNode node;

	// count number of lookup was dropped cause of exceedance MAX_HOP_COUNT
	private static int dropLookupcount;

	public ChordMessageHandler(ChordNode node) {
		this.node = node;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		if (!node.isPresent()) {
			return;
		}
		Message msg = receivingEvent.getPayload();
		log.debug("node " + node + " receive msg " + msg + " at "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		ChordRoutingTable routingTable = node.getChordRoutingTable();

		// increase hop count
		if (msg instanceof AbstractRequestMessage) {

			AbstractRequestMessage chordMsg = (AbstractRequestMessage) msg;
			if (!receivingEvent.getSenderTransInfo()
					.equals(node.getTransInfo())) {
				chordMsg.incHop();
				if (chordMsg.getHopCount() > ChordConfiguration.MAX_HOP_COUNT) {
					log.debug("invalid route hop = " + chordMsg.getHopCount()
							+ " msg = " + chordMsg + " last sender "
							+ receivingEvent.getSenderTransInfo());
					log.debug(" last sender "
							+ node.getBootstrapManager().getOverlayNode(
									receivingEvent.getSenderTransInfo()));
					if (chordMsg.getHopCount() > ChordConfiguration.MAX_HOP_COUNT * 3 / 2) {
						log.debug("drop msg = " + chordMsg);
						if (chordMsg instanceof LookupMessage) {
							dropLookupcount++;
							log.debug("sum of dropped lookup = "
									+ dropLookupcount);
						}
						return;
					}
				}
			}
		}

		// request messages

		if (msg instanceof LookupMessage) {

			LookupMessage lookupMsg = (LookupMessage) msg;
			handleLookupMsg(lookupMsg);
			sendAck(msg, receivingEvent, true);
		}

		else if (msg instanceof JoinMessage) {
			JoinMessage joinMessage = (JoinMessage) msg;
			node.overlayNodeLookup(((JoinMessage) msg).getSender(),
					new HandshakeCallback(joinMessage.getSenderContact(), node,
							receivingEvent));
		}

		else if (msg instanceof HandshakeMsg) {
			Set<ChordContact> routingContacts = RoutingTableContructor
					.getDistinctContactList(routingTable.copyFingerTable());
			HandshakeReply reply = new HandshakeReply(
					((HandshakeMsg) msg).getReceiverContact(),
					((HandshakeMsg) msg).getSenderContact(),
					routingTable.getPredecessor(), routingContacts);
			sendReply(reply, receivingEvent);
		}

		else if (msg instanceof RetrievePredecessorMsg) {
			RetrievePredecessorReply reply = new RetrievePredecessorReply(
					((RetrievePredecessorMsg) msg).getReceiverContact(),
					((RetrievePredecessorMsg) msg).getSenderContact(),
					routingTable.getPredecessor());
			sendReply(reply, receivingEvent);
		}

		else if (msg instanceof RetrieveSuccessorMsg) {
			RetrieveSuccessorReply reply = new RetrieveSuccessorReply(
					((RetrieveSuccessorMsg) msg).getReceiverContact(),
					((RetrieveSuccessorMsg) msg).getSenderContact(),
					routingTable.getSuccessor());
			sendReply(reply, receivingEvent);
		}

		else if (msg instanceof NotifyPredecessorMsg) {
			NotifyPredecessorMsg notifyPredecessorMsg = (NotifyPredecessorMsg) msg;
			routingTable.updatePredecessor(notifyPredecessorMsg
					.getPredecessor());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof NotifySuccessorMsg) {
			NotifySuccessorMsg notifySuccessorMsg = (NotifySuccessorMsg) msg;
			routingTable.updateSuccessor(notifySuccessorMsg.getSuccessor());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof NotifyOfflineMsg) {
			NotifyOfflineMsg notifyOfflineMsg = (NotifyOfflineMsg) msg;
			log.info("Inform offline receive NotifyOfflineMsg");
			routingTable.receiveOfflineEvent(notifyOfflineMsg.getOfflineInfo());
			sendAck(msg, receivingEvent, false);
		}

		else if (msg instanceof LeaveMessage) {
			LeaveMessage leaveMessage = (LeaveMessage) msg;
			log.debug("Inform offline receive LeaveMessage");
			routingTable.receiveOfflineEvent(leaveMessage.getSenderContact());
			sendAck(msg, receivingEvent, false);
		}

		// reply messages

		else if (msg instanceof LookupReply) {

			LookupReply reply = (LookupReply) msg;
			int lookupId = reply.getRequest().getLookupID();
			AbstractChordOperation<?> op = node.getLookupOperation(lookupId);
			if (op != null && op instanceof LookupOperation) {
				LookupOperation lookupOp = (LookupOperation) op;
				lookupOp.deliverResult(reply.getResponsibleContact(), reply
						.getRequest().getTarget(), reply.getRequest()
						.getLookupID(), reply.getRequest().getHopCount());
			} else {
				log.debug("lookup operation not found -> duplicated answer to lookup - receiver = "
						+ node.getOverlayID() + " lookupId = " + lookupId);
			}
			sendAck(msg, receivingEvent, false);
		}

		// messages used to confirm to the DHTNode interface

		else if (msg instanceof StoreMessage) {
			StoreMessage storeMsg = (StoreMessage) msg;
			node.getDHT().addDHTEntry(storeMsg.getKey().getCorespondingKey(),
					storeMsg.getObject());
			// node.getStoredObjects()
			// .put(storeMsg.getKey(), storeMsg.getObject());

			StoreReplyMessage reply = new StoreReplyMessage(
					((StoreMessage) msg).getReceiverContact(),
					((StoreMessage) msg).getSenderContact(),
					node.getLocalChordContact());
			sendReply(reply, receivingEvent);

		} else if (msg instanceof ValueLookupMessage) {
			ValueLookupMessage lookupMsg = (ValueLookupMessage) msg;
			DHTObject object = (DHTObject) node.getDHT().getDHTValue(
					lookupMsg.getTargetKey().getCorespondingKey());
			// node.getStoredObjects().get(
			//		lookupMsg.getTargetKey());

			ValueLookupReplyMessage reply = new ValueLookupReplyMessage(
					((ValueLookupMessage) msg).getReceiverContact(),
					((ValueLookupMessage) msg).getSenderContact(), object);
			sendReply(reply, receivingEvent);
		}
	}

	/**
	 * Method handle LookupMessage only
	 * 
	 * @param lookupMsg
	 */
	public void handleLookupMsg(LookupMessage lookupMsg) {

		ChordRoutingTable routingTable = node.getChordRoutingTable();
		ChordID target = lookupMsg.getTarget();

		if (routingTable.responsibleFor(target)
				|| node.getDHT().getDHTEntry(target.getCorespondingKey()) != null) {
			// the node itself is responsible for the key. Additionally, if it
			// stores information on the key (due to content replication) it
			// will answer

			LookupReply reply = new LookupReply(lookupMsg.getReceiverContact(),
					lookupMsg.getSenderContact(), node.getLocalChordContact(),
					lookupMsg);
			log.debug("send lookup reply " + reply + "receiver "
					+ lookupMsg.getSenderContact());

			MessageTimer msgTimer = new MessageTimer(node, reply,
					lookupMsg.getSenderContact());
			node.getTransLayer().sendAndWait(reply,
					lookupMsg.getSenderContact().getTransInfo(),
					node.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
					msgTimer, ChordConfiguration.MESSAGE_TIMEOUT);

		} else {
			// forward message
			log.debug("forward lookup");

			// Get maximum finger that precedes the id
			ChordContact precedingFinger = routingTable
					.getClosestPrecedingFinger(target);

			// if no finger precedes the id
			if (precedingFinger.equals(node.getLocalChordContact())) {
				/*
				 * This is the case if the key lies between this node's id and
				 * its direct successor. That means the message is delivered to
				 * the direct successor, otherwise it is forwarded to the
				 * closest preceding node.
				 */

				log.trace("next successor is responder succ = "
						+ routingTable.getSuccessor());

				ChordContact nextHop = routingTable.getSuccessor();

				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), nextHop, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(nextHop.getOverlayID()) != 0) {

					MessageTimer msgTimer = new MessageTimer(node, forwardMsg,
							nextHop);

					node.getTransLayer().sendAndWait(forwardMsg,
							nextHop.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			} else {
				// forward to the found preceding finger

				LookupMessage forwardMsg = new LookupMessage(
						lookupMsg.getSenderContact(), precedingFinger, target,
						lookupMsg.getLookupID(), lookupMsg.getHopCount());

				if (node.getOverlayID().compareTo(
						precedingFinger.getOverlayID()) != 0) {

					MessageTimer msgTimer = new MessageTimer(node, forwardMsg,
							precedingFinger);

					node.getTransLayer().sendAndWait(forwardMsg,
							precedingFinger.getTransInfo(), node.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			}
		}
	}

	private void sendReply(Message reply, TransMsgEvent receivingEvent) {

		node.getTransLayer().sendReply(reply, receivingEvent, node.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL);
	}

	/**
	 * send acknowledge message
	 * 
	 * @param receivingEvent
	 */
	private void sendAck(Message msg, TransMsgEvent receivingEvent, boolean look) {
		ChordMessage chordMsg = (ChordMessage) msg; // We wouldn't send an ack
													// for a non-chord message

		AckMessage ack = new AckMessage(chordMsg.getReceiverContact(),
				chordMsg.getSenderContact(), receivingEvent.getCommId(), look);
		node.getTransLayer().sendReply(ack, receivingEvent, node.getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL);
	}
}
