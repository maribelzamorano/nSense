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

package de.tu.darmstadt.dvs.nSense.overlay;

import java.util.List;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.overlay.messages.ActionsMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.ForwardMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.PositionUpdateMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.SensorRequestMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.SensorResponseMsg;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.IncomingMessageList;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is the MessageHandler for the incoming messages. It preprocess the
 * messages for the round. It stores the new information in the datastructure
 * {@link NSense} and stores the newest messages in {@link IncomingMessageList}.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 * 
 */
public class NSenseMessageHandler implements TransMessageCallback,
		TransMessageListener {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(NSenseMessageHandler.class);

	private NSenseNode node;

	public NSenseMessageHandler(NSenseNode node) {
		this.node = node;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {

		NSense localNSense = node.getLocalNSense();
		IncomingMessageList incomingMessageList = node.getIncomingMessageList();
		Message msg = receivingEvent.getPayload();

		if (msg instanceof ForwardMsg) {
			ForwardMsg fwdMsg = (ForwardMsg) msg;

			// fetch information from the message
			int visionRange = fwdMsg.getRadius();
			VectorN position = fwdMsg.getPosition();
			VectorN positionDimensions = fwdMsg.getPositionDimensions();

			SequenceNumber seqNr = fwdMsg.getSequenceNr();
			List<NSenseID> receiversList = fwdMsg.getReceiversList();
			byte hops = fwdMsg.getHopCount();

			// create a nodeInfo
			NSenseContact contact = fwdMsg.getContact();
			NSenseNodeInfo nodeInfo = new NSenseNodeInfo(visionRange, position,positionDimensions,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			boolean isNew = localNSense.updateNodeStorage(
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
			NSenseID senderID = posUpdateMsg.getSenderID();
			int visionRange = posUpdateMsg.getRadius();
			VectorN position = posUpdateMsg.getPosition();
			VectorN positionDimensions = posUpdateMsg.getPositionDimensions();

			SequenceNumber seqNr = posUpdateMsg.getSequenceNr();
			List<NSenseID> receiversList = posUpdateMsg.getReceiversList();
			byte hops = posUpdateMsg.getHopCount();

			// create contact
			NSenseContact contact = new NSenseContact(senderID,
					receivingEvent.getSenderTransInfo());
			// create a nodeInfo
			NSenseNodeInfo nodeInfo = new NSenseNodeInfo(visionRange, position,positionDimensions,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			boolean isNew = localNSense.updateNodeStorage(
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
			NSenseID senderID = sensorRequestMsg.getSenderID();
			int visionRange = sensorRequestMsg.getRadius();
			VectorN position = sensorRequestMsg.getPosition();
			VectorN positionDimensions = sensorRequestMsg.getPositionDimensions();

			SequenceNumber seqNr = sensorRequestMsg.getSequenceNr();
			List<NSenseID> receiversList = null;
			byte hops = sensorRequestMsg.getHopCount();

			// create contact
			NSenseContact contact = new NSenseContact(senderID,
					receivingEvent.getSenderTransInfo());
			// create a nodeInfo
			NSenseNodeInfo nodeInfo = new NSenseNodeInfo(visionRange, position,positionDimensions,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			localNSense.updateNodeStorage(contact.getOverlayID(), nodeInfo);

			// put it in the message Queue
			IncomingMessageBean msgBean = new IncomingMessageBean(contact,
					sensorRequestMsg);
			incomingMessageList.addSensorRequestMsg(msgBean);

		} else if (msg instanceof SensorResponseMsg) {
			SensorResponseMsg sensorResponseMsg = (SensorResponseMsg) msg;

			// fetch information from the message
			int visionRange = sensorResponseMsg.getRadius();
			VectorN position = sensorResponseMsg.getPosition();
			VectorN positionDimensions = sensorResponseMsg.getPositionDimensions();

			SequenceNumber seqNr = sensorResponseMsg.getSequenceNr();
			List<NSenseID> receiversList = null;
			byte hops = sensorResponseMsg.getHopCount();

			// create a nodeInfo
			NSenseContact contact = sensorResponseMsg.getContact();
			NSenseNodeInfo nodeInfo = new NSenseNodeInfo(visionRange, position,positionDimensions,
					contact, seqNr, receiversList, hops);

			// update the nodeStorage with the new nodeInfo
			localNSense.updateNodeStorage(contact.getOverlayID(), nodeInfo);

			// put it in the message Queue
			IncomingMessageBean msgBean = new IncomingMessageBean(contact,
					sensorResponseMsg);
			incomingMessageList.addSensorResponseMsg(msgBean);

		} else if (msg instanceof ActionsMsg) {
			// Do Nothing! Is only for traffic
		} else {
			log.warn("An unkown type of message is arrived in NSense");
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// not needed
		log.warn("Unexpected message receive in NSenseMessageHandler.receive");
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// not needed
		log.warn("Unexpected message timeout occured in NSenseMessageHandler.messageTimeoutOccured");
	}
}
