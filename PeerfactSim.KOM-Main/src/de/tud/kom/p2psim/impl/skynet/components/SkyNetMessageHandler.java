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


package de.tud.kom.p2psim.impl.skynet.components;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.service.skynet.SkyNetEventType;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.SkyNetSimulationType;
import de.tud.kom.p2psim.api.service.skynet.SkyNetSimulationType.SimulationType;
import de.tud.kom.p2psim.api.service.skynet.SupportPeer;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.KBRKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.SkyNetEventObject;
import de.tud.kom.p2psim.impl.skynet.SkyNetHostProperties;
import de.tud.kom.p2psim.impl.skynet.SkyNetPropertiesReader;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.AttributeUpdateACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.AttributeUpdateMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.ParentCoordinatorInformationACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.ParentCoordinatorInformationMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerRequestACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerRequestMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerUpdateACKMsg;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.SupportPeerUpdateMsg;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsInterpretation;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsSubCoordinatorInfo;
import de.tud.kom.p2psim.impl.skynet.metrics.messages.MetricUpdateACKMsg;
import de.tud.kom.p2psim.impl.skynet.metrics.messages.MetricUpdateMsg;
import de.tud.kom.p2psim.impl.skynet.metrics.messages.MetricUpdateSyncMsg;
import de.tud.kom.p2psim.impl.skynet.queries.messages.QueryForwardACKMsg;
import de.tud.kom.p2psim.impl.skynet.queries.messages.QueryForwardMsg;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is responsible for the treatment of incoming SkyNet-messages. It
 * implements the {@link TransMessageListener}-interface, which explains, how
 * the messages are received and delivered to the appropriate component.<br>
 * For every message, which is received by <code>SkyNetMessageHandler</code>,
 * the MessageHandler responds with the corresponding ACK.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetMessageHandler implements TransMessageListener {

	private static Logger log = SimLogger.getLogger(SkyNetMessageHandler.class);

	private final SkyNetNodeInterface skyNetNode;

	private final SupportPeer supportPeer;

	private long errorTimestamp;

	private boolean tryingJoin;

	private final HashMap<BigDecimal, QueryForwardACKMsg> queryMsgCounter;

	private final boolean alwaysPushSystemStatistics;

	public void setTryingJoin(boolean tryingJoin) {
		this.tryingJoin = tryingJoin;

	}

	public SkyNetMessageHandler(SkyNetNodeInterface skyNetNode,
			SupportPeer supportPeer) {
		alwaysPushSystemStatistics = SkyNetPropertiesReader.getInstance()
				.getBooleanProperty("AlwaysPushSystemStatistics");
		this.skyNetNode = skyNetNode;
		this.supportPeer = supportPeer;
		tryingJoin = false;
		queryMsgCounter = new HashMap<BigDecimal, QueryForwardACKMsg>();
	}

	public void messageArrived(TransMsgEvent receivingEvent) {
		if (((AbstractOverlayNode) skyNetNode.getOverlayNode()).getPeerStatus()
				.equals(PeerStatus.PRESENT)
				|| (skyNetNode.getOverlayNode() instanceof KBRKademliaNode && ((KBRKademliaNode) skyNetNode
						.getOverlayNode()).getPeerStatus().equals(
						PeerStatus.PRESENT))) {
			long timestamp = Simulator.getCurrentTime();
			Message msg = receivingEvent.getPayload();
			if (msg instanceof MetricUpdateMsg) {
				MetricUpdateMsg request = (MetricUpdateMsg) msg;
				processMetricUpdate(request, receivingEvent, timestamp);
			} else if (msg instanceof ParentCoordinatorInformationMsg) {
				ParentCoordinatorInformationMsg request = (ParentCoordinatorInformationMsg) msg;
				processParentCoordinatorInfo(request, receivingEvent, timestamp);
			} else if (msg instanceof AttributeUpdateMsg) {
				AttributeUpdateMsg request = (AttributeUpdateMsg) msg;
				processAttributeUpdate(request, receivingEvent, timestamp,
						request.isReceiverSP());
			} else if (msg instanceof SupportPeerRequestMsg) {
				SupportPeerRequestMsg request = (SupportPeerRequestMsg) msg;
				processSupportPeerRequest(request, receivingEvent, timestamp);
			} else if (msg instanceof SupportPeerUpdateMsg) {
				SupportPeerUpdateMsg request = (SupportPeerUpdateMsg) msg;
				processSupportPeerUpdate(request, receivingEvent);
			} else if (msg instanceof MetricUpdateSyncMsg) {
				MetricUpdateSyncMsg request = (MetricUpdateSyncMsg) msg;
				processMetricUpdateSyncMsg(request, receivingEvent);
			} else if (msg instanceof QueryForwardMsg) {
				QueryForwardMsg request = (QueryForwardMsg) msg;
				BigDecimal senderID = request.getSenderNodeInfo().getSkyNetID()
						.getID();

				// This if-block is used to test if (a) a new query-message is
				// received, which must be normally processed or if (b) an old
				// query-message is received, that was already processed. If
				// case (b) is encountered, an ack is directly retransmitted to
				// satisfy the originator of the query-message
				if (queryMsgCounter.containsKey(senderID)) {
					if (queryMsgCounter.get(senderID).getSkyNetMsgID() < request
							.getSkyNetMsgID()) {
						processQueryForwardMsg(request, receivingEvent);
					} else {
						log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
								+ "Recreate ACK-message with ID "
								+ request.getSkyNetMsgID()
								+ " instead of ID "
								+ queryMsgCounter.get(senderID)
										.getSkyNetMsgID()
								+ " for "
								+ SkyNetUtilities.getNetID(queryMsgCounter.get(
										senderID).getReceiverNodeInfo()));
						Message reply = new QueryForwardACKMsg(request
								.getReceiverNodeInfo(), request
								.getSenderNodeInfo(), request.getSkyNetMsgID(),
								request.isSenderSP(), request.isReceiverSP());
						supportPeer.getTransLayer().sendReply(reply,
								receivingEvent, skyNetNode.getPort(),
								TransProtocol.UDP);
					}
				} else {
					processQueryForwardMsg(request, receivingEvent);
				}

			} else {
				log.warn("Received unknown message type");
			}

		} else {
			if (((AbstractOverlayNode) skyNetNode.getOverlayNode())
					.getPeerStatus().equals(PeerStatus.ABSENT)
					|| (skyNetNode.getOverlayNode() instanceof KBRKademliaNode && ((KBRKademliaNode) skyNetNode
							.getOverlayNode()).getPeerStatus().equals(
							PeerStatus.ABSENT))) {
				if (skyNetNode.getHost().getNetLayer().isOnline()) {
					if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.NAPSTER_SIMULATION)) {
						manualJoin();
					} else if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.CHORD_SIMULATION)) {
						log.fatal("A manualJoin-method for Chord is needed");
					} else if (SkyNetSimulationType.getSimulationType().equals(
							SimulationType.KADEMLIA_SIMULATION)) {
						/*
						 * if (((KBRKademliaNode) skyNetNode.getOverlayNode())
						 * .getPeerStatus().equals(PeerStatus.ABSENT)) { log
						 * .fatal("A manualJoin-method for Kademlia is needed");
						 * }
						 */
					} else {
						log.error("Unknown SimulationType");
					}
				} else {
					log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ " is absent and offline");
				}
			}
		}
	}

	private void processMetricUpdateSyncMsg(MetricUpdateSyncMsg request,
			TransMsgEvent receivingEvent) {
		if (request.getLastMetricSync()
				- skyNetNode.getMetricUpdateStrategy().getLastMetricSync() >= skyNetNode
				.getMetricUpdateStrategy().getMetricSyncInterval()) {

			long lastMetricSync = request.getLastMetricSync();

			long updateIntervalOffset = 0;
			if ((request.getUpdateIntervalOffset() - (0.5 * skyNetNode
					.getMetricUpdateStrategy().getMetricIntervalDecrease())) >= 0) {
				updateIntervalOffset = request.getUpdateIntervalOffset()
						- skyNetNode.getMetricUpdateStrategy()
								.getMetricIntervalDecrease();
			} else {
				updateIntervalOffset = request.getUpdateIntervalOffset();
			}

			skyNetNode.getMetricUpdateStrategy().setLastMetricSync(
					lastMetricSync);
			skyNetNode.getMetricUpdateStrategy().scheduleNextUpdateEventAt(
					updateIntervalOffset);
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "rescheduled new metricUpdate with offset = "
					+ updateIntervalOffset);
			HashMap<BigDecimal, MetricsSubCoordinatorInfo> subCoMap = skyNetNode
					.getMetricUpdateStrategy().getStorage()
					.getListOfSubCoordinators();
			Iterator<BigDecimal> iter = subCoMap.keySet().iterator();
			MetricsSubCoordinatorInfo subCoInfo = null;
			while (iter.hasNext()) {
				subCoInfo = subCoMap.get(iter.next());
				Message msg = new MetricUpdateSyncMsg(skyNetNode
						.getSkyNetNodeInfo(), subCoInfo.getNodeInfo(),
						updateIntervalOffset, lastMetricSync, skyNetNode
								.getMessageCounter()
								.assignmentOfMessageNumber());
				skyNetNode.getHost().getTransLayer().send(msg,
						subCoInfo.getNodeInfo().getTransInfo(),
						skyNetNode.getPort(), TransProtocol.UDP);
			}
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received old sync-msg");
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link QueryForwardMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processQueryForwardMsg(QueryForwardMsg request,
			TransMsgEvent receivingEvent) {
		// create reply- is done before processing to reply just in time
		Message reply = new QueryForwardACKMsg(request.getReceiverNodeInfo(),
				request.getSenderNodeInfo(), request.getSkyNetMsgID(), request
						.isSenderSP(), request.isReceiverSP());
		supportPeer.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
		BigDecimal senderID = request.getSenderNodeInfo().getSkyNetID().getID();
		queryMsgCounter.put(senderID, (QueryForwardACKMsg) reply);
		// start real processing of the message
		if (request.isSolved()) {
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received queryAnswer");
			skyNetNode.getQueryHandler().processQueryResult(request);
		} else {
			if (request.isReceiverSP()) {
				log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "as SP received queryForwardMsg");
				supportPeer.getSPQueryHandler().processForeignQuery(request);
			} else {
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "as Co received queryForwardMsg");
				skyNetNode.getQueryHandler().processForeignQuery(request);
			}
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link SupportPeerUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processSupportPeerUpdate(SupportPeerUpdateMsg request,
			TransMsgEvent receivingEvent) {
		if (supportPeer.isSupportPeer()) {
			supportPeer.getSPAttributeUpdateStrategy().setBrotherCoordinator(
					request.getSenderNodeInfo());
			supportPeer.getSPAttributeUpdateStrategy().setParentCoordinator(
					request.getParentCoordinatorInfo());
			log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "processed SupportPeerUpdate from "
					+ SkyNetUtilities.getNetID(request.getSenderNodeInfo()));
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "received SupportPeerUpdate from "
					+ SkyNetUtilities.getNetID(request.getSenderNodeInfo())
					+ ", but does not process the message"
					+ ", since it is no SupportPeer");

		}
		// create reply
		Message reply = new SupportPeerUpdateACKMsg(request
				.getReceiverNodeInfo(), request.getSenderNodeInfo(), request
				.getSkyNetMsgID(), supportPeer.isSupportPeer());
		supportPeer.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	/**
	 * This private method is called if a message of the type
	 * {@link SupportPeerUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processSupportPeerRequest(SupportPeerRequestMsg request,
			TransMsgEvent receivingEvent, long timestamp) {
		if (skyNetNode.isSupportPeer()) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " Peer already SupportPeer");
			// create reply
			Message msg = new SupportPeerRequestACKMsg(request
					.getReceiverNodeInfo(), request.getSenderNodeInfo(), null,
					false, request.getSkyNetMsgID(), true);
			skyNetNode.getTransLayer().sendReply(msg, receivingEvent,
					skyNetNode.getPort(), TransProtocol.UDP);
		} else {
			log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " SupportPeer-Creation");
			supportPeer.setSupportPeer(true);
			supportPeer.getSPAttributeUpdateStrategy()
					.setProcessSupportPeerEvents(true);
			supportPeer.getSPAttributeUpdateStrategy().setBrotherCoordinator(
					request.getSenderNodeInfo());
			supportPeer.getSPAttributeUpdateStrategy().setParentCoordinator(
					request.getParentCoordinator());

			// Schedule next attribute-update
			long time = Simulator.getCurrentTime();
			supportPeer.getSPAttributeUpdateStrategy().setSendingTime(time);
			long attributeTime = time
					+ (supportPeer.getSPAttributeUpdateStrategy()
							.getUpdateInterval());
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.SUPPORT_PEER_UPDATE, time), attributeTime,
					supportPeer, null);

			// create reply
			Message msg = new SupportPeerRequestACKMsg(request
					.getReceiverNodeInfo(), request.getSenderNodeInfo(),
					skyNetNode.getSkyNetNodeInfo(), true, request
							.getSkyNetMsgID(), true);
			supportPeer.getTransLayer().sendReply(msg, receivingEvent,
					supportPeer.getPort(), TransProtocol.UDP);
		}
	}

	/**
	 * This private method is called if a message of the type
	 * {@link ParentCoordinatorInformationMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processParentCoordinatorInfo(
			ParentCoordinatorInformationMsg request,
			TransMsgEvent receivingEvent, long timestamp) {

		if (request.isReceiverSP()) {
			// receiver is SupportPeer
			if (supportPeer.getSPAttributeUpdateStrategy()
					.getParentCoordinator() != null) {
				if (request.getSenderNodeInfo().getSkyNetID().getID()
						.compareTo(
								supportPeer.getSPAttributeUpdateStrategy()
										.getParentCoordinator().getSkyNetID()
										.getID()) == 0) {
					log.debug(supportPeer.getSkyNetNodeInfo().getTransInfo()
							.getNetId().toString()
							+ " Received as SupportPeer "
							+ request.getClass().getSimpleName());
					supportPeer.getSPAttributeUpdateStrategy()
							.processParentCoordinatorInfo(request);
				} else {
					log.debug(Simulator.getFormattedTime(Simulator
							.getCurrentTime())
							+ " "
							+ skyNetNode.getSkyNetNodeInfo().getTransInfo()
									.getNetId().toString()
							+ " Received ParentCoordinatorInfo of unknown "
							+ request.getSenderNodeInfo().getTransInfo()
									.getNetId().toString());
				}
			} else {
				supportPeer.getSPAttributeUpdateStrategy()
						.processParentCoordinatorInfo(request);
			}
		} else {
			if (skyNetNode.getAttributeUpdateStrategy()
					.getReceiverOfNextUpdate() != null) {
				if (request.getSenderNodeInfo().getSkyNetID().getID()
						.compareTo(
								skyNetNode.getAttributeUpdateStrategy()
										.getReceiverOfNextUpdate()
										.getSkyNetID().getID()) == 0) {
					// receiver is SubCoordinator
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ " Received as SubCoordinator "
							+ request.getClass().getSimpleName());
					skyNetNode.getAttributeUpdateStrategy()
							.processParentCoordinatorInfo(request);
				} else {
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "Received ParentCoordinatorInfo of unknown "
							+ request.getSenderNodeInfo().getTransInfo()
									.getNetId().toString());
				}
			} else {
				skyNetNode.getAttributeUpdateStrategy()
						.processParentCoordinatorInfo(request);
			}
		}

		// create reply
		Message reply = new ParentCoordinatorInformationACKMsg(request
				.getReceiverNodeInfo(), request.getSenderNodeInfo(), request
				.getSkyNetMsgID(), request.isReceiverSP());
		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	/**
	 * This private method is called if a message of the type
	 * {@link AttributeUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processAttributeUpdate(AttributeUpdateMsg request,
			TransMsgEvent receivingEvent, long timestamp, boolean toSupportPeer) {
		// give message to Coordinator or SupportPeer
		if (toSupportPeer) {
			log.debug(supportPeer.getSkyNetNodeInfo().getTransInfo().getNetId()
					.toString()
					+ " Received as SupportPeer "
					+ request.getClass().getSimpleName());
			supportPeer.getSPAttributeInputStrategy().processUpdateMessage(
					request, timestamp);
		} else {
			log.debug("Received as ParentCoordinator "
					+ request.getClass().getSimpleName());
			skyNetNode.getAttributeInputStrategy().processUpdateMessage(
					request, timestamp);
		}

		// create reply
		Message reply;
		reply = new AttributeUpdateACKMsg(request.getReceiverNodeInfo(),
				request.getSenderNodeInfo(), request.getSkyNetMsgID(), request
						.isSenderSP(), toSupportPeer);
		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);

	}

	/**
	 * This private method is called if a message of the type
	 * {@link MetricUpdateMsg} was received.
	 * 
	 * @param request
	 *            contains the received message.
	 * @param receivingEvent
	 *            contains the event, which encapsulates the message and other
	 *            information concerning that receiving-event
	 */
	private void processMetricUpdate(MetricUpdateMsg request,
			TransMsgEvent receivingEvent, long timestamp) {
		log.debug("Received " + request.getClass().getSimpleName() + ": "
				+ request.toString());
		skyNetNode.getMetricInputStrategy().processUpdateMessage(request,
				timestamp);
		Message reply = null;
		MetricsInterpretation temp = skyNetNode.getMetricsInterpretation();

		// create reply depending on the definition for pushing the
		// systemStatistics and the DHTParamManipulator
		if (alwaysPushSystemStatistics) {
			reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
					request.getSenderNodeInfo(), temp
							.getActualSystemStatistics(), temp
							.getParaManipulator(), temp
							.getStatisticsTimestamp(), temp
							.getManipulatorTimestamp(), request
							.getSkyNetMsgID(), skyNetNode.getSkyNetNodeInfo()
							.getObservedLevelFromRoot());
		} else {
			HashMap<BigDecimal, MetricsSubCoordinatorInfo> list = skyNetNode
					.getMetricInputStrategy().getMetricStorage()
					.getListOfSubCoordinators();
			MetricsSubCoordinatorInfo subCo = list.get(request
					.getSenderNodeInfo().getSkyNetID().getID());
			if (subCo.isNeedsUpdate()) {
				// In this case the selected SubCoordinator has no actual
				// systemStatistics nor DHTParaManipulator. So the new
				// information
				// is piggybacked with the MetricUpdateACKMsg
				reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
						request.getSenderNodeInfo(), temp
								.getActualSystemStatistics(), temp
								.getParaManipulator(), temp
								.getStatisticsTimestamp(), temp
								.getManipulatorTimestamp(), request
								.getSkyNetMsgID(), skyNetNode
								.getSkyNetNodeInfo().getObservedLevelFromRoot());
				subCo.setNeedsUpdate(false);
				list.put(request.getSenderNodeInfo().getSkyNetID().getID(),
						subCo);
				skyNetNode.getMetricInputStrategy().getMetricStorage()
						.setListOfSubCoordinators(list);

			} else {
				// No need to send systemStatistics nor DHTParaManipulator
				reply = new MetricUpdateACKMsg(request.getReceiverNodeInfo(),
						request.getSenderNodeInfo(), null, null, -1, -1,
						request.getSkyNetMsgID(), skyNetNode
								.getSkyNetNodeInfo().getObservedLevelFromRoot());
			}
		}

		skyNetNode.getTransLayer().sendReply(reply, receivingEvent,
				skyNetNode.getPort(), TransProtocol.UDP);
	}

	private void manualJoin() {
		long currentTime = Simulator.getCurrentTime();
		if (!tryingJoin) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "is absent but online, "
					+ "so setting timer for possible manual join");
			tryingJoin = true;
			errorTimestamp = currentTime;
		} else if (currentTime - errorTimestamp > (80 * Simulator.SECOND_UNIT)) {
			NapsterClientNode napsterNode = ((NapsterClientNode) skyNetNode
					.getOverlayNode());
			napsterNode.setPeerStatus(PeerStatus.PRESENT);

			// Create ServerOverlayContact, since this was never
			// done before
			if (napsterNode.getServerOverlayContact() == null) {
				NapsterOverlayContact serverOverlayContact = new NapsterOverlayContact(
						(NapsterOverlayID) napsterNode.getServer()
								.getOverlayID(), napsterNode
								.getServerTransInfo());
				napsterNode.setServerOverlayContact(serverOverlayContact);
			} else if (napsterNode.getServerOverlayContact().getOverlayID() == null) {
				NapsterOverlayContact serverOverlayContact = new NapsterOverlayContact(
						(NapsterOverlayID) napsterNode.getServer()
								.getOverlayID(), napsterNode
								.getServerTransInfo());
				napsterNode.setServerOverlayContact(serverOverlayContact);
			}

			((SkyNetNode) skyNetNode).setPresentTime(currentTime);
			// Schedule next metric-update
			skyNetNode.getMetricUpdateStrategy().setSendingTime(currentTime);
			long delta = currentTime
					% skyNetNode.getMetricUpdateStrategy().getUpdateInterval();
			delta = skyNetNode.getMetricUpdateStrategy().getUpdateInterval()
					- delta;
			long metricsTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.METRICS_UPDATE, currentTime), metricsTime,
					skyNetNode, null);

			// Schedule next attribute-update
			skyNetNode.getAttributeUpdateStrategy().setSendingTime(currentTime);
			delta = currentTime
					% skyNetNode.getAttributeUpdateStrategy()
							.getUpdateInterval();
			delta = skyNetNode.getAttributeUpdateStrategy().getUpdateInterval()
					- delta;
			long attributeTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.ATTRIBUTE_UPDATE, currentTime),
					attributeTime, skyNetNode, null);

			// Schedule next query-remainder
			delta = currentTime
					% ((SkyNetNode) skyNetNode).getQueryRemainderTime();
			delta = ((SkyNetNode) skyNetNode).getQueryRemainderTime() - delta;
			long queryRemainderStartTime = currentTime + delta;
			Simulator.scheduleEvent(new SkyNetEventObject(
					SkyNetEventType.QUERY_REMAINDER, currentTime),
					queryRemainderStartTime, skyNetNode, null);

			// other inits
			((SkyNetHostProperties) skyNetNode.getHost().getProperties())
					.init();

			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "tries to go PRESENT manually");
			tryingJoin = false;
		}
	}

}
