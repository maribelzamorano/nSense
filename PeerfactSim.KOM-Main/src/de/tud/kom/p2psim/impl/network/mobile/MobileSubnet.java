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



package de.tud.kom.p2psim.impl.network.mobile;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetLatencyModel;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.network.AbstractSubnet;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The default implementation of the SubNet interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MobileSubnet extends AbstractSubnet implements SimulationEventHandler {
	private static Logger log = SimLogger.getLogger(MobileSubnet.class);

	Map<NetID, NetLayer> netLayers;

	NetLatencyModel netLatencyModel;

	public static final double SUBNET_WIDTH = 2d;

	public static final double SUBNET_HEIGHT = 2d;

	public static final long inOrderOffset = 1;

	Map<MobileSubnet.LinkID, Long> links = new HashMap<MobileSubnet.LinkID, Long>();

	public MobileSubnet() {
		netLayers = new HashMap<NetID, NetLayer>();
	}

	@Override
	public void registerNetLayer(NetLayer net) {
		netLayers.put(net.getNetID(), net);
		log.debug("Register " + net.getNetID() + " " + net);
	}

	@Override
	public void send(NetMessage msg) {
		NetLayer sender = netLayers.get(msg.getSender());
		NetLayer receiver = netLayers.get(msg.getReceiver());
		long latency = netLatencyModel.getLatency(sender, receiver);
		log.debug("Send from " + sender + " to " + receiver + " with delay " + latency);
		scheduleReceiveEvent(msg, receiver, latency);
	}

	void scheduleReceiveEvent(NetMessage msg, NetLayer receiver, long latency) {
		LinkID link = new LinkID(msg.getSender(), msg.getReceiver());
		long lastArrivalTime = getLastArrivalTime(link);

		long newArrivalTime = Simulator.getCurrentTime() + latency;
		log.debug("new arrival time = " + newArrivalTime + " lastArrivalTime=" + lastArrivalTime);
		if (lastArrivalTime > newArrivalTime) { // assure ordered delivery
			newArrivalTime = lastArrivalTime + inOrderOffset;
			log.debug("arrival time adjusted to " + newArrivalTime);
		}
		links.put(link, newArrivalTime);

		Simulator.scheduleEvent(msg, newArrivalTime, this, SimulationEvent.Type.MESSAGE_RECEIVED);
	}

	long getLastArrivalTime(LinkID link) {
		long lastArrivalTime = (links.containsKey(link)) ? links.get(link) : -1;
		return lastArrivalTime;
	}

	NetLayer getNetLayer(NetID netId) {
		return this.netLayers.get(netId);
	}
	
	static class LinkID {
		private NetID srcId;

		private NetID dstId;

		public LinkID(NetID srcId, NetID dstId) {
			super();
			this.srcId = srcId;
			this.dstId = dstId;
		}

		@Override
		public boolean equals(Object obj) {
			if (!LinkID.class.isInstance(obj))
				return false;
			LinkID id2 = (LinkID) obj;
			return srcId.equals(id2.srcId) && dstId.equals(id2.dstId);
		}

		@Override
		public int hashCode() {
			int hCode = 17;
			hCode += (37 * srcId.hashCode());
			hCode += (37 * dstId.hashCode());
			return hCode;
		}

	}

	public void clear() {
		netLayers.clear();
		links.clear();
	}

	public void setLatencyModel(NetLatencyModel model) {
		this.netLatencyModel = model;
	}

	/**
	 * Dispatch the arriving message to its destination.
	 * 
	 * @param se
	 *            event containing the network message
	 */
	public void eventOccurred(SimulationEvent se) {
		MobileNetMessage msg = (MobileNetMessage) se.getData();
		NetID senderID = msg.getSender();
		NetID receiverID = msg.getReceiver();
		NetLayer receiver = netLayers.get(receiverID);
		LinkID linkID = new LinkID(senderID, receiverID);
		long lastArrivalTime = getLastArrivalTime(linkID);
		if (lastArrivalTime == se.getSimulationTime()) {
			links.remove(linkID);
			log.debug("Remove obsolete link " + linkID);
			assert !links.containsKey(linkID);
		}
		((MobileNetLayer) receiver).receive(msg);
	}

}
