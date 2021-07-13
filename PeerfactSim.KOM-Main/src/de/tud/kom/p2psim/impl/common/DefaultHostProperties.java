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



package de.tud.kom.p2psim.impl.common;

import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.HostProperties;
import de.tud.kom.p2psim.api.network.NetPosition;

/**
 * Default implementation of host properties.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
public class DefaultHostProperties implements HostProperties {

	private boolean connectivity;

	private DefaultHost host;

	private boolean churnAffected = true;

	private String groupID;

	private List<ConnectivityListener> conListeners;

	/**
	 * Create new and empty default host properties.
	 */
	public DefaultHostProperties() {
		this.conListeners = new LinkedList<ConnectivityListener>();
	}

	public double getCurrentDownloadBandwidth() {
		return host.getNetLayer().getCurrentDownloadBandwidth();
	}

	public double getCurrentUploadBandwidth() {
		return host.getNetLayer().getCurrentUploadBandwidth();
	}

	public NetPosition getNetPosition() {
		return host.getNetLayer().getNetPosition();
	}

	public double getMaxDownloadBandwidth() {
		return host.getNetLayer().getMaxDownloadBandwidth();
	}

	public double getMaxUploadBandwidth() {
		return host.getNetLayer().getMaxUploadBandwidth();
	}

	public void setHost(Host host) {
		this.host = (DefaultHost) host;
	}

	public DefaultHost getHost() {
		return host;
	}

	public void setEnableChurn(boolean churn) {
		this.churnAffected = churn;
	}

	public boolean isChurnAffected() {
		return this.churnAffected;
	}

	public void addConnectivityListener(ConnectivityListener listener) {
		conListeners.add(listener);
	}

	public void removeConnectivityListener(ConnectivityListener listener) {
		conListeners.remove(listener);
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public void connectivityChanged(ConnectivityEvent ce) {
		for (ConnectivityListener listener : conListeners) {
			listener.connectivityChanged(ce);
		}
	}
}
