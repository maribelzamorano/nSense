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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Monitor.Reason;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.network.NetProtocol;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.network.AbstractNetLayer;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MobileNetLayer extends AbstractNetLayer {

	protected static Logger log = SimLogger.getLogger(MobileNetLayer.class);

	private double currentDownBandwidth;

	private double currentUpBandwidth;

	private final MobileMovementManager mv;
	
	private final MobileSubnet subNet;

	public MobileNetLayer(MobileSubnet subNet, MobileNetID netID, MobileMovementManager mv, NetPosition netPosition, Bandwidth bw) {
		super(bw, netPosition);
		this.mv = mv;
		this.subNet = subNet;
		this.myID = netID;
		this.online = true;
		subNet.registerNetLayer(this);
	}
	
	public boolean isSupported(TransProtocol transProtocol) {
		return TransProtocol.UDP.equals(transProtocol);
	}

	public void send(Message msg, NetID receiver, NetProtocol netProtocol) {
		TransProtocol usedTransProtocol = ((AbstractTransMessage) msg).getProtocol();
		if (this.isSupported(usedTransProtocol)) {
			NetMessage netMsg = new MobileNetMessage(msg, receiver, myID, netProtocol);
			Simulator.getMonitor().netMsgEvent(netMsg, myID, Reason.SEND);
			subNet.send(netMsg);
		} else
			throw new IllegalArgumentException("Transport protocol " + usedTransProtocol + " not supported by this NetLayer implementation.");

	}

	@Override
	public String toString() {
		return "NetLayer(netID=" + myID + ", " + (online ? "online" : "offline") + ")";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentDownBandwidth);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentUpBandwidth);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		result = PRIME * result + ((subNet == null) ? 0 : subNet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MobileNetLayer other = (MobileNetLayer) obj;
		if (Double.doubleToLongBits(currentDownBandwidth) != Double.doubleToLongBits(other.currentDownBandwidth))
			return false;
		if (Double.doubleToLongBits(currentUpBandwidth) != Double.doubleToLongBits(other.currentUpBandwidth))
			return false;
		if (subNet == null) {
			if (other.subNet != null)
				return false;
		} else if (!subNet.equals(other.subNet))
			return false;
		return true;
	}

	public void cancelTransmission(int commId) {
		throw new UnsupportedOperationException();	
	}
	
	public MobileMovementManager getMv() {
		return mv;
	}

}
