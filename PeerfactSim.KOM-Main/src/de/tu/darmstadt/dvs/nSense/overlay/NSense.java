/**
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

package de.tu.darmstadt.dvs.nSense.overlay;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.overlay.operations.NSenseSector;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Stores the node information about the other nodes for the local nodes. The
 * information will be store in the <code>nodeStorage</code>. The
 * <code>nearNodes, sensorNodes</code> and <code>localNode</code> contains the
 * keys for the nodeStorage.
 * 
 * @author Maribel Zamorano
 * @version 25/11/2013
 */
public class NSense {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(NSense.class);

	private final Hashtable<NSenseID, NSenseNodeInfo> nodeStorage;

	private final Vector<NSenseID> nearNodes;

	private final NSenseID[] sensorNodes;

	private final int dimension;

	private final NSenseID localNode;

	public NSense(NSenseID localNodeID) {
		this.nodeStorage = new Hashtable<NSenseID, NSenseNodeInfo>();
		this.nearNodes = new Vector<NSenseID>();
		this.sensorNodes = new NSenseID[Configuration.NUMBER_SECTORS];
		this.dimension = Configuration.DIMENSION;
		this.localNode = localNodeID;
	}

	/**
	 * Update the node storage with the given information. The given information
	 * will only store, if the information has a newer content.
	 * 
	 * @param id
	 *            The ID of the node.
	 * @param nodeInfo
	 *            The {@link NSenseNodeInfo} with the new information
	 * @return If the content of the nodeInfo is new then <code>true</code>,
	 *         otherwise <code>false</code>. A nodeInfo is new, if the sequence
	 *         number is greater than the old sequence number or the nodeInfo is
	 *         not in {@link #nodeStorage}.
	 * @throws IllegalArgumentException
	 *             If a parameter is null.
	 */
	public boolean updateNodeStorage(NSenseID id, NSenseNodeInfo nodeInfo) {
		if (id == null || nodeInfo == null) {
			throw new IllegalArgumentException("An argument is null");
		}
		NSenseNodeInfo oldNodeInfo = nodeStorage.get(id);

		if (oldNodeInfo != null) {
			if (oldNodeInfo.getSequenceNr().equals(nodeInfo.getSequenceNr())) {
				// it is possible that this nodeInfo has new receivers
				oldNodeInfo.updateReceiversList(nodeInfo.getReceiversList());
				return false;
			} else if (oldNodeInfo.getSequenceNr().isNewerAs(
					nodeInfo.getSequenceNr())) {
				// do nothing, because nodeInfo is old
				return false;
			}
		}
		// if nodeInfo newer or not in nodeStorage, replace nodeInfo!
		nodeStorage.put(id, nodeInfo);
		return true;
	}

	public void updateSensorNodeList(NSenseID[] newSensorNodes) {
		if (newSensorNodes != null && sensorNodes != null) {
			if (newSensorNodes.length == sensorNodes.length) {
				NSenseNodeInfo localNodeInfo = nodeStorage.get(localNode);
				List<NSenseID> toIgnore = new Vector<NSenseID>();
				toIgnore.add(localNode);
				// search for new sensor nodes
				// iterate over sensorNodes and Update
				for (int i = 0; i < sensorNodes.length; i++) {
					if (newSensorNodes[i] != null) {
						// replace old value
						sensorNodes[i] = newSensorNodes[i];
					} else {
						/** If no newer sensor node returned, then search a new **/
						// if (sensorNodes[i] != null) {
						// keep the old node
						// sensorNodes[i] = sensorNodes[i];
						// } else {
						// select the respective found sensor node for the
						// sector.
						/*
						 * System.out
						 * .println("Replacing sensor node with new FOUND sensor"
						 * ); // }
						 */
						NSenseID[] foundNewSensorNodes = findSensorNodes(
								localNodeInfo.getPosition(),
								localNodeInfo.getVisionRangeRadius(), toIgnore);

						sensorNodes[i] = foundNewSensorNodes[i];
					}
				}
			} else {
				log.error("The stored list for sensorNodes has a different length as the new delivered list");
			}
		}

	}

