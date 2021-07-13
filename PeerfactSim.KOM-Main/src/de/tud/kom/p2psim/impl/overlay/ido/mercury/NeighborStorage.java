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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This is the storage of nodeInfos for the MercuryIDO. It stores the
 * information of the position of nodes, which are matched with the
 * subscription.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 * 
 */
public class NeighborStorage {
	/**
	 * The id of the node, which creates this storage
	 */
	private OverlayID ownID = null;

	/**
	 * The container, which stores a {@link MercuryNodeInfo} to a overlayID.
	 */
	private Map<OverlayID, MercuryNodeInfo> storage;

	/**
	 * Creates a container to store the information and set the given parameter.
	 * 
	 * @param ownID
	 *            The ID of the node, which creates this storage.
	 */
	public NeighborStorage(OverlayID ownID) {
		this.ownID = ownID;
		this.storage = new HashMap<OverlayID, MercuryNodeInfo>();
	}

	/**
	 * Add a nodeInfo to the storage.
	 * 
	 * @param nodeInfo
	 *            The node Info, which should add to the storage.
	 */
	public void addNodeInfo(MercuryNodeInfo nodeInfo) {
		storage.put(nodeInfo.getID(), nodeInfo);
	}

	/**
	 * Remove a node Info from the storage.
	 * 
	 * @param id
	 *            The id of the node, which is associated to the node info.
	 */
	public void removeNodeInfo(OverlayID id) {
		storage.remove(id);
	}

	/**
	 * Get a stored node info.
	 * 
	 * @param id
	 *            The overlay id.
	 * @return The associated node info to the given id.
	 */
	public MercuryNodeInfo getNodeInfo(OverlayID id) {
		return storage.get(id);
	}

	/**
	 * Gets all neighbors. Thats are all nodes, which are stored in this
	 * storage.
	 * 
	 * @return A list of {@link MercuryNodeInfo}.
	 */
	public List<MercuryNodeInfo> getAllNeighbors() {
		List<MercuryNodeInfo> neighbors = new Vector<MercuryNodeInfo>();
		for (OverlayID id : storage.keySet()) {
			MercuryNodeInfo nodeInfo = storage.get(id);
			if (!id.equals(ownID)) {
				neighbors.add(nodeInfo);
			}
		}
		return neighbors;
	}

	/**
	 * Remove node infos, which are expired. The expire date is arrived with the
	 * actually time and the time of the creation of the nodeInfo. If the
	 * distance between this greater then <code>timeToValid</code>, then will be
	 * removed the nodeInfo.
	 * 
	 * @param timeToValid
	 *            The time for that a nodeInfo is valid.
	 */
	public void removeExpiredNodeInfos(long timeToValid) {
		List<OverlayID> remove = new Vector<OverlayID>();
		for (OverlayID id : storage.keySet()) {
			MercuryNodeInfo nodeInfo = storage.get(id);
			if (nodeInfo != null
					&& Simulator.getCurrentTime()
							- nodeInfo.getLastUpdateTime() > timeToValid) {
				remove.add(id);
			}
		}
		for (OverlayID id : remove) {
			removeNodeInfo(id);
		}
	}

	/**
	 * Remove node infos, which are not in the AOI. The AOI is defined over the
	 * center Position and the aoi.
	 * 
	 * @param center
	 *            The Position of the node, who is the owner of the neighbor
	 *            storage.
	 * @param aoi
	 *            Describes a scalar, which describes normally a radius. In this
	 *            case describes this a cuboid with the length of 2*aoi.
	 */
	public void removeNotInAOINodeInfos(Point center, int aoi) {
		int lowerBoundX = center.x - aoi;
		int upperBoundX = center.x + aoi;
		int lowerBoundY = center.y - aoi;
		int upperBoundY = center.y + aoi;
		List<OverlayID> remove = new Vector<OverlayID>();
		for (OverlayID id : storage.keySet()) {
			MercuryNodeInfo nodeInfo = storage.get(id);
			if (nodeInfo != null && nodeInfo.getPosition() != null) {
				if (!isInCuboid(lowerBoundX, upperBoundX, lowerBoundY,
						upperBoundY, nodeInfo.getPosition())) {
					remove.add(id);
				}
			}
		}
		for (OverlayID id : remove) {
			removeNodeInfo(id);
		}
	}

	/**
	 * Check the given test point, it is in the cuboid. For this need the method
	 * the lower and upper bound of X and Y.
	 * 
	 * @param lowerBoundX
	 *            lower bound of X
	 * @param upperBoundX
	 *            upper bound of X
	 * @param lowerBoundY
	 *            lower bound of Y
	 * @param upperBoundY
	 *            upper bound of Y
	 * @param testPoint
	 *            The point, for that is to test, whether in the cuboid.
	 * @return <code>true</code> if the point in the defined cuboid, otherwise
	 *         <code>false</code>.
	 */
	private boolean isInCuboid(int lowerBoundX, int upperBoundX,
			int lowerBoundY, int upperBoundY, Point testPoint) {
		if (lowerBoundX <= testPoint.x && testPoint.x <= upperBoundX) {
			if (lowerBoundY <= testPoint.y && testPoint.y <= upperBoundY) {
				return true;
			}
		}
		return false;
	}
}
