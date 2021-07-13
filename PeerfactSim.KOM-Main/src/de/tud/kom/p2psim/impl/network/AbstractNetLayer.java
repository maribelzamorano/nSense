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


package de.tud.kom.p2psim.impl.network;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Monitor.Reason;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.network.NetMessageListener;
import de.tud.kom.p2psim.api.network.NetMsgEvent;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This abstract class provides a skeletal implementation of the
 * <code>NetLayer<code> interface to lighten the effort for implementing this interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public abstract class AbstractNetLayer implements NetLayer {

	protected static Logger log = SimLogger.getLogger(AbstractNetLayer.class);

	protected List<NetMessageListener> msgListeners;

	protected List<ConnectivityListener> connListeners;

	protected NetID myID;

	protected boolean online;

	private NetPosition position;

	Bandwidth currentBandwidth;

	Bandwidth maxBandwidth;

	private Host host;

	/**
	 * Abstract constructor called by a subclass of this instance
	 * 
	 * @param maxDownBandwidth
	 *            the maximum physical download bandwidth
	 * @param maxUpBandwidth
	 *            the maximum physical upload bandwidth
	 * @param position
	 *            the NetPosition of the network layer
	 */
	public AbstractNetLayer(Bandwidth maxBandwidth, NetPosition position) {
		this.msgListeners = new LinkedList<NetMessageListener>();
		this.connListeners = new LinkedList<ConnectivityListener>();
		if (maxBandwidth.getDownBW() < maxBandwidth.getUpBW())
			log.warn("maxDownBandwidth < maxUpBandwidth on host with NetID "
					+ this.myID);
		this.maxBandwidth = maxBandwidth;
		this.currentBandwidth = maxBandwidth.clone();
		this.position = position;
	}

	/**
	 * This message is called by the subnet to deliver a new NetMessage to a
	 * remote NetLayer. (@see de.tud.kom.p2psim.impl.network.AbstractSubnet).
	 * Calling this method informs further all registered NetMsgListeners about
	 * the receipt of this NetMessage using a appropriate NetMsgEvent.
	 * 
	 * @param message
	 *            The NetMessage that was received by the NetLayer.
	 */
	public void receive(NetMessage message) {
		if (this.isOnline()) {

			log.info(Simulator.getSimulatedRealtime() + " Receiving " + message);

			Simulator.getMonitor().netMsgEvent(message, myID, Reason.RECEIVE);
			NetMsgEvent event = new NetMsgEvent(message, this);
			if (msgListeners == null || msgListeners.isEmpty()) {
				Simulator.getMonitor().netMsgEvent(message, myID, Reason.DROP);
				log.warn(this + "Cannot deliver message "
						+ message.getPayload() + " at netID=" + myID
						+ " as no message msgListeners registered");
			} else {
				for (NetMessageListener listener : msgListeners) {
					listener.messageArrived(event);
				}
			}
		} else
			Simulator.getMonitor().netMsgEvent(message, myID, Reason.DROP);
	}

	/**
	 * Return whether the required transport protocol is supported by the given
	 * NetLayer instance
	 * 
	 * @param protocol
	 *            the required transport protocol
	 * @return true if supported
	 */
	protected abstract boolean isSupported(TransProtocol protocol);

	/**
	 * As the download bandwidth of a host might be shared between concurrently
	 * established connections, this method will be used by the subnet in order
	 * to adapt the current available download bandwidth.
	 * 
	 * @param currentDownBandwidth
	 *            the new available download bandwidth
	 */
	@Deprecated
	public void setCurrentDownBandwidth(double currentDownBandwidth) {
		this.currentBandwidth.setDownBW(currentDownBandwidth);
	}

	/**
	 * As the upload bandwidth of a host might be shared between concurrently
	 * established connections, this method will be used by the subnet in order
	 * to adapt the current available upload bandwidth.
	 * 
	 * @param currentUpBandwidth
	 *            the new available upload bandwidth
	 */
	@Deprecated
	public void setCurrentUpBandwidth(double currentUpBandwidth) {
		this.currentBandwidth.setUpBW(currentUpBandwidth);
	}

	public void setCurrentBandwidth(Bandwidth currentBandwidth) {
		this.currentBandwidth = currentBandwidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.tud.kom.p2psim.api.api.network.NetLayer#addNetMsgListener(
	 * NetMessageListener) listener)
	 */
	public void addNetMsgListener(NetMessageListener listener) {
		log.debug("Register msg listener " + listener);
		this.msgListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.tud.kom.p2psim.api.api.network.NetLayer#removeNetMsgListener(
	 * NetMessageListener) listener)
	 */
	public void removeNetMsgListener(NetMessageListener listener) {
		this.msgListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#getNetID()
	 */
	public NetID getNetID() {
		return this.myID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#goOffline()
	 */
	public void goOffline() {
		this.online = false;
		connectivityChanged(new ConnectivityEvent(this, this.online));
		Simulator.getMonitor().churnEvent(this.getHost(), Reason.OFFLINE);
		log.info(myID + " disconnected @ " + Simulator.getSimulatedRealtime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#goOnline()
	 */
	public void goOnline() {
		this.online = true;
		connectivityChanged(new ConnectivityEvent(this, this.online));
		Simulator.getMonitor().churnEvent(this.getHost(), Reason.ONLINE);
		log.info(myID + " connected @ " + Simulator.getSimulatedRealtime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#isOffline()
	 */
	public boolean isOffline() {
		return !online;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#isOnline()
	 */
	public boolean isOnline() {
		return online;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#setHost(Host host)
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.common.Component#getHost()
	 */
	public Host getHost() {
		return this.host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#getNetPosition()
	 */
	public NetPosition getNetPosition() {
		return this.position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#getMaxDownloadBandwidth()
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public double getMaxDownloadBandwidth() {
		return this.maxBandwidth.getDownBW();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.api.network.NetLayer#getMaxUploadBandwidth()
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public double getMaxUploadBandwidth() {
		return this.maxBandwidth.getUpBW();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.api.network.NetLayer#getCurrentDownloadBandwidth()
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public double getCurrentDownloadBandwidth() {
		return this.currentBandwidth.getDownBW();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.api.network.NetLayer#getCurrentUploadBandwidth()
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public double getCurrentUploadBandwidth() {
		return this.currentBandwidth.getUpBW();
	}

	public Bandwidth getCurrentBandwidth() {
		return currentBandwidth;
	}

	public Bandwidth getMaxBandwidth() {
		return maxBandwidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.network.NetLayer#addConnectivityListener(de.tud
	 * .kom.p2psim.api.common.ConnectivityListener)
	 */
	public void addConnectivityListener(ConnectivityListener listener) {
		this.connListeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.network.NetLayer#removeConnectivityListener(de.
	 * tud.kom.p2psim.api.common.ConnectivityListener)
	 */
	public void removeConnectivityListener(ConnectivityListener listener) {
		this.connListeners.remove(listener);
	}

	void connectivityChanged(ConnectivityEvent e) {
		for (ConnectivityListener l : connListeners)
			l.connectivityChanged(e);
	}

}