	/**
	 * Find a sensor node for the given sectorID. The node can a node in the
	 * sector or a node in the near of the sector.
	 * 
	 * @param position
	 *            The position of the node, that search a sensor node.
	 * @param visionRangeRadius
	 *            The vision range radius of the node
	 * @param sectorID
	 *            The sectorID, for that is to find a sensor node.
	 * @param ignoreNodes
	 *            Nodes that are to ignore. Normal the nodeID of the node, that
	 *            belongs the position and visionRangeRadius.
	 * @return The NSenseID, of the node that is the best sensor node for this
	 *         the given sector. If no node found, then returned
	 *         <code>null</code>.
	 */
	public NSenseID[] findSensorNodes(VectorN position, int visionRangeRadius,
			List<NSenseID> ignoreNodes) {

		if (log.isTraceEnabled()) {
			log.trace("First Method to find nearSensorNode");
		}
		NSenseSector sectorOp = new NSenseSector();
		sectorOp.setDimData(Configuration.DIMENSION);
		NSenseID[] newSensorNodes = new NSenseID[Configuration.NUMBER_SECTORS];
		Double[] sensorNodesDistance = new Double[Configuration.NUMBER_SECTORS];

		for (NSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				NSenseNodeInfo nodeInfo = nodeStorage.get(id);
				// transform nodeInfo.position to the origin on the basis of
				// posCenter
				// select sensor nodes also in higher dimensional spaces (for
				// evaluation)
				float[] dimensions = new float[nodeInfo.getPosition().getSize()];
				for (int i = 0; i < dimensions.length; i++) {
					dimensions[i] = nodeInfo.getPosition().getValue(i)
							- position.getValue(i);

				}
				double distance = position.distance(nodeInfo.getPosition());

				if (dimensions.length > 1) {
					try {
						// compute the sector on the basis of the n-sphere

						if (visionRangeRadius < distance) {
							sectorOp.collarSubdivision(0);
							int sector = sectorOp.getSector(new VectorN(
									dimensions), dimension,
									newSensorNodes.length);
							if ((newSensorNodes[sector - 1] == null)
									|| (distance < sensorNodesDistance[sector - 1])) {
								newSensorNodes[sector - 1] = id;
								sensorNodesDistance[sector - 1] = distance;
							}
							/**
							 * // compute the sector on the basis of the
							 * n-sphere int sector = new
							 * NSenseSector().getSector(new VectorN(
							 * dimensions), dimension, newSensorNodes.length);
							 * double distance = position.distance(nodeInfo
							 * .getPosition()); if ((newSensorNodes[sector - 1]
							 * == null) || (visionRangeRadius < distance &&
							 * distance < sensorNodesDistance[sector - 1])) {
							 * newSensorNodes[sector - 1] = id;
							 * sensorNodesDistance[sector - 1] = distance;
							 **/
						}
					} catch (IllegalArgumentException e) {
						// do nothing.
						if (log.isDebugEnabled()) {
							log.debug(
									"Node position is on the same position for this request. It is not bad, because it gives no angle between this nodes.",
									e);
						}
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Node position does not contains enough arguments");
					}
				}
			}
		}
		// add closest node as sensor node if no better sensor node is found for
		// a sector. To-do cambiar a closest node to position in vision range...
		/*
		 * NSenseID closestNode = getClosestSensor(newSensorNodes, position,
		 * -1); for (int i = 0; i < newSensorNodes.length; i++) { if
		 * (newSensorNodes[i] == null) { newSensorNodes[i] = closestNode; } }
		 */

