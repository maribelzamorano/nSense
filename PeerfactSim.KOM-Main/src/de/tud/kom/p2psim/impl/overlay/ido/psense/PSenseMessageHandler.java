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

package de.tud.kom.p2psim.impl.overlay.ido.psense;

import java.awt.Point;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.ActionsMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.PositionUpdateMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.SensorRequestMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.SensorResponseMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.IncomingMessageList;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.SequenceNumber;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is the MessageHandler for the incoming messages. It preprocess the
 * messages for the round. It stores the new information in the datastructure
 * {@link PSense} and stores the newest messages in {@link IncomingMessageList}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 * 
 */
public class PSenseMessageHandler implements TransMessageCallback,
		TransMessageListener {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(PSenseMessageHandler.class);

	private PSenseNode node;

	public PSenseMessageHandler(PSenseNode node) {
		this.node = node;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		PSense localPSense = node.getLocalPSense();
		IncomingMessageList incomingMessageList = node.getIncomingMessageList();
		Message msg = receivingEvent.getPayload();

		if (msg instanceof ForwardMsg) {
			ForwardMsg fwdMsg = (ForwardMsg) msg;

			// fetch information from the message
			int visionRange = fwdMsg.getRadius();
			Point position = fwdMsg.getPosition();
			SequenceNumber seqNr = fwdMsg.getSequenceNr();
			List<PSenseID> receiversList = fwdMsg.getReceiversList();
			byte hops = fwdMsg.getHopCount();

			// create a nodeInfo
			PSenseContact contact = fwdMsg.getContact();
			PSenseNodeInfo nodeInfo = new PSenseNodeInfo(visionRange, position,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			boolean isNew = localPSense.updateNodeStorage(
					contact.getOverlayID(), nodeInfo);

			if (isNew) {
				// put it in the message Queue
				IncomingMessageBean msgBean = new IncomingMessageBean(contact,
						fwdMsg);
				incomingMessageList.addPositionMsg(msgBean);
			}
		} else if (msg instanceof PositionUpdateMsg) {
			PositionUpdateMsg posUpdateMsg = (PositionUpdateMsg) msg;

			// fetch information from the message
			PSenseID senderID = posUpdateMsg.getSenderID();
			int visionRange = posUpdateMsg.getRadius();
			Point position = posUpdateMsg.getPosition();
			SequenceNumber seqNr = posUpdateMsg.getSequenceNr();
			List<PSenseID> receiversList = posUpdateMsg.getReceiversList();
			byte hops = posUpdateMsg.getHopCount();

			// create contact
			PSenseContact contact = new PSenseContact(senderID,
					receivingEvent.getSenderTransInfo());
			// create a nodeInfo
			PSenseNodeInfo nodeInfo = new PSenseNodeInfo(visionRange, position,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			boolean isNew = localPSense.updateNodeStorage(
					contact.getOverlayID(), nodeInfo);

			if (isNew) {
				// put it in the message Queue
				IncomingMessageBean msgBean = new IncomingMessageBean(contact,
						posUpdateMsg);
				incomingMessageList.addPositionMsg(msgBean);
			}

		} else if (msg instanceof SensorRequestMsg) {

			SensorRequestMsg sensorRequestMsg = (SensorRequestMsg) msg;

			// fetch information from the message
			PSenseID senderID = sensorRequestMsg.getSenderID();
			int visionRange = sensorRequestMsg.getRadius();
			Point position = sensorRequestMsg.getPosition();
			SequenceNumber seqNr = sensorRequestMsg.getSequenceNr();
			List<PSenseID> receiversList = null;
			byte hops = sensorRequestMsg.getHopCount();

			// create contact
			PSenseContact contact = new PSenseContact(senderID,
					receivingEvent.getSenderTransInfo());
			// create a nodeInfo
			PSenseNodeInfo nodeInfo = new PSenseNodeInfo(visionRange, position,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			localPSense.updateNodeStorage(contact.getOverlayID(), nodeInfo);

			// put it in the message Queue
			IncomingMessageBean msgBean = new IncomingMessageBean(contact,
					sensorRequestMsg);
			incomingMessageList.addSensorRequestMsg(msgBean);

		} else if (msg instanceof SensorResponseMsg) {
			SensorResponseMsg sensorResponseMsg = (SensorResponseMsg) msg;

			// fetch information from the message
			int visionRange = sensorResponseMsg.getRadius();
			Point position = sensorResponseMsg.getPosition();
			SequenceNumber seqNr = sensorResponseMsg.getSequenceNr();
			List<PSenseID> receiversList = null;
			byte hops = sensorResponseMsg.getHopCount();

			// create a nodeInfo
			PSenseContact contact = sensorResponseMsg.getContact();
			PSenseNodeInfo nodeInfo = new PSenseNodeInfo(visionRange, position,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			localPSense.updateNodeStorage(contact.getOverlayID(), nodeInfo);

			// put it in the message Queue
			IncomingMessageBean msgBean = new IncomingMessageBean(contact,
					sensorResponseMsg);
			incomingMessageList.addSensorResponseMsg(msgBean);

		} else if (msg instanceof ActionsMsg) {
			// Do Nothing! Is only for traffic
		} else {
			log.warn("An unkown type of message is arrived in PSense");
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// not needed
		log.warn("Unexpected message receive in PSenseMessageHandler.receive");
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// not needed
		log.warn("Unexpected message timeout occured in PSenseMessageHandler.messageTimeoutOccured");
	}
}
