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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.TypesConfig;

/**
 * Configuration settings for the routing table. All methods should return
 * constant values.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface RoutingTableConfig extends TypesConfig {

	/**
	 * @return the binary length of the Kademlia overlay identifiers
	 *         (KademliaOverlayID).
	 */
	public int getIDLength();

	/**
	 * @return the order of the routing tree. (Each node can have up to {@code
	 *         2^getRoutingTreeOrder()} children.) In the Kademlia paper, this
	 *         parameter is called {@code b}.
	 */
	public int getRoutingTreeOrder();

	/**
	 * @return the size of a bucket. In the Kademlia paper, this parameter is
	 *         called {@code k}.
	 */
	public int getBucketSize();

	/**
	 * @return the size of the replacement cache.
	 */
	public int getReplacementCacheSize();

	/**
	 * @return the number of marks that allow the routing table to remove a
	 *         routing table entry of a contact that has not responded to
	 *         messages. That is, if the contact has failed to respond to
	 *         queries {@code getStaleCounter()} times, it may be dropped.
	 */
	public int getStaleCounter();

	/**
	 * @return (an approximate of) the total number of peers that will be part
	 *         of the overlay network.
	 */
	public int getNumberOfPeers();

}
