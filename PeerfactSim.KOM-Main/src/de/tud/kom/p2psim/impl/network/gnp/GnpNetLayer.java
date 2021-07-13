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


package de.tud.kom.p2psim.impl.network.gnp;

import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Monitor.Reason;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.network.NetProtocol;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.network.AbstractNetLayer;
import de.tud.kom.p2psim.impl.network.IPv4Message;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.network.gnp.topology.GnpPosition;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;

/**
 * 
 * @author geraldklunker <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnpNetLayer extends AbstractNetLayer implements
		SimulationEventHandler {

	private GeoLocation geoLocation;

	private GnpSubnet subnet;

	private long nextFreeSendingTime = 0;

	private long nextFreeReceiveTime = 0;

	private Map<GnpNetLayer, GnpNetBandwidthAllocation> connections = new HashMap<GnpNetLayer, GnpNetBandwidthAllocation>();

	public GnpNetLayer(GnpSubnet subNet, IPv4NetID netID,
			GnpPosition netPosition, GeoLocation geoLoc, Bandwidth maxBW) {
		super(maxBW, netPosition);
		this.subnet = subNet;
		this.myID = netID;
		this.online = true;
		this.geoLocation = geoLoc;
		subNet.registerNetLayer(this);
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	/**
	 * 
	 * @return 2-digit country code
	 */
	public String getCountryCode() {
		return geoLocation.getCountryCode();
	}

	/**
	 * 
	 * @return first time sending is possible (line is free)
	 */
	public long getNextFreeSendingTime() {
		return nextFreeSendingTime;
	}

	/**
	 * 
	 * @param time
	 *            first time sending is possible (line is free)
	 */
	public void setNextFreeSendingTime(long time) {
		nextFreeSendingTime = time;
	}

	/**
	 * 
	 * @param netLayer
	 * @return
	 */
	public boolean isConnected(GnpNetLayer netLayer) {
		return connections.containsKey(netLayer);
	}

	/**
	 * 
	 * @param netLayer
	 * @param allocation
	 */
	public void addConnection(GnpNetLayer netLayer,
			GnpNetBandwidthAllocation allocation) {
		connections.put(netLayer, allocation);
	}

	/**
	 * 
	 * @param netLayer
	 * @return
	 */
	public GnpNetBandwidthAllocation getConnection(GnpNetLayer netLayer) {
		return connections.get(netLayer);
	}

	/**
	 * 
	 * @param netLayer
	 */
	public void removeConnection(GnpNetLayer netLayer) {
		connections.remove(netLayer);
	}

	/**
	 * 
	 * @param msg
	 */
	public void addToReceiveQueue(IPv4Message msg) {
		long receiveTime = subnet.getLatencyModel().getTransmissionDelay(
				msg.getSize(), getMaxBandwidth().getDownBW());
		long currenTime = Simulator.getCurrentTime();
		long arrivalTime = nextFreeReceiveTime + receiveTime;
		if (arrivalTime <= currenTime) {
			nextFreeReceiveTime = currenTime;
			receive(msg);
		} else {
			nextFreeReceiveTime = arrivalTime;
			Simulator.scheduleEvent(msg, arrivalTime, this,
					SimulationEvent.Type.MESSAGE_RECEIVED);
		}
	}

	@Override
	public boolean isSupported(TransProtocol transProtocol) {
		return (transProtocol.equals(TransProtocol.UDP) || transProtocol
				.equals(TransProtocol.TCP));
	}

	public void send(Message msg, NetID receiver, NetProtocol netProtocol) {
		// outer if-else-block is used to avoid sending although the host is
		// offline
		if (this.isOnline()) {
			TransProtocol usedTransProtocol = ((AbstractTransMessage) msg)
					.getProtocol();
			if (this.isSupported(usedTransProtocol)) {
				NetMessage netMsg = new IPv4Message(msg, receiver, this.myID);
				log.debug(Simulator.getSimulatedRealtime() + " Sending "
						+ netMsg);
				Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.SEND);
				this.subnet.send(netMsg);
			} else
				throw new IllegalArgumentException("Transport protocol "
						+ usedTransProtocol
						+ " not supported by this NetLayer implementation.");
		} else {
			int assignedMsgId = subnet.determineTransMsgNumber(msg);
			log.debug("During send: Assigning MsgId " + assignedMsgId
					+ " to dropped message");
			((AbstractTransMessage) msg).setCommId(assignedMsgId);
			NetMessage netMsg = new IPv4Message(msg, receiver, this.myID);
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.DROP);
		}

	}

	@Override
	public String toString() {
		return this.getNetID().toString() + " ( "
				+ this.getHost().getProperties().getGroupID() + " )";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.simengine.SimulationEventHandler#eventOccurred(
	 * de.tud.kom.p2psim.api.simengine.SimulationEvent)
	 */
	public void eventOccurred(SimulationEvent se) {
		if (se.getType() == SimulationEvent.Type.MESSAGE_RECEIVED)
			receive((NetMessage) se.getData());
		else if (se.getType() == SimulationEvent.Type.TEST_EVENT) {
			Object[] msgInfo = (Object[]) se.getData();
			send((Message) msgInfo[0], (NetID) msgInfo[1],
					(NetProtocol) msgInfo[2]);
		}

		else if (se.getType() == SimulationEvent.Type.SCENARIO_ACTION
				&& se.getData() == null) {
			goOffline();
		} else if (se.getType() == SimulationEvent.Type.SCENARIO_ACTION) {
			System.out.println("ERROR" + se.getData());
			cancelTransmission((Integer) se.getData());
		}
	}

	public void goOffline() {
		super.goOffline();
		subnet.goOffline(this);
	}

	public void cancelTransmission(int commId) {
		subnet.cancelTransmission(commId);
	}

	// for JUnit Test

	public void goOffline(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.SCENARIO_ACTION);
	}

	public void cancelTransmission(int commId, long time) {
		Simulator.scheduleEvent(new Integer(commId), time, this,
				SimulationEvent.Type.SCENARIO_ACTION);
	}

	public void send(Message msg, NetID receiver, NetProtocol netProtocol,
			long sendTime) {
		Object[] msgInfo = new Object[3];
		msgInfo[0] = msg;
		msgInfo[1] = receiver;
		msgInfo[2] = netProtocol;
		Simulator.scheduleEvent(msgInfo, sendTime, this,
				SimulationEvent.Type.TEST_EVENT);
	}

}
