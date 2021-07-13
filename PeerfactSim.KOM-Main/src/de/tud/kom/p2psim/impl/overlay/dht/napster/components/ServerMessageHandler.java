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


package de.tud.kom.p2psim.impl.overlay.dht.napster.components;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ClientJoinReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ClientJoinRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ClientLeaveReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ClientLeaveRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.NodeLookupReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.NodeLookupRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.PredecessorReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.PredecessorRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ResponsibilityReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.messages.ResponsibilityRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ResponsibleForKeyResult;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ServerMessageHandler implements TransMessageListener {

	private static Logger log = SimLogger.getLogger(ServerMessageHandler.class);

	private NapsterServerNode server;

	private int msgID = -2;

	public ServerMessageHandler(NapsterServerNode serverNode) {
		this.server = serverNode;
	}

	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof ClientJoinRequestMsg) {
			log.info("[Server] Received message is of type "
					+ msg.getClass().getSimpleName());
			ClientJoinRequestMsg joinMsg = (ClientJoinRequestMsg) msg;
			processClientJoin(joinMsg, receivingEvent);
		} else if (msg instanceof NodeLookupRequestMsg) {
			log.info("[Server] Received message is of type "
					+ msg.getClass().getSimpleName());
			NodeLookupRequestMsg lookupMsg = (NodeLookupRequestMsg) msg;
			processNodeLookup(lookupMsg, receivingEvent);
		} else if (msg instanceof ResponsibilityRequestMsg) {
			log.info("[Server] Received message is of type "
					+ msg.getClass().getSimpleName());
			ResponsibilityRequestMsg lookupMsg = (ResponsibilityRequestMsg) msg;
			processResponsibility(lookupMsg, receivingEvent);
		} else if (msg instanceof PredecessorRequestMsg) {
			log.info("[Server] Received message is of type "
					+ msg.getClass().getSimpleName());
			PredecessorRequestMsg lookupMsg = (PredecessorRequestMsg) msg;
			processPredecessor(lookupMsg, receivingEvent);
		} else if (msg instanceof ClientLeaveRequestMsg) {
			log.info("[Server] Received message is of type "
					+ msg.getClass().getSimpleName());
			ClientLeaveRequestMsg leaveMsg = (ClientLeaveRequestMsg) msg;
			processClientLeave(leaveMsg, receivingEvent);
		} else {
			log.warn("[Server] Received message is of an unknown type");
		}

	}

	private void processClientLeave(ClientLeaveRequestMsg leaveMsg,
			TransMsgEvent receivingEvent) {
		server.getDHT().removeContact(leaveMsg.getSender());

		// creating the message and send the message
		Message reply = new ClientLeaveReplyMsg(leaveMsg.getReceiver(),
				leaveMsg.getSender(), null, leaveMsg.getOpID());
		msgID = server.getTransLayer().sendReply(reply, receivingEvent,
				server.getPort(), TransProtocol.TCP);
		log.info("[Server] Sending transMessage with id " + msgID);
	}

	private void processPredecessor(PredecessorRequestMsg lookupMsg,
			TransMsgEvent receivingEvent) {
		// lookup the predecessor
		NapsterOverlayContact contact = server.getDHT().getPredecessor(
				lookupMsg.getCastedContent());
		if (contact != null) {
			// creating the message and send the message
			Message reply = new PredecessorReplyMsg(lookupMsg.getReceiver(),
					lookupMsg.getSender(), contact, lookupMsg.getOpID());
			msgID = server.getTransLayer().sendReply(reply, receivingEvent,
					server.getPort(), TransProtocol.UDP);
			log.info("[Server] Sending transMessage with id " + msgID);
		}
	}

	private void processClientJoin(ClientJoinRequestMsg requestMsg,
			TransMsgEvent receivingEvent) {

		// generate the overlayID, put the new overlayContact on the DHT and
		// return the new overlayID to the requester
		IPv4NetID ip = (IPv4NetID) receivingEvent.getSenderTransInfo()
				.getNetId();
		NapsterOverlayContact contact = new NapsterOverlayContact(
				new NapsterOverlayID(ip), receivingEvent.getSenderTransInfo());

		server.getDHT().addContact(contact);

		// creating the message and send the message
		Message reply = new ClientJoinReplyMsg((NapsterOverlayID) server
				.getOverlayID(), contact.getOverlayID(),
				contact.getOverlayID(), requestMsg.getOpID());
		msgID = server.getTransLayer().sendReply(reply, receivingEvent,
				server.getPort(), TransProtocol.TCP);
		log.info("[Server] Sending transMessage with id " + msgID);
	}

	private void processNodeLookup(NodeLookupRequestMsg requestMsg,
			TransMsgEvent receivingEvent) {
		// Lookup the OverlayContact for the given key
		NapsterOverlayID key = requestMsg.getCastedContent();
		NapsterOverlayContact contact = server.getDHT().nodeLookup(key);

		// creating the message and send the message
		if (contact != null) {
			Message reply = new NodeLookupReplyMsg(requestMsg.getReceiver(),
					requestMsg.getSender(), contact, requestMsg.getOpID());
			msgID = server.getTransLayer().sendReply(reply, receivingEvent,
					server.getPort(), TransProtocol.UDP);
			log.info("[Server] Sending transMessage with id " + msgID);
		}
	}

	private void processResponsibility(ResponsibilityRequestMsg requestMsg,
			TransMsgEvent receivingEvent) {
		// Lookup the Responsibility of the sender for the given key
		NapsterOverlayID key = requestMsg.getCastedContent();
		NapsterOverlayContact contact = server.getDHT().nodeLookup(key);
		if (contact != null) {
			Boolean flag;
			if (contact.getOverlayID().getID().equals(
					requestMsg.getSender().getID())) {
				// if (contact.getOverlayID().equals(requestMsg.getSender())) {
				// log.warn("DEBUX: "+contact.getOverlayID().getLongID()+"=="+
				// requestMsg.getSender().getLongID());
				flag = new Boolean(true);
			} else {
				// log.warn("DEBUX: "+contact.getOverlayID()+"!="+requestMsg.
				// getSender
				// ());
				flag = new Boolean(false);
			}
			// creating the message and send the message
			Message reply = new ResponsibilityReplyMsg(
					requestMsg.getReceiver(), requestMsg.getSender(),
					new ResponsibleForKeyResult(contact, flag), requestMsg
							.getOpID());
			msgID = server.getTransLayer().sendReply(reply, receivingEvent,
					server.getPort(), TransProtocol.UDP);
			log.info("[Server] Sending transMessage with id " + msgID);
		}
	}

}
