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

package de.tu.darmstadt.dvs.nSense.overlay.operations;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.overlay.IncomingMessageBean;
import de.tu.darmstadt.dvs.nSense.overlay.NSense;
import de.tu.darmstadt.dvs.nSense.overlay.NSenseContact;
import de.tu.darmstadt.dvs.nSense.overlay.NSenseID;
import de.tu.darmstadt.dvs.nSense.overlay.NSenseNode;
import de.tu.darmstadt.dvs.nSense.overlay.NSenseNodeInfo;
import de.tu.darmstadt.dvs.nSense.overlay.OutgoingMessageBean;
import de.tu.darmstadt.dvs.nSense.overlay.messages.AbstractNSenseMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.AbstractPositionUpdateMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.ActionsMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.ForwardMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.PositionUpdateMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.SensorRequestMsg;
import de.tu.darmstadt.dvs.nSense.overlay.messages.SensorResponseMsg;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tu.darmstadt.dvs.nSense.overlay.util.IncomingMessageList;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is the operation for a round. First, it will be update the sensor
 * and neighbor list. After this, delete it old nodes. Then it creates the
 * messages. The messages are position updates, sensor responses, sensor
 * requests and position forwards. This message are stored in a outgoing message
 * queue for the next processing in this class. The outgoing message queue must
 * be fit to the round limit. For that, many messages of the type position
 * forwards and position updates will be deleted up to the round limit is
 * achieved. Then will be send the messages to the nodes.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class RoundOperation extends AbstractOperation<NSenseNode, Boolean> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(RoundOperation.class);

	private NSenseNode node;

	private Boolean connectedWithOverlay;

	public RoundOperation(NSenseNode node, OperationCallback<Boolean> callback) {
		super(node, callback);
		this.node = node;
		connectedWithOverlay = true;
	}

	@Override
	protected void execute() {
		if (log.isDebugEnabled())
			log.debug("========RoundOperation for Node: " + node.getOverlayID()
					+ "========");
		// TODO: mehr DEBUG informationen einfügen
		IncomingMessageList incomingMsg = node.getIncomingMessageList();
		NSense localNSense = node.getLocalNSense();

		if (aloneInOverlay()) {
			if (Simulator.getCurrentTime()
					- node.getLastNotAloneInOverlayTime() > Configuration.TIMEOUT_ALONE_IN_OVERLAY) {
				connectedWithOverlay = false;
				operationFinished(false);
			}
		} else {
			node.setLastNotAloneInOverlayTime(Simulator.getCurrentTime());
		}

		if (log.isDebugEnabled())
			log.debug("IncomingMessage: " + incomingMsg);

		node.incSeqNr();

		// The information of the incomingMessages are processed in the
		// messageHandler for this two tasks
		updateNearNodeList(localNSense);
		updateSensorNodeList(incomingMsg, localNSense);
		// Delete nodes, that are expired!
		localNSense.removeDeadNodes();

		List<OutgoingMessageBean> outgoingMessages = new Vector<OutgoingMessageBean>(
				100);

		createPositionUpdateMessages(outgoingMessages, localNSense);
		createSensorRequests(outgoingMessages, localNSense);
		createSensorResponses(outgoingMessages, localNSense, incomingMsg);
		createForwardMessages(outgoingMessages, localNSense, incomingMsg);

		createActionMessages(outgoingMessages, localNSense,
				node.getPlayerActions());

		removeDuplicateMessages(outgoingMessages);
		int roundLimit = getRoundLimit();

		fitOutgoingMessageForRoundLimit(outgoingMessages, roundLimit);
		sendMessages(outgoingMessages);

		outgoingMessages.clear();
		node.clearIncomingMessageList();
		node.clearPlayerActions();

		localNSense.removeUnusedNodes();

		operationFinished(true);
	}

	private boolean aloneInOverlay() {
		if (node.getIncomingMessageList().size() == 0) {
			if (node.getLocalNSense().getNearNodes().size() == 0) {

				// check for an empty sensor node list
				boolean emptySensorNodesList = true;
				NSenseID[] sensorNodes = node.getLocalNSense().getSensorNodes();
				for (int i = 0; i < sensorNodes.length; i++) {
					if (sensorNodes[i] != null)
						emptySensorNodesList = false;
				}

				if (emptySensorNodesList) {
					return true;
				}
			}
		}
		return false;
	}

	private void fitOutgoingMessageForRoundLimit(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {
		int notToDeleteMsgSize = getNotToDeleteMsgSize(outgoingMessages);

		if (notToDeleteMsgSize > roundLimit) {
			deleteAllPosMsgs(outgoingMessages);

			if (log.isDebugEnabled())
				log.debug("Remove all position message, because Round Limit ("
						+ roundLimit + "Byte) is to small for "
						+ notToDeleteMsgSize + "Byte of messages");

			deleteArbitraryMsgs(outgoingMessages, roundLimit);

			if (log.isDebugEnabled())
				log.debug("Remove arbitrary messages, to fit the Round Limit of"
						+ roundLimit + "Bytes");
			if (log.isInfoEnabled())
				log.info("Only sensor and action message will be send!");
		} else {
			deletePositionUpdatesToFitRoundLimit(outgoingMessages, roundLimit);
		}
	}

	private void deletePositionUpdatesToFitRoundLimit(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {

		// fetch all position updates from the outgoingMessage list.
		List<OutgoingMessageBean> posUpdateOutgoingMsgs = new Vector<OutgoingMessageBean>(
				100);
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof AbstractPositionUpdateMsg) {
				posUpdateOutgoingMsgs.add(outMsgBean);
			}
		}

		int outgoingSize = getAllMsgSize(outgoingMessages);
		while (outgoingSize > roundLimit) {
			int maxIndex = posUpdateOutgoingMsgs.size();
			int offsetIndex = 0;
			int deleteIndex = 0;
			if (maxIndex > 0) {
				deleteIndex = Simulator.getRandom().nextInt(maxIndex);
				offsetIndex = Simulator.getRandom().nextInt(maxIndex);
			}

			// Determine the number of msgs, that should be deleted.
			double averageMsgSize = (double) outgoingSize
					/ outgoingMessages.size();
			double toMuch = outgoingSize - roundLimit;
			// Delete only the half of the estimated numberToDelete.
			int numberToDelete = (int) Math
					.ceil(((toMuch / averageMsgSize) * 0.5));

			for (int i = 0; i < numberToDelete
					&& posUpdateOutgoingMsgs.size() != 0; i++) {
				OutgoingMessageBean outMsgBean = posUpdateOutgoingMsgs
						.get(deleteIndex);
				deleteOutMsgBean(outgoingMessages, outMsgBean);
				posUpdateOutgoingMsgs.remove(outMsgBean);

				maxIndex = posUpdateOutgoingMsgs.size();
				if (maxIndex > 0)
					deleteIndex = (deleteIndex + offsetIndex) % maxIndex;
				else
					deleteIndex = 0;
			}
			outgoingSize = getAllMsgSize(outgoingMessages);
		}
	}

	private void deleteArbitraryMsgs(
			List<OutgoingMessageBean> outgoingMessages, int roundLimit) {
		while (getAllMsgSize(outgoingMessages) > roundLimit) {
			int maxIndex = outgoingMessages.size();
			int deleteIndex = Simulator.getRandom().nextInt(maxIndex);

			OutgoingMessageBean outMsgBean = outgoingMessages.get(deleteIndex);
			// delete the choices message from the outgoing messages.
			deleteOutMsgBean(outgoingMessages, outMsgBean);
		}
	}

	private void deleteAllPosMsgs(List<OutgoingMessageBean> outgoingMessages) {
		List<OutgoingMessageBean> toRemove = new Vector<OutgoingMessageBean>();

		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof PositionUpdateMsg || msg instanceof ForwardMsg) {
				toRemove.add(outMsgBean);
			}
		}

		for (OutgoingMessageBean outMsgBean : toRemove) {
			deleteOutMsgBean(outgoingMessages, outMsgBean);
		}
	}

	private void deleteOutMsgBean(List<OutgoingMessageBean> outgoingMessages,
			OutgoingMessageBean outMsgBean) {

		outgoingMessages.remove(outMsgBean);

		List<NSenseID> receiversList = outMsgBean.getReceivers();
		if (log.isDebugEnabled())
			log.debug("Before deletion of a receiver: " + receiversList);

		// delete the receiver from the receiversList in all other Messages,
		// where the same receiversList is used! This is a nice side effect of
		// the outgoingMessages.
		if (receiversList != null)
			receiversList.remove(outMsgBean.getContact().getOverlayID());

		if (log.isDebugEnabled())
			log.debug("After deletion of a receiver: " + receiversList);
	}

	private int getAllMsgSize(List<OutgoingMessageBean> outgoingMessages) {
		int sum = 0;
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			sum += msg.getSize() + Configuration.NETWORT_PROTOCOL_OVERHEAD
					+ Configuration.TRANSPORT_PROTOCOL_OVERHEAD;
		}
		return sum;

	}

	private int getNotToDeleteMsgSize(List<OutgoingMessageBean> outgoingMessages) {
		int sum = 0;
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			Message msg = outMsgBean.getMessage();
			if (msg instanceof SensorRequestMsg
					|| msg instanceof SensorResponseMsg
					|| msg instanceof ActionsMsg) {
				sum += msg.getSize() + Configuration.NETWORT_PROTOCOL_OVERHEAD
						+ Configuration.TRANSPORT_PROTOCOL_OVERHEAD;
			}
		}
		return sum;
	}

	private int getRoundLimit() {
		int roundLimitByte = Configuration.ROUND_BYTE_LIMIT;

		if (log.isDebugEnabled())
			log.debug("RoundLimit for this round is: " + roundLimitByte
					+ "Byte.");

		return roundLimitByte;
	}

	private void removeDuplicateMessages(
			List<OutgoingMessageBean> outgoingMessages) {
		for (int i = 0; i < outgoingMessages.size(); i++) {
			for (int j = i + 1; j < outgoingMessages.size(); j++) {

				if (outgoingMessages.get(i).equals(outgoingMessages.get(j))) {
					// remove
					OutgoingMessageBean rmMsgBean = outgoingMessages.remove(j);

					if (log.isDebugEnabled())
						log.debug("This Msg:          "
								+ outgoingMessages.get(i)
								+ "\nis a duplicate of: " + rmMsgBean);
					// counter will be decrement, but the actually [j] is
					// removed
					j--;
				}
			}
		}
	}

	private void sendMessages(List<OutgoingMessageBean> outgoingMessages) {
		for (OutgoingMessageBean outMsgBean : outgoingMessages) {
			NSenseContact receiverContact = outMsgBean.getContact();
			node.getTransLayer().send(outMsgBean.getMessage(),
					receiverContact.getTransInfo(), node.getPort(),
					Configuration.TRANSPORT_PROTOCOL);
		}
	}

	private void createActionMessages(
			List<OutgoingMessageBean> outgoingMessages, NSense localNSense,
			List<Integer> playerActions) {
		if (playerActions.size() > 0) {
			List<NSenseID> ignoreNodes = new Vector<NSenseID>();
			ignoreNodes.add(localNSense.getLocalNode());

			List<NSenseID> interestedNodes = localNSense.getAllNodesInArea(
					node.getPosition(), Configuration.ACTION_RANGE_RADIUS,
					ignoreNodes);

			for (NSenseID id : interestedNodes) {
				int SumActionSize = 0;
				for (Integer actionSize : playerActions) {
					SumActionSize += actionSize;
				}
				NSenseNodeInfo nodeInfo = localNSense.getNodeInfo(id);
				NSenseContact contact = nodeInfo.getContact();
				ActionsMsg msg = new ActionsMsg(Configuration.MAXIMAL_HOP,
						node.getSeqNr(), Configuration.ACTION_RANGE_RADIUS,
						node.getPosition(),node.getPositionDimensions(), SumActionSize);

				OutgoingMessageBean msgBean = new OutgoingMessageBean(contact,
						null, msg);
				outgoingMessages.add(msgBean);
			}
		}
	}

	private void createForwardMessages(
			List<OutgoingMessageBean> outgoingMessages, NSense localNSense,
			IncomingMessageList incomingMsg) {
		LinkedList<IncomingMessageBean> positionUpdateMsgs = incomingMsg
				.getPositionUpdateMsgs();
		for (IncomingMessageBean inMsgBean : positionUpdateMsgs) {
			AbstractPositionUpdateMsg updateMsg = (AbstractPositionUpdateMsg) inMsgBean
					.getMessage();
			if (updateMsg.getHopCount() <= 0)
				continue; // abort this msg, and check the next

			List<NSenseID> toForwardNodes;
			// TODO: Receiverslist von nodeInfo könnte man benutzen, um weniger
			// Forward msgs zu senden. (schauen ob es gleiche seqNr ist)

			// if it is a pos update from a sensor node and it exits no near
			// node, then should forwarded to the closest node.
			boolean nodeInArea = localNSense.existsNodeInArea(updateMsg
					.getPosition(), updateMsg.getRadius(), inMsgBean
					.getContact().getOverlayID());
			if (!nodeInArea
					&& localNSense.isSensorNode(inMsgBean.getContact()
							.getOverlayID())) {
				toForwardNodes = new Vector<NSenseID>();
				NSenseID closestNode = localNSense.getClosestNode(
						updateMsg.getPosition(), updateMsg.getReceiversList());
				if (closestNode != null)
					toForwardNodes.add(closestNode);

			} else {
				toForwardNodes = localNSense.getAllNodesInArea(
						updateMsg.getPosition(), updateMsg.getRadius(),
						updateMsg.getReceiversList());
			}

			// getReceiversList() get a copy of the list back!
			List<NSenseID> receivers = updateMsg.getReceiversList();
			// add the new receivers
			receivers.addAll(toForwardNodes);

			// create the msg
			byte hops = (byte) (updateMsg.getHopCount() - (byte) 1);
			SequenceNumber seqNr = updateMsg.getSequenceNr();
			int visionRangeRadius = updateMsg.getRadius();
			VectorN position = updateMsg.getPosition();
			VectorN positionDimensions = updateMsg.getPositionDimensions();

			NSenseContact contact = inMsgBean.getContact();

			ForwardMsg msg = new ForwardMsg(hops, seqNr, receivers,
					visionRangeRadius, position,positionDimensions, contact);

			// add the msgs to the outgoingMessages list
			for (NSenseID forwardID : toForwardNodes) {
				// get the contact information of the receiver
				NSenseContact receiverContact = localNSense.getNodeInfo(
						forwardID).getContact();
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						receiverContact, receivers, msg);
				outgoingMessages.add(outMsgBean);
			}

		}

	}

	private void createSensorResponses(
			List<OutgoingMessageBean> outgoingMessages, NSense localNSense,
			IncomingMessageList incomingMsg) {
		LinkedList<IncomingMessageBean> incomingSensorRequests = incomingMsg
				.getSensorRequestsMsgs();
		for (IncomingMessageBean inMsgBean : incomingSensorRequests) {
			AbstractNSenseMsg inMsg = inMsgBean.getMessage();

			SensorRequestMsg reqMsg = (SensorRequestMsg) inMsg;

			List<NSenseID> ignoreNodes = new Vector<NSenseID>();
			ignoreNodes.add(reqMsg.getSenderID());

			NSenseNodeInfo sensorNodeInfo = null;
			NSenseID sensorNode = null;
			byte tempHop = 0;

			// search a node, with enough hopCount
			do {
				NSenseID[] sensorNodes = localNSense.findSensorNodes(
						reqMsg.getPosition(), reqMsg.getRadius(), ignoreNodes);
				sensorNode = sensorNodes[reqMsg.getSectorID()];
				if (sensorNode == null) {
					sensorNode = localNSense.getLocalNode();
					if (log.isInfoEnabled())
						log.info("FindSensorNode give null back! Replaced with localNode for SensorRequest!");
				}

				sensorNodeInfo = localNSense.getNodeInfo(sensorNode);

				if (sensorNodeInfo != null)
					tempHop = sensorNodeInfo.getHops();
				else
					break;
				ignoreNodes.add(sensorNode);
			} while (tempHop <= 0);

			if (sensorNodeInfo != null) {

				// set sequence number.
				SequenceNumber seqNr;
				if (sensorNode == localNSense.getLocalNode()) {
					// in sensorNodeInfo of the localNode is an old sequence
					// Number, because that, take new node sequence number
					seqNr = node.getSeqNr();
				} else {
					seqNr = sensorNodeInfo.getSequenceNr();
					tempHop--;
				}

				SensorResponseMsg msg = new SensorResponseMsg(tempHop, seqNr,
						sensorNodeInfo.getVisionRangeRadius(),
						sensorNodeInfo.getPosition(),sensorNodeInfo.getPositionDimensions(), reqMsg.getSectorID(),
						sensorNodeInfo.getContact(), reqMsg.getSequenceNr());

				NSenseContact contact = inMsgBean.getContact();
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						contact, null, msg);
				outgoingMessages.add(outMsgBean);
			}
		}
	}

	private void createSensorRequests(
			List<OutgoingMessageBean> outgoingMessages, NSense localNSense) {
		NSenseID[] sensorNodes = localNSense.getSensorNodes();

		for (int i = 0; i < sensorNodes.length; i++) {
			NSenseID id = sensorNodes[i];
			if (id != null) {
				NSenseNodeInfo nodeInfo = localNSense.getNodeInfo(id);
				if (nodeInfo != null) {
					NSenseContact contact = nodeInfo.getContact();
					SensorRequestMsg msg = new SensorRequestMsg(
							node.getOverlayID(), Configuration.MAXIMAL_HOP,
							node.getSeqNr(), Configuration.VISION_RANGE_RADIUS,
							node.getPosition(),node.getPositionDimensions(), (byte) i);

					OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
							contact, null, msg);
					outgoingMessages.add(outMsgBean);
				} else {
					log.error("Inconsistent's in PSense! SensorNodes contains the id "
							+ id + " that are not stored in nodeStorage!");
				}
			}
		}

	}

	private void createPositionUpdateMessages(
			List<OutgoingMessageBean> outgoingMessages, NSense localNSense) {
		// create list with nearNodes and SensorNodes
		List<NSenseID> nodes = new Vector<NSenseID>();
		nodes.addAll(localNSense.getNearNodes());
		for (NSenseID id : localNSense.getSensorNodes()) {
			if (id != null && !nodes.contains(id))
				nodes.add(id);
		}

		// create receivers list, so that all msgs have the same list
		List<NSenseID> receivers = new Vector<NSenseID>();
		// add self to the receiverList
		receivers.add(node.getOverlayID());

		for (NSenseID id : nodes) {
			NSenseNodeInfo nodeInfo = localNSense.getNodeInfo(id);
			if (nodeInfo != null) {
				receivers.add(id);
				NSenseContact contact = nodeInfo.getContact();
				PositionUpdateMsg msg = new PositionUpdateMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), receivers,
						Configuration.VISION_RANGE_RADIUS, node.getPosition(), node.getPositionDimensions());
				OutgoingMessageBean outMsgBean = new OutgoingMessageBean(
						contact, receivers, msg);
				outgoingMessages.add(outMsgBean);
			} else {
				log.error("Inconsistent's in NSense! NearNodes or SensorNodes contains the id "
						+ id + " that are not stored in nodeStorage!");
			}
		}
	}

	private void updateNearNodeList(NSense localNSense) {
		localNSense.updateNearNodeList();
	}

	private void updateSensorNodeList(IncomingMessageList incomingMsg,
			NSense localNSense) {
		LinkedList<IncomingMessageBean> sensorResponesMsgs = incomingMsg
				.getSensorResponseMsgs();
		NSenseID[] newSensorNodes = computeNewSensorNodesFromResponses(sensorResponesMsgs);

		localNSense.updateSensorNodeList(newSensorNodes);
	}

	private NSenseID[] computeNewSensorNodesFromResponses(
			LinkedList<IncomingMessageBean> sensorResponesMsgs) {

		// filter the newest sensor node msg for every sector
		IncomingMessageBean[] temp = new IncomingMessageBean[Configuration.NUMBER_SECTORS];
		for (IncomingMessageBean msgBean : sensorResponesMsgs) {
			SensorResponseMsg msg = (SensorResponseMsg) msgBean.getMessage();
			int sector = msg.getSectorID();
			// if empty then add
			if (temp[sector] == null) {
				temp[sector] = msgBean;
			} else {
				// otherwise, look, which msg is newer
				SequenceNumber tempSeqNrReq = ((SensorResponseMsg) temp[sector]
						.getMessage()).getSequenceNrRequest();
				SequenceNumber msgSeqNrReq = msg.getSequenceNrRequest();

				if (msgSeqNrReq.isNewerAs(tempSeqNrReq)) {
					temp[sector] = msgBean;
				}
			}
		}

		// store the PSenseID to every sector
		NSenseID[] newSensorNodes = new NSenseID[temp.length];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null)
				newSensorNodes[i] = temp[i].getContact().getOverlayID();
		}
		return newSensorNodes;
	}

	@Override
	public Boolean getResult() {
		return connectedWithOverlay;
	}

	public void stopOperation() {
		if (log.isDebugEnabled())
			log.debug("The Round Operation is stopped for node: "
					+ node.getOverlayID());
		operationFinished(false);
	}
}
