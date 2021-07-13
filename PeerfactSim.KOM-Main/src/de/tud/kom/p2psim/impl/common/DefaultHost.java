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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.application.Application;
import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.HostProperties;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.storage.ContentStorage;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.user.User;
import de.tud.kom.p2psim.impl.storage.DefaultContentStorage;
import de.tud.kom.p2psim.impl.transport.DefaultTransLayer;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Default implementation of a host.
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DefaultHost implements Host {
	private static final Logger log = SimLogger.getLogger(DefaultHost.class);

	private NetLayer netLayer;

	private List<OverlayNode> overlays;

	private Application application;

	private HostProperties properties;

	private User user;

	private List<Component> components;

	private TransLayer transLayer;

	private ContentStorage storage;

	/**
	 * Create a new and empty host.
	 */
	public DefaultHost() {
		this.overlays = new LinkedList<OverlayNode>();
		this.components = new LinkedList<Component>();
	}

	public void setNetwork(NetLayer nw) {
		components.add(nw);
		this.netLayer = nw;
		this.netLayer.setHost(this);
		nw.addConnectivityListener(properties);
	}

	public void setApplication(Application appl) {
		components.add(appl);
		this.application = appl;
		this.application.setHost(this);
	}

	public void setOverlayNode(OverlayNode node) {
		components.add(node);
		overlays.add(node);
		node.setHost(this);
	}

	public void setTransport(TransLayer transLayer) {
		components.add(transLayer);
		this.transLayer = transLayer;
		this.transLayer.setHost(this);
	}

	public TransLayer getTransLayer() {
		if (transLayer == null) {
			log.warn("transport layer is unset. Create default one.");
			this.transLayer = new DefaultTransLayer(getNetLayer());
		}
		return transLayer;
	}

	public void setProperties(HostProperties properties) {
		this.properties = properties;
		this.properties.setHost(this);
	}

	public User getUser() {
		return user;
	}

	public NetLayer getNetLayer() {
		return netLayer;
	}

	public OverlayNode getOverlay(Class api) {
		for (OverlayNode overlay : overlays) {
			if (api.isInstance(overlay))
				return overlay;
		}
		return null;
	}

	public Iterator<OverlayNode> getOverlays() {
		return overlays.iterator();
	}

	public Application getApplication() {
		return application;
	}

	public HostProperties getProperties() {
		return properties;
	}

	public ContentStorage getStorage() {
		if (storage == null) {
			log.warn("Storage was not set. Create a default (and empty) one.");
			storage = new DefaultContentStorage();
		}
		return storage;
	}

	public void setContentStorage(ContentStorage storage) {
		components.add(storage);
		this.storage = storage;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Host {");
		sb.append("GroupID=");
		if (this.properties != null)
			sb.append(this.properties.getGroupID());
		sb.append(", nw=");
		if (this.netLayer != null)
			sb.append(netLayer.getNetID());
		sb.append(", #olays=");
		sb.append(overlays.size());
		sb.append(", appl=");
		sb.append(application);
		sb.append("}");

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> componentClass) {
		T found = null;
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				if (found != null)
					throw new IllegalStateException("Ambiguious request: both "
							+ found + " and " + component
							+ " are instances of " + componentClass);
				found = (T)component;
			}
		}
		assert componentClass.isInstance(found) : "required class="
				+ componentClass + " but found " + found;
		return found;
	}

	/**
	 * Currently only used for testing
	 * 
	 * @param component
	 */
	public void setComponent(Component component) {
		this.components.add(component);
	}

}
