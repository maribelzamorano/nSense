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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class contains the list of all active nodes. When a node join the
 * network, it chooses one of active nodes to send a join-request to.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ChordBootstrapManager implements BootstrapManager {

	private static Logger log = SimLogger
			.getLogger(ChordBootstrapManager.class);

	/**
	 * list of active node
	 */
	private final List<ChordNode> availableNodes = new LinkedList<ChordNode>();

	private static final List<ChordBootstrapManager> instances = new Vector<ChordBootstrapManager>();

	public ChordBootstrapManager() {
		instances.add(this);
	}

	public static ChordBootstrapManager getInstance(ChordID id) {
		for (ChordBootstrapManager cbm : instances) {
			for (ChordNode node : cbm.getAvailableNodes()) {
				if (node.getOverlayID().equals(id)) {
					return cbm;
				}
			}
		}
		return null;
	}

	public static List<ChordNode> getAllAvailableNodes() {
		List<ChordNode> result = new Vector<ChordNode>();
		for (ChordBootstrapManager cbm : instances) {
			result.addAll(cbm.getAvailableNodes());
		}
		return result;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> list = new LinkedList<TransInfo>();
		for (OverlayNode cNode : availableNodes)
			list.add(((ChordNode) cNode).getLocalChordContact().getTransInfo());
		return list;
	}

	@Override
	public void registerNode(OverlayNode node) {
		if (node instanceof ChordNode)
			availableNodes.add((ChordNode) node);
	}

	@Override
	public void unregisterNode(OverlayNode node) {

		if (node instanceof ChordNode) {
			ChordNode n = (ChordNode) node;
			availableNodes.remove(n);
		}
	}

	public boolean isEmpty() {
		return getAvailableNodes().isEmpty();
	}

	public List<ChordNode> getAvailableNodes() {
		return new ArrayList<ChordNode>(availableNodes);
	}

	public int getNumOfAvailableNodes() {
		return getAvailableNodes().size();
	}

	public ChordNode getOverlayNode(TransInfo transInfo) {
		for (ChordNode node : availableNodes) {
			if (node.getTransInfo().equals(transInfo)) {
				return node;
			}
		}
		return null;
	}

	public ChordContact getRandomAvailableNode() {
		int index = Simulator.getRandom().nextInt(availableNodes.size());
		return availableNodes.get(index).getLocalChordContact();
	}
}
