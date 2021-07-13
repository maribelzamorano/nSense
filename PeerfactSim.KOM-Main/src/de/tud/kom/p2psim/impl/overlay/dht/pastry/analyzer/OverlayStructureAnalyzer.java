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


package de.tud.kom.p2psim.impl.overlay.dht.pastry.analyzer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.analyzer.AbstractEvaluationAnalyzer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class OverlayStructureAnalyzer extends AbstractEvaluationAnalyzer {

	public OverlayStructureAnalyzer() {
		setFlushEveryLine(true);
		setFolderName("Pastry");
		setOutputFileName("Structure.dat");
		setTimeBetweenAnalyzeSteps(10 * Simulator.SECOND_UNIT);
	}

	@Override
	protected String generateHeadlineForMetrics() {
		String headline = "#time[sec]\n" + "#time[min]\n" + "#PRESENT nodes\n"
				+ "#TO_JOIN nodes\n" + "#CHURN nodes\n" + "#ABSENT nodes\n"
				+ "#Neighbors_Sum\n" + "#Neighbors_AVG\n" + "#Neighbors_MIN\n"
				+ "#Neighbors_MAX\n" + "#Leafs_AVG\n" + "#Leafs_MIN\n"
				+ "#Leafs_MAX\n";

		return headline;
	}

	@Override
	protected String generateEvaluationMetrics() {

		int present = 0;
		int toJoin = 0;
		int absent = 0;
		int churn = 0;

		int sumOfNeighbors = 0;
		int numOfNeighborsMin = Integer.MAX_VALUE;
		int numOfNeighborsMax = Integer.MIN_VALUE;

		int sumOfLeafs = 0;
		int numOfLeafsMin = Integer.MAX_VALUE;
		int numOfLeafsMax = Integer.MIN_VALUE;

		Collection<PastryNode> pNodes = getPastryNodes();

		for (PastryNode n : pNodes) {
			PeerStatus s = n.getPeerStatus();

			if (s == PeerStatus.PRESENT) {
				present++;
				int numOfNeighbors = n.getNumOfAllNeighbors();
				if (numOfNeighbors < numOfNeighborsMin)
					numOfNeighborsMin = numOfNeighbors;
				if (numOfNeighbors > numOfNeighborsMax)
					numOfNeighborsMax = numOfNeighbors;
				sumOfNeighbors += numOfNeighbors;

				int numOfLeafs = n.getLeafSetNodes().size();
				if (numOfLeafs < numOfLeafsMin)
					numOfLeafsMin = numOfLeafs;
				if (numOfLeafs > numOfLeafsMax)
					numOfLeafsMax = numOfLeafs;
				sumOfLeafs += numOfLeafs;

			} else if (s == PeerStatus.TO_JOIN)
				toJoin++;
			else if (s == PeerStatus.ABSENT)
				if (n.wantsToBePresent())
					churn++;
				else
					absent++;
		}

		double avgNumOfNeighbors = 0;
		double avgNumOfLeafs = 0;
		if (pNodes.size() > 0) {
			avgNumOfNeighbors = (double) sumOfNeighbors / pNodes.size();
			avgNumOfLeafs = (double) sumOfLeafs / pNodes.size();
		}

		String line = Simulator.getCurrentTime() / Simulator.SECOND_UNIT
				+ "\t"
				+ Simulator.getCurrentTime() / Simulator.MINUTE_UNIT
				+ "\t"
				+ present
				+ "\t"
				+ toJoin
				+ "\t"
				+ churn
				+ "\t"
				+ absent
				+ "\t"
				+ sumOfNeighbors
				+ "\t"
				+ avgNumOfNeighbors
				+ "\t"
				+ (numOfNeighborsMin == Integer.MAX_VALUE ? 0
						: numOfNeighborsMin)
				+ "\t"
				+ (numOfNeighborsMax == Integer.MIN_VALUE ? 0
						: numOfNeighborsMax) + "\t" + avgNumOfLeafs + "\t"
				+ (numOfLeafsMin == Integer.MAX_VALUE ? 0 : numOfLeafsMin)
				+ "\t"
				+ (numOfLeafsMax == Integer.MIN_VALUE ? 0 : numOfLeafsMax)
				+ "\n";

		return line;
	}

	private static Collection<PastryNode> getPastryNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		Collection<PastryNode> pNodes = new LinkedList<PastryNode>();

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(PastryNode.class);
			if (olNode != null && olNode instanceof PastryNode) {
				pNodes.add((PastryNode) olNode);
			}
		}
		return pNodes;
	}

}
