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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types;

/**
 * Configuration settings for general Kademlia types. All methods should return
 * constant values.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface TypesConfig {

	/**
	 * @return the height of the hierarchy tree. 0 means "no hierarchy". Each
	 *         step of the hierarchy consumes {@link #getHierarchyTreeOrder()}
	 *         bits in the node identifier.
	 */
	public int getHierarchyDepth();

	/**
	 * @return the number of branching bits per hierarchy step. That is, each
	 *         node (cluster) in the hierarchy tree has {@code
	 *         2^getHierarchyTreeOrder()} children.
	 */
	public int getHierarchyTreeOrder();

	/**
	 * @return the binary length of the Kademlia overlay identifiers
	 *         (KademliaOverlayID).
	 */
	public int getIDLength();

	/**
	 * @return the assumed size of a data item (in bytes) that is transferred at
	 *         the overlay (application) layer.
	 */
	public int getDataSize();

}
