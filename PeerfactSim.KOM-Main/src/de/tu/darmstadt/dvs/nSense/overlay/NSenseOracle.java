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

package de.tu.darmstadt.dvs.nSense.overlay;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import de.tu.darmstadt.dvs.nSense.application.IDONodeInfoNDim;
import de.tu.darmstadt.dvs.nSense.application.IDOOracleNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * An Oracle for nSense. It computes the ideal set of to knowing peers for a
 * node. Therefore is used global knowledge, which must insert before using the
 * oracle.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 * 
 */
public class NSenseOracle implements IDOOracleNDim {

	private NSense globalNSense;

	public NSenseOracle() {
		globalNSense = new NSense(NSenseID.EMPTY_NSENSE_ID);
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfoNDim> nodeInfos) {
		for (IDONodeInfoNDim nodeInfo : nodeInfos) {
			NSenseNodeInfo temp = createNSenseNodeInfo(nodeInfo);
			globalNSense.updateNodeStorage((NSenseID) nodeInfo.getID(), temp);
		}
	}

	/**
	 * Create from the {@link IDONodeInfo} a {@link NSenseNodeInfo}.
	 * 
	 * @param nodeInfo
	 *            A nodeInfo.
	 * @return A nodeInfo of type NSenseNodeInfo
	 */
	private NSenseNodeInfo createNSenseNodeInfo(IDONodeInfoNDim nodeInfo) {
		int aoi = nodeInfo.getAoiRadius();
		VectorN position = nodeInfo.getPosition();
		VectorN positionDimensions = nodeInfo.getPositionDimensions();

		NSenseID id = (NSenseID) nodeInfo.getID();
		NSenseContact contact = new NSenseContact(id, null);
		SequenceNumber seqNr = new SequenceNumber();
		List<NSenseID> receiverList = new Vector<NSenseID>();
		byte hops = 5;
		return new NSenseNodeInfo(aoi, position, positionDimensions, contact, seqNr, receiverList,
				hops);
	}

	@Override
	public void reset() {
		globalNSense = new NSense(NSenseID.EMPTY_NSENSE_ID);
	}

	@Override
	public List<IDONodeInfoNDim> getAllNeighbors(OverlayID id, int aoi) {
		IDONodeInfoNDim nodeInfoCenterNode = globalNSense
				.getNodeInfo((NSenseID) id);
		List<NSenseID> ignoreNodes = new Vector<NSenseID>();
		ignoreNodes.add((NSenseID) nodeInfoCenterNode.getID());

		VectorN position = nodeInfoCenterNode.getPosition();

		return getAllNeighbors(position, aoi, ignoreNodes);
	}

	/**
	 * Determine all Neighbors to one position and the given AOI for the
	 * globalNSense. The ignoreNodes contains the node for this is calculated.
	 * 
	 * @param position
	 *            The position of the node
	 * @param aoi
	 *            the AOI of a node.
	 * @param ignoreNodes
	 *            The node, which is on this position and should not be used as
	 *            result.
	 * @return A list of Neighbors, that are found in globalNSense for the AOI.
	 */
	private List<IDONodeInfoNDim> getAllNeighbors(VectorN position, int aoi,
			List<NSenseID> ignoreNodes) {
		// The Set is used, for an easy handling of no duplicate entries
		Set<NSenseID> idSet = new HashSet<NSenseID>();

		// adds all nodes in AOI to the idSet
		List<NSenseID> inAOI = globalNSense.getAllNodesInArea(position, aoi,
				ignoreNodes);
		idSet.addAll(inAOI);

//		NSenseID[] newSensorNodes = globalNSense.findSensorNodes(position, aoi,
//				ignoreNodes);
//		// adds all sensor nodes to the idSet
//		for (int sectorId = 0; sectorId < Configuration.NUMBER_SECTORS; sectorId++) {
//			if (newSensorNodes[sectorId] != null)
//				idSet.add(newSensorNodes[sectorId]);
//		}

		// to all ids in idSets, build a list with IDONodeInfo.
		Iterator<NSenseID> iter = idSet.iterator();
		List<IDONodeInfoNDim> result = new Vector<IDONodeInfoNDim>();
		while (iter.hasNext()) {
			NSenseID id = iter.next();
			if (id != null)
				result.add(globalNSense.getNodeInfo(id));
		}

		return result;
	}
	

	public List<IDONodeInfoNDim> getAllShouldKnowNeighbors(VectorN position, int aoi,
			List<NSenseID> ignoreNodes){
		// The Set is used, for an easy handling of no duplicate entries
		Set<NSenseID> idSet = new HashSet<NSenseID>();

		// adds all nodes in AOI to the idSet
		List<NSenseID> inAOI = globalNSense.getAllNodesInDimensionalArea(position, aoi,
				ignoreNodes);
		idSet.addAll(inAOI);
//
//		NSenseID[] newSensorNodes = globalNSense.findSensorNodes(position, aoi,
//				ignoreNodes);
//		// adds all sensor nodes to the idSet
//		for (int sectorId = 0; sectorId < Configuration.NUMBER_SECTORS; sectorId++) {
//			if (newSensorNodes[sectorId] != null)
//				idSet.add(newSensorNodes[sectorId]);
//		}

		// to all ids in idSets, build a list with IDONodeInfo.
		Iterator<NSenseID> iter = idSet.iterator();
		List<IDONodeInfoNDim> result = new Vector<IDONodeInfoNDim>();
		while (iter.hasNext()) {
			NSenseID id = iter.next();
			if (id != null)
				result.add(globalNSense.getNodeInfo(id));
		}

		return result;
	}

	@Override
	public List<IDONodeInfoNDim> getAllShouldKnowNeighbors(OverlayID id, int aoi) {
		IDONodeInfoNDim nodeInfoCenterNode = globalNSense
				.getNodeInfo((NSenseID) id);
		List<NSenseID> ignoreNodes = new Vector<NSenseID>();
		ignoreNodes.add((NSenseID) nodeInfoCenterNode.getID());

		VectorN position = nodeInfoCenterNode.getPositionDimensions();

		return getAllShouldKnowNeighbors(position, aoi, ignoreNodes);
	}
}
