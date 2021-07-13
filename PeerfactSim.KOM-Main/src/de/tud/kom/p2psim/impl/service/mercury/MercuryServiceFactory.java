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

package de.tud.kom.p2psim.impl.service.mercury;

import java.util.List;
import java.util.Vector;

import org.jfree.util.Log;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.common.DefaultHost;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryBootstrap;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryBootstrapChord;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryBootstrapKademlia;

/**
 * Mercury implemented as Service on top of DHT/KBR-Overlay. Please have a look
 * at the documentation for peerfactSim for all configuration parameters.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryServiceFactory implements ComponentFactory {

	
	private List<MercuryAttributePrimitive> attributes = new Vector<MercuryAttributePrimitive>();

	private short port = 123;

	private MercuryBootstrap bootstrap = null;

	private long timeBetweenMaintenance = 0;

	private long timeToCollectNotifications = 0;

	/**
	 * list of available Overlay Implementations
	 * 
	 * @author Bjoern Richerzhagen
	 * 
	 */
	private enum DHT_OVERLAY {
		CHORD, KADEMLIA
	}
	

	@Override
	public Component createComponent(Host host) {

		if (bootstrap != null) {
			bootstrap.setAttributes(attributes);
		} else {
			Log.error("No Overlay has been specified for Mercury! Please set an overlay using DHTOverlay-option.");
		}

		// Create the service
		MercuryService service = new MercuryService(host, port, bootstrap,
				timeBetweenMaintenance, timeToCollectNotifications);
		service.setAvailableAttributes(attributes);
		
		// Register Service as a Component - Correct way of implementing a service?
		// Needed to allow for actions in .dat-File
		DefaultHost defaultHost = (DefaultHost) host;
		defaultHost.setComponent(service);
		
		return service;
	}

	/**
	 * Register an Attribute (from XML-Config), each Attribute results in a new
	 * DHT-Overlay
	 * 
	 * @param attr
	 */
	public void setAttribute(MercuryAttributePrimitive attr) {
		attributes.add(attr);
	}
	
	
	public void setPort(short port) {
		this.port = port;
	}
	
	/**
	 * Interval between Maintenance (deleting old Subscriptions)
	 * 
	 * @param timeBetweenMaintenance
	 */
	public void setTimeBetweenMaintenance(long timeBetweenMaintenance) {
		this.timeBetweenMaintenance = timeBetweenMaintenance;
	}

	/**
	 * It is possible to collect Notifications for one receiver for up to
	 * <code>timeToCollectNotifications</code> seconds in order to save bandwith
	 * 
	 * @param timeToCollectNotifications
	 */
	public void setTimeToCollectNotifications(long timeToCollectNotifications) {
		this.timeToCollectNotifications = timeToCollectNotifications;
	}

	/**
	 * Specify the overlay that should be used by Mercury. Bootstrappers for
	 * each overlay can be found in the subpackage <code>dht</code>
	 * 
	 * @param overlay
	 */
	public void setDHTOverlay(String overlay) {
		switch (DHT_OVERLAY.valueOf(overlay.toUpperCase())) {
		case CHORD:
			bootstrap = new MercuryBootstrapChord();
			break;
		case KADEMLIA:
			bootstrap = new MercuryBootstrapKademlia();
			break;

		default:
			Log.error("The spcified Overlay " + overlay
					+ " is not supported by Mercury!");
			break;
		}

	}

	
}
