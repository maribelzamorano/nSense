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



package de.tud.kom.p2psim.impl.transport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.network.NetMessageListener;
import de.tud.kom.p2psim.api.network.NetMsgEvent;
import de.tud.kom.p2psim.api.network.NetProtocol;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The standard transport layer implementation.
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DefaultTransLayer implements TransLayer, SimulationEventHandler,
		NetMessageListener {

	static Logger log = SimLogger.getLogger(DefaultTransLayer.class);

	private final Map<Short, List<TransMessageListener>> transMessageListeners;

	private final Map<Integer, TransMessageCallback> replyCallbacks;

	private final Map<Long, Long> sequenzNumber;

	private final Map<Long, Set<TCPMessage>> queuedMessages;

	private final NetLayer netLayer;

	private Host host;

	private int lastCommId;

	/**
	 * @param netLayer
	 *            the network layer instance to be used by this transport layer
	 *            instance
	 */
	public DefaultTransLayer(NetLayer netLayer) {
		this.transMessageListeners = new HashMap<Short, List<TransMessageListener>>();
		this.replyCallbacks = new HashMap<Integer, TransMessageCallback>();
		this.netLayer = netLayer;
		this.netLayer.addNetMsgListener(this);
		this.sequenzNumber = new HashMap<Long, Long>();
		queuedMessages = new HashMap<Long, Set<TCPMessage>>();
	}

	@Override
	public void messageArrived(NetMsgEvent ne) {
		NetID sender = ne.getSender();
		AbstractTransMessage message = (AbstractTransMessage) ne.getPayload();

		TransInfo senderInfo = getAddress(sender, message.getSenderPort());
		TransInfo receiverInfo = getAddress(netLayer.getNetID(),
				message.getReceiverPort());

		if (message.getProtocol() == TransProtocol.TCP) {

			long connectionHash = senderInfo.hashCode();
			connectionHash = (connectionHash << 32) + receiverInfo.hashCode();
			if (!sequenzNumber.containsKey(connectionHash))
				sequenzNumber.put(connectionHash, 0L);

			TCPMessage currentMessage = (TCPMessage) message;
			long sn = sequenzNumber.get(connectionHash);

			if (currentMessage.getSequenzNumber() != sn) {
				if (!queuedMessages.containsKey(connectionHash))
					queuedMessages.put(connectionHash,
							new HashSet<TCPMessage>());
				queuedMessages.get(connectionHash).add(currentMessage);
			} else {
				receive(message, sender);
				sequenzNumber.put(connectionHash, ++sn);
			}

			if (queuedMessages.containsKey(connectionHash)) {
				boolean ok = true;
				while (ok) {
					ok = false;
					for (TCPMessage msg : queuedMessages.get(connectionHash)) {
						if (msg.getSequenzNumber() == sn) {
							receive(msg, sender);
							sequenzNumber.put(connectionHash, ++sn);
							ok = true;
						}
					}
				}
			}
		} else if (message.getProtocol() == TransProtocol.UDP) {
			receive(message, sender);
		}
	}

	private void receive(AbstractTransMessage transMsg, NetID sender) {
		// Inform monitors about transport layer message receive
		Simulator.getMonitor().transMsgReceived(transMsg);

		TransInfo senderInfo = getAddress(sender, transMsg.getSenderPort());

		Message payload = transMsg.getPayload();
		
		log.debug(Simulator.getSimulatedRealtime() + " Receiving " + transMsg);
		if (transMsg.isReply()) {
			TransMessageCallback responseCallback = replyCallbacks
					.remove(transMsg.getCommId());
			log.debug("callback for commID=" + transMsg.getCommId() + " is "
					+ responseCallback);
			if (responseCallback != null)
				responseCallback.receive(payload, senderInfo,
						transMsg.getCommId());
		} else {
			List<TransMessageListener> messageReceiver = this.transMessageListeners
					.get(transMsg.getReceiverPort());
			if (messageReceiver == null || messageReceiver.size() == 0) {
				log.debug("Message " + transMsg
						+ " cannot be dispatched as no receiver is registered");
			} else {
				for (TransMessageListener listener : messageReceiver) {
					if (log.isDebugEnabled())
						log.debug("Dispatching message " + transMsg + " to "
								+ listener);
					listener.messageArrived(new TransMsgEvent(transMsg,
							senderInfo, this));
				}
			}
		}
	}

	@Override
	public void addTransMsgListener(TransMessageListener receiver, short port) {
		if (transMessageListeners.get(port) == null) {
			transMessageListeners.put(port,
					new LinkedList<TransMessageListener>());
		}
		transMessageListeners.get(port).add(receiver);
		log.debug("TransLayer (netId=" + this.netLayer.getNetID()
				+ ") - register new message listener " + receiver);
	}

	@Override
	public int send(Message msg, TransInfo receiverInfo, short senderPort,
			TransProtocol protocol) {
		return sendAndWait(msg, receiverInfo, senderPort, protocol, null, -1);
	}

	@Override
	public int sendAndWait(Message msg, TransInfo receiverInfo,
			short senderPort, TransProtocol protocol,
			TransMessageCallback senderCallback, long timeout) {
		if (receiverInfo.getNetId() == null)
			throw new IllegalArgumentException(
					"NetID of the receiver may not be null");

		int commId = sendTransportMsg(msg, receiverInfo, senderPort, protocol,
				-1, false);

		if (senderCallback != null) {
			replyCallbacks.put(commId, senderCallback);
			log.debug("Reply callback for " + msg + " is " + senderCallback);
			if (timeout > 0) {
				scheduleResponseTimeout(timeout, commId);
			}
		}
		return commId;
	}

	/**
	 * 
	 * @param msg
	 * @param receiverInfo
	 * @param senderPort
	 * @param protocol
	 * @param commId
	 *            if commId == -1 a new id is generated by the NetLayer
	 * @param isReply
	 */
	private int sendTransportMsg(Message msg, TransInfo receiverInfo,
			short senderPort, TransProtocol protocol, int commId,
			boolean isReply) {
		AbstractTransMessage transMsg;

		switch (protocol) {
		case UDP:
			transMsg = new UDPMessage(msg, senderPort, receiverInfo.getPort(),
					commId, isReply);
			// Inform monitors about transport layer message send
			Simulator.getMonitor().transMsgSent(transMsg);
			break;

		case TCP:
			TransInfo senderInfo = getLocalTransInfo(senderPort);
			long connectionHash = senderInfo.hashCode();
			connectionHash = (connectionHash << 32) + receiverInfo.hashCode();
			if (!sequenzNumber.containsKey(connectionHash))
				sequenzNumber.put(connectionHash, 0L);
			long sn = sequenzNumber.get(connectionHash);
			transMsg = new TCPMessage(msg, senderPort, receiverInfo.getPort(),
					commId, isReply, sn);
			// Inform monitors about transport layer message send
			Simulator.getMonitor().transMsgSent(transMsg);
			sequenzNumber.put(connectionHash, ++sn);
			break;

		default:
			throw new IllegalArgumentException("Unknown transport protocol");
		}

		log.info(Simulator.getSimulatedRealtime() + " Sending " + transMsg);

		netLayer.send(transMsg, receiverInfo.getNetId(), NetProtocol.IPv4);
		lastCommId = transMsg.getCommId();
		return lastCommId;
	}

	@Override
	public int sendReply(Message msg, TransMsgEvent receivingEvent,
			short senderPort, TransProtocol protocol) {
		return sendTransportMsg(msg, receivingEvent.getSenderTransInfo(),
				senderPort, protocol, receivingEvent.getCommId(), true);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.TIMEOUT_EXPIRED)) {
			int commId = (Integer) se.getData();
			TransMessageCallback receiver = this.replyCallbacks.get(commId);
			if (receiver != null) {
				log.debug("response timeout occured " + se);
				receiver.messageTimeoutOccured(commId);
				replyCallbacks.remove(commId);
			}
		} else
			log.error("Invalid event occured " + se);
	}

	private void scheduleResponseTimeout(long latency, int commId) {
		long arrivalTime = Simulator.getCurrentTime() + latency;
		Simulator.scheduleEvent(new Integer(commId), arrivalTime, this,
				SimulationEvent.Type.TIMEOUT_EXPIRED);
	}

	private TransInfo getAddress(NetID netID, short port) {
		return DefaultTransInfo.getTransInfo(netID, port);
	}

	@Override
	public TransInfo getLocalTransInfo(short port) {
		return DefaultTransInfo.getTransInfo(this.netLayer.getNetID(), port);
	}

	@Override
	public void removeTransMsgListener(TransMessageListener receiver, short port) {
		List<TransMessageListener> portListeners = transMessageListeners
				.get(port);
		if (portListeners != null)
			portListeners.remove(receiver);
	}

	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public void cancelTransmission(int commId) {
		netLayer.cancelTransmission(commId);
	}

	@Override
	public int getLastCommunicationId() {
		return lastCommId;
	}

}
