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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.TypesConfig;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Configuration settings for Kademlia operations. All methods should return
 * constant values.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface OperationsConfig extends TypesConfig {

	/**
	 * @return the maximum number of simulation time units that a lookup
	 *         operation may take.
	 */
	public long getLookupOperationTimeout();

	/**
	 * @return the time in simulation time units after which a message without
	 *         reply times out. That is, the receiver of the message is
	 *         considered unresponsive.
	 */
	public long getLookupMessageTimeout();

	/**
	 * @return the size of a bucket. In the Kademlia paper, this parameter is
	 *         called {@code k}.
	 */
	public int getBucketSize();

	/**
	 * @return the maximum number of simultaneous, concurrent lookups during a
	 *         node lookup.
	 */
	public int getMaxConcurrentLookups();

	/**
	 * @return the order of the routing tree. (Each node can have up to {@code
	 *         2^getRoutingTreeOrder()} children.) In the Kademlia paper, this
	 *         parameter is called {@code b}.
	 */
	public int getRoutingTreeOrder();

	/**
	 * @return the interval in which the routing table buckets are refreshed.
	 *         Simulation time units are used.
	 * @see Simulator for time units.
	 */
	public long getRefreshInterval();

}