		return newSensorNodes;

	}

	@SuppressWarnings("unused")
	private NSenseID getClosestSensor(NSenseID[] nodeIDs, VectorN position,
			int minDistance) {
		if (nodeIDs == null || nodeIDs.length == 0)
			return null;
		NSenseID minID = null;
		double min = Double.MAX_VALUE;
		for (NSenseID id : nodeIDs) {
			if (id != null) {
				NSenseNodeInfo nodeInfo = nodeStorage.get(id);
				double distance = position.distance(nodeInfo.getPosition());
				if (minDistance < distance && distance < min) {
					min = distance;
					minID = id;
				}
			}
		}
		return minID;
	}

	public void removeDeadNodes() {
		Enumeration<NSenseID> e = nodeStorage.keys();
		while (e.hasMoreElements()) {
			NSenseID id = e.nextElement();
			NSenseNodeInfo nodeInfo = nodeStorage.get(id);
			if (Simulator.getCurrentTime() - nodeInfo.getLastUpdate() > Configuration.DECLARE_NODE_DEATH_TIMEOUT) {
				if (id != localNode) {
					NSenseNodeInfo rmNodeInfo = nodeStorage.remove(id);

					// for consistent of the data structure
					for (int i = 0; i < sensorNodes.length; i++) {
						if (sensorNodes[i] != null && sensorNodes[i].equals(id))
							sensorNodes[i] = null;
					}
					nearNodes.remove(id);

					if (log.isDebugEnabled())
						log.debug("Node: "
								+ rmNodeInfo.getContact().getOverlayID()
								+ " is declared dead and deleted from the nodeStorage.");
				}
			}
		}
	}

	public void removeUnusedNodes() {
		Enumeration<NSenseID> e = nodeStorage.keys();
		while (e.hasMoreElements()) {
			NSenseID id = e.nextElement();
			if (localNode.equals(id) || nearNodes.contains(id)
					|| sensorNodesContains(id)) {
				// node is OK
			} else {
				nodeStorage.remove(id);
			}
		}
	}

	private boolean sensorNodesContains(NSenseID id) {
		for (NSenseID sensorNodeID : sensorNodes) {
			if (sensorNodeID != null && sensorNodeID.equals(id))
				return true;
		}
		return false;
	}

	/**
	 * Update the nodeInfo for the local node.
	 * 
	 * @param nodeInfo
	 *            The nodeInfo, which is to store for the local node.
	 */
	public void updateLocalNodePosition(NSenseNodeInfo nodeInfo) {
		nodeStorage.put(localNode, nodeInfo);
	}

	/**
	 * Update the near node list. It looks, which nodes are in the area of the
	 * local node.
	 */
	public void updateNearNodeList() {
		// remove all ids from nearNodes list
		nearNodes.clear();

		NSenseNodeInfo localNodeInfo = nodeStorage.get(localNode);
		List<NSenseID> toIgnore = new Vector<NSenseID>();
		toIgnore.add(localNode);

		List<NSenseID> newNearNodes = getAllNodesInArea(
				localNodeInfo.getPosition(),
				localNodeInfo.getVisionRangeRadius(), toIgnore);

		nearNodes.addAll(newNearNodes);

	}

	/**
	 * Determines all known nodes in the given area. The ignoreNodes will be
	 * excluded.<br>
	 * Attention, the localNode can be in this area too.
	 * 
	 * @param position
	 *            The center of the area.
	 * @param radius
	 *            The radius of the area.
	 * @param ignoreNodes
	 *            The nodes that are to ignore.
	 * @return A list of {@link NSenseID}s, which are contain in this given
	 *         area.
	 */
	public List<NSenseID> getAllNodesInArea(VectorN position, int radius,
			List<NSenseID> ignoreNodes) {
		List<NSenseID> nodesInArea = new Vector<NSenseID>();

		for (NSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				NSenseNodeInfo nodeInfo = nodeStorage.get(id);
				if (inVisionRange(position, nodeInfo.getPosition(), radius))
					nodesInArea.add(id);
			}
		}
		return nodesInArea;
	}

	public List<NSenseID> getAllNodesInDimensionalArea(VectorN position,
			int radius, List<NSenseID> ignoreNodes) {
		List<NSenseID> nodesInArea = new Vector<NSenseID>();
		for (NSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				NSenseNodeInfo nodeInfo = nodeStorage.get(id);

				if (inVisionRange(position, nodeInfo.getPositionDimensions(),
						radius))
					nodesInArea.add(id);
			}
		}
		return nodesInArea;
	}

	/**
	 * Check, whether point2 is in vision range of first point.
	 * 
	 * @param point
	 *            The first point.
	 * @param point2
	 *            The second point, which is to check, whether in the
	 *            visionRangeRadius of the first point.
	 * @param visionRangeRadius
	 *            The vision range radius of the first point.
	 * @return <code>true</code> if point2 is in the visionRangeRadius of point,
	 *         otherwise <code>false</code>
	 */
	private boolean inVisionRange(VectorN point, VectorN point2,
			int visionRangeRadius) {
		double length = point.distance(point2);
		return (length <= visionRangeRadius);
	}

	/**
	 * Gets the {@link NSenseNodeInfo} to the associated id.
	 * 
	 * @param id
	 *            The {@link NSenseID} for the asked node info
	 * @return The {@link NSenseNodeInfo} to the associated id. If no
	 *         information stored to the id, then return <code>null</code>.
	 */
	public NSenseNodeInfo getNodeInfo(NSenseID id) {
		return nodeStorage.get(id);
	}

	/**
	 * Gets a copy of the list of the nearNodes back.
	 * 
	 * @return A list of all near nodes.
	 */
	public List<NSenseID> getNearNodes() {
		return new Vector<NSenseID>(nearNodes);
	}

	/**
	 * Gets a copy of sensor nodes back. The sectorID is coded in the array
	 * position.
	 * 
	 * @return A list of all sensor nodes.
	 */
	public NSenseID[] getSensorNodes() {
		return sensorNodes.clone();
	}

	/**
	 * Gets the @link {@link NSenseID} of the local node.
	 * 
	 * @return the local node PSenseID
	 */
	public NSenseID getLocalNode() {
		return localNode;
	}

	/**
	 * Look for known nodes in the given area. The ignoreNode is the node, that
	 * belong to the given area.
	 * 
	 * @param position
	 *            The center of the area, which is to check.
	 * @param radius
	 *            The radius around the position, for define the area
	 * @param ignoreNode
	 *            The node that is to ignore. Normal, that is the associated
	 *            node to the position and visionRangeRadius.
	 * @return <code>true</code> if a node exits in this area, otherwise
	 *         <code>false</code>.
	 */
	public boolean existsNodeInArea(VectorN position, int radius,
			NSenseID ignoreNode) {
		List<NSenseID> ignoreNodes = new Vector<NSenseID>();
		ignoreNodes.add(ignoreNode);
		List<NSenseID> nodes = getAllNodesInArea(position, radius, ignoreNodes);

		if (nodes.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Check whether the given id a sensor node is.
	 * 
	 * @param id
	 *            The ID, that is to check.
	 * @return <code>true</code> if the id is a sensor node, otherwise
	 *         <code>false</code>
	 */
	public boolean isSensorNode(NSenseID id) {
		for (NSenseID sensorID : sensorNodes) {
			if (sensorID != null && sensorID.equals(id))
				return true;
		}
		return false;
	}

	private NSenseID getSmallestDistance(List<NSenseID> nodeIDs,
			VectorN position, int minDistance) {
		if (nodeIDs == null || nodeIDs.size() == 0)
			return null;
		NSenseID minID = null;
		double min = Double.MAX_VALUE;
		for (NSenseID id : nodeIDs) {
			NSenseNodeInfo nodeInfo = nodeStorage.get(id);
			double distance = position.distance(nodeInfo.getPosition());
			if (minDistance < distance && distance < min) {
				min = distance;
				minID = id;
			}
		}
		return minID;
	}

	/**
	 * Gets a node, that is closest to the position and is not a node from the
	 * ignoreNodes list.
	 * 
	 * @param position
	 *            The reference position for the closest node
	 * @param ignoreNodes
	 *            Nodes that are to ignore.
	 * @return The {@link NSenseID} of the closest node. If doesn't exist one
	 *         node, then return <code>null</code>
	 */
	public NSenseID getClosestNode(VectorN position, List<NSenseID> ignoreNodes) {
		List<NSenseID> nodes = new Vector<NSenseID>();
		// remove ignoreNodes from all known nodes.
		for (NSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id))
				nodes.add(id);
		}
		return getSmallestDistance(nodes, position, -1);
	}

	/**
	 * Gets all known {@link NSenseNodeInfo} back, that are stored in the
	 * {@link #nodeStorage}.
	 * 
	 * @return A list of all values from the {@link #nodeStorage}
	 */
	public List<NSenseNodeInfo> getAllKnownNodeInfos() {
		return new Vector<NSenseNodeInfo>(nodeStorage.values());
	}
}