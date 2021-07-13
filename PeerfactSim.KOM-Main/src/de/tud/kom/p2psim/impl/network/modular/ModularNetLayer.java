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


package de.tud.kom.p2psim.impl.network.modular;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Monitor.Reason;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.network.NetMessageListener;
import de.tud.kom.p2psim.api.network.NetMsgEvent;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.network.NetProtocol;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.network.AbstractNetLayer;
import de.tud.kom.p2psim.impl.network.IPv4Message;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB.City;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB.Country;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB.Region;
import de.tud.kom.p2psim.impl.network.modular.device.HostDevice;
import de.tud.kom.p2psim.impl.network.modular.livemon.NetLayerLiveMonitoring;
import de.tud.kom.p2psim.impl.network.modular.st.FragmentingStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.JitterStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.LatencyStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.PLossStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.PacketSizingStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.PositioningStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.TrafficControlStrategy;
import de.tud.kom.p2psim.impl.network.modular.st.TrafficControlStrategy.IReceiveContext;
import de.tud.kom.p2psim.impl.network.modular.st.TrafficControlStrategy.ISendContext;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * <p>
 * The Modular Network layer aims at being most flexible by allowing every
 * aspect of a network layer to be modeled on its own - as a module. Like
 * "building blocks", these modules can be customized in the simulator
 * configuration file and put together to form the whole network layer.
 * </p>
 * 
 * <p>
 * The Modular Network Layer currently supports modules of the type
 * <ul>
 * <li>Fragmenting
 * <li>Jitter
 * <li>Latency
 * <li>Packet Sizing
 * <li>Packet Loss
 * <li>Positioning
 * <li>Traffic Control
 * </ul>
 * </p>
 * 
 * <p>
 * For information how to configure a modular network layer, please consult
 * ModularNetLayerFactory.
 * </p>
 * <p>
 * To understand particular module types and to write a module yourself, please
 * consult its abstract strategy class.
 * </p>
 * 
 * @see FragmentingStrategy
 * @see JitterStrategy
 * @see LatencyStrategy
 * @see PacketSizingStrategy
 * @see PLossStrategy
 * @see PositioningStrategy
 * @see TrafficControlStrategy
 * @see ModularNetLayerFactory
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ModularNetLayer extends AbstractNetLayer {

	static Logger log = SimLogger.getLogger(ModularNetLayer.class);

	private NetMeasurementDB.Host hostMeta;

	private AbstractModularSubnet subnet;

	private HostDevice device;

	/**
	 * Creates a new Modular Network layer. This should be only called from the
	 * ModularNetLayerFactory
	 * 
	 * @param subnet
	 * @param maxBW
	 * @param hostMeta
	 * @param position
	 * @param id
	 * @param device
	 *            Device-Type of this Host
	 */
	ModularNetLayer(AbstractModularSubnet subnet, Bandwidth maxBW,
			NetMeasurementDB.Host hostMeta, NetPosition position, IPv4NetID id,
			HostDevice device) {
		super(maxBW, position);
		this.hostMeta = hostMeta;
		this.myID = id;
		this.subnet = subnet;
		this.online = true;
		subnet.registerNetLayer(this);
		if (device == null) {
			this.device = new HostDevice();
		} else {
			this.device = device;
		}
		this.device.setNet(this);
		getSubnet().netLayerWentOnline(this);
	}


	public String toString() {

		if (hostMeta != null) {

			City city = hostMeta.getCity();
			Region region = city.getRegion();
			Country country = region.getCountry();

			return "ModNetLayer(" + myID + ", " + city + ", " + region + ", "
					+ country + ")";

		}

		return "ModNetLayer(" + myID + " (no location info))";
	}

	@Override
	protected boolean isSupported(TransProtocol protocol) {
		return protocol.equals(TransProtocol.UDP);
	}

	Object trafCtrlMetadata = null;

	@Override
	@Deprecated
	public void cancelTransmission(int commId) {
		// What is that for?
		throw new IllegalArgumentException(
				"cancelTransmission(int) currently unusable by the modular network layer.");
	}

	@Override
	public void send(Message msg, NetID receiver, NetProtocol protocol) {
		if (!(msg instanceof AbstractTransMessage))
			throw new AssertionError(
					"Can only send messages of class AbstractTransMessage or "
							+ "subclasses of it through the network layer, but the message class was "
							+ msg.getClass().getSimpleName());
		if (protocol != NetProtocol.IPv4)
			throw new AssertionError(
					"Currently, the simulator only supports IPv4. "
							+ msg.getClass().getSimpleName());

		if (this.isOnline()) {
			TransProtocol usedTransProtocol = ((AbstractTransMessage) msg)
					.getProtocol();
			if (this.isSupported(usedTransProtocol)) {
				NetMessage netMsg = new ModularNetMessage(msg, receiver,
						this.myID, subnet.getStrategies(), NetProtocol.IPv4); // IPv6
																				// currently
																				// not
																				// supported
				NetLayerLiveMonitoring.getOfflineMsgDrop().noDropMessage();
				subnet.getStrategies()
						.getTrafficControlStrategy()
						.onSendRequest(this.new SendContextImpl(), netMsg,
								receiver);

				if (((AbstractTransMessage) msg).getCommId() == -1) {
					int assignedMsgId = subnet.determineTransMsgNumber(msg);
					((AbstractTransMessage) msg).setCommId(assignedMsgId);
				}
			} else
				throw new IllegalArgumentException("Transport protocol "
						+ usedTransProtocol
						+ " not supported by this NetLayer implementation.");
		} else {
			int assignedMsgId = subnet.determineTransMsgNumber(msg);
			((AbstractTransMessage) msg).setCommId(assignedMsgId);
			NetMessage netMsg = new IPv4Message(msg, receiver, this.myID);
			log.debug("Dropping message " + msg + ", because sender " + this
					+ " is offline.");
			NetLayerLiveMonitoring.getOfflineMsgDrop().droppedMessage();
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.DROP);
		}
	}

	class SendContextImpl implements ISendContext {

		@Override
		public Object getTrafCtrlMetadata() {
			return trafCtrlMetadata;
		}

		@Override
		public void setTrafCtrlMetadata(Object trafCtrlMetadata) {
			ModularNetLayer.this.trafCtrlMetadata = trafCtrlMetadata;
		}

		@Override
		public void sendSubnet(NetMessage netMsg) {
			NetLayerLiveMonitoring.getTrafCtrlMsgDrop().noDropMessage();
			Simulator.getMonitor().netMsgEvent(netMsg, getMyID(), Reason.SEND);
			ModularNetLayer.this.getSubnet().send(netMsg);
		}

		@Override
		public void dropMessage(NetMessage netMsg) {
			log.debug("Dropping message " + netMsg + ", because the sender's ("
					+ ModularNetLayer.this
					+ ") traffic control mechanism has decided it.");
			NetLayerLiveMonitoring.getTrafCtrlMsgDrop().droppedMessage();
			Simulator.getMonitor().netMsgEvent(netMsg, getMyID(), Reason.DROP);
		}

		@Override
		public Bandwidth getMaxBW() {
			return ModularNetLayer.this.getMaxBandwidth();
		}

	}

	// TODO
	@Override
	@Deprecated
	public void receive(NetMessage message) {
		throw new IllegalStateException(
				"The method receive(NetMessage) is deprecated in the Modular Network Layer.");
	}

	public void receive(ModularNetMessage message,
			ModularNetLayer netLayerOfSender) {
		subnet.getStrategies()
				.getTrafficControlStrategy()
				.onReceive(
						this.new ReceiveContextImpl(
								netLayerOfSender.getMaxBandwidth()), message);
	}

	class ReceiveContextImpl implements IReceiveContext {

		private Bandwidth senderBW;

		public ReceiveContextImpl(Bandwidth senderBW) {
			this.senderBW = senderBW;
		}

		@Override
		public Object getTrafCtrlMetadata() {
			return trafCtrlMetadata;
		}

		@Override
		public void setTrafCtrlMetadata(Object trafCtrlMetadata) {
			ModularNetLayer.this.trafCtrlMetadata = trafCtrlMetadata;
		}

		@Override
		public void arrive(NetMessage message) {
			NetLayerLiveMonitoring.getTrafCtrlMsgDrop().noDropMessage();
			if (ModularNetLayer.this.isOnline()) {
				NetLayerLiveMonitoring.getOfflineMsgDrop().noDropMessage();
				NetID myID = getMyID();
				Simulator.getMonitor().netMsgEvent(message, myID,
						Reason.RECEIVE);
				NetMsgEvent event = new NetMsgEvent(message,
						ModularNetLayer.this);
				Collection<NetMessageListener> msgListeners = getMsgListeners();
				if (msgListeners == null || msgListeners.isEmpty()) {
					Simulator.getMonitor().netMsgEvent(message, myID,
							Reason.DROP);
					log.warn(this + "Cannot deliver message "
							+ message.getPayload() + " at netID=" + myID
							+ " as no message msgListeners registered");
				} else {
					for (NetMessageListener listener : msgListeners) {
						listener.messageArrived(event);
					}
				}
			} else {
				log.debug("Dropping message " + message + ", because receiver "
						+ this + " is offline.");
				NetLayerLiveMonitoring.getOfflineMsgDrop().droppedMessage();
				Simulator.getMonitor().netMsgEvent(message, getMyID(),
						Reason.DROP);
			}
		}

		@Override
		public void dropMessage(NetMessage netMsg) {
			NetLayerLiveMonitoring.getTrafCtrlMsgDrop().droppedMessage();
			log.debug("Dropping message " + netMsg
					+ ", because the receiver's (" + ModularNetLayer.this
					+ ") traffic control mechanism has decided it.");
			Simulator.getMonitor().netMsgEvent(netMsg, getMyID(), Reason.DROP);
		}

		@Override
		public Bandwidth getMaxBW() {
			return ModularNetLayer.this.getMaxBandwidth();
		}

		@Override
		public Bandwidth getBandwidthOfSender() {
			return senderBW;
		}

	}

	public NetMeasurementDB.Host getDBHostMeta() {
		return hostMeta;
	}

	public NetID getMyID() {
		return myID;
	}

	public Collection<NetMessageListener> getMsgListeners() {
		return msgListeners;
	}

	public AbstractModularSubnet getSubnet() {
		return subnet;
	}

	public HostDevice getDevice() {
		return device;
	}

	private long lastSchRcvTime = -1;

	public long getLastSchRcvTime() {
		return lastSchRcvTime;
	}

	public void setLastSchRcvTime(long lastSchRcvTime) {
		this.lastSchRcvTime = lastSchRcvTime;
	}

	@Override
	public void goOffline() {
		super.goOffline();
		getSubnet().netLayerWentOffline(this);
	}

	@Override
	public void goOnline() {
		super.goOnline();
		getSubnet().netLayerWentOnline(this);
	}


}
