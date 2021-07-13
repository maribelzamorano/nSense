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

package de.tud.kom.p2psim.impl.service.dht.replication;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.dht.DHTListenerSupported;
import de.tud.kom.p2psim.impl.service.dht.AbstractDHTServiceFactory;

/**
 * Create a new ServiceInstance for a ReplicatingDHT. All Config-Parameters are
 * prepopulated with default values.
 * 
 * Usage: add the Service to your Host, right after the specification of the
 * overlayNode-Factory. It will register itself as a DHTListener at the node.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTServiceFactory extends AbstractDHTServiceFactory {

	static final Logger log = Logger
			.getLogger(ReplicationDHTServiceFactory.class);

	ReplicationDHTConfig config = new ReplicationDHTConfig();

	@Override
	public Component createComponent(Host host) {

		short port = 609;
		DHTListenerSupported node = host
				.getComponent(DHTListenerSupported.class);
		if (node == null) {
			log.error("DHTService could not be started: there is no DHTListenerSupported Node!");
			return null;
		}
		ReplicationDHTService service = new ReplicationDHTService(host, port,
				node, config);
		node.registerDHTListener(service);

		return service;

	}

	/**
	 * Number of replicates to create
	 * 
	 * @param number
	 */
	public void setNumberOfReplicates(int number) {
		config.setNumberOfReplicates(number);
	}

	/**
	 * Minimum Number of replicates. if therer are less replictes, the service
	 * will republish. Optional.
	 * 
	 * @param number
	 */
	public void setMinimumNumberOfReplicates(int number) {
		config.setMinimumNumberOfReplicates(number);
	}

	/**
	 * Each Ping will be tried <code>number</code> times before the contact is
	 * declared offline
	 * 
	 * @param number
	 */
	public void setNumberOfPingTries(int number) {
		config.setNumberOfPingTries(number);
	}

	/**
	 * Time between PING-Operations
	 * 
	 * @param time
	 */
	public void setTimeBetweenRootPings(long time) {
		config.setTimeBetweenRootPings(time);
	}

	/**
	 * Time between checks of all stored files and possible republications if
	 * number of replicates is lower than MinimumNumberOfReplicates
	 * 
	 * @param time
	 */
	public void setTimeBetweenReplicationChecks(long time) {
		config.setTimeBetweenReplicationChecks(time);
	}

}
