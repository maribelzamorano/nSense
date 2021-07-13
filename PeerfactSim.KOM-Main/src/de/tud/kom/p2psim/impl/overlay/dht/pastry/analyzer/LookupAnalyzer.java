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

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.analyzer.AbstractEvaluationAnalyzer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * Analyzer to check the complexity of lookup operations
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class LookupAnalyzer extends AbstractEvaluationAnalyzer {

	private static LookupAnalyzer instance;

	private final List<Integer> hopCounts = new LinkedList<Integer>();

	private int rightResponsibleCounts = 0;

	private int wrongResponsibleCounts = 0;

	private HashMap<PastryID, PastryNode> pNodesForId;

	private LinkedList<PastryNode> pNodes;

	StatHelper<Integer> stats = new StatHelper<Integer>();

	public LookupAnalyzer() {
		instance = this;

		setFlushEveryLine(true);
		setFolderName("Pastry");
		setOutputFileName("Lookups.dat");

		// List of all pastry nodes
		pNodesForId = new HashMap<PastryID, PastryNode>();
		pNodes = new LinkedList<PastryNode>();

		for (Host h : GlobalOracle.getHosts()) {
			PastryNode pNode = (PastryNode) h.getOverlay(PastryNode.class);
			if (pNode != null) {
				pNodesForId.put(pNode.getOverlayID(), pNode);
				pNodes.add(pNode);
			}
		}
	}

	@Override
	protected String generateHeadlineForMetrics() {

		String headline = "#time[s]\n" + "#time[min]\n" + "#PRESENT Peers\n"
				+ "#TO_JOIN Peers\n" + "#CHURN Peers\n" + "#NumOfLookups\n"
				+ "#Lookup hops(avg)\n" + "#Lookup hops(st.Dev.Minus)\n"
				+ "#Lookup hops(st.Dev.Plus)\n" + "#Lookup hops(median)";

		return headline;
	}

	@Override
	protected String generateEvaluationMetrics() {
		String line = Simulator.getCurrentTime() / Simulator.SECOND_UNIT + "\t"
				+ Simulator.getCurrentTime() / Simulator.MINUTE_UNIT + "\t"
				+ getNumOfPresentNodes() + "\t" + getNumOfJoiningNodes() + "\t"
				+ getNumOfChurnAffectedNodes() + "\t";

		line += hopCounts.size() + "\t";

		double[] avgAndStDev = stats
				.computeAverageAndStandardDeviation(hopCounts);

		line += avgAndStDev[0] + "\t" + avgAndStDev[1] + "\t" + avgAndStDev[2]
				+ "\t" + avgAndStDev[3] + "\t" + stats.computeMedian(hopCounts);

		hopCounts.clear();

		return line;
	}

	public static LookupAnalyzer getInstance() {
		return instance;
	}

	public static void lookupFinished(PastryID target,
			PastryNode responsibleNode, int hopCount) {
		LookupAnalyzer analyzer = getInstance();
		if (analyzer != null) {
			analyzer.addLookup(hopCount);
			analyzer.checkIfRightPeerResponsible(target, responsibleNode);
		}
	}

	public void addLookup(int hopCount) {
		hopCounts.add(hopCount);
	}

	public void checkIfRightPeerResponsible(PastryID target,
			PastryNode responsibleNode) {

		PastryNode realResp = getGloabalResponsibleNode(target);

		if (realResp != null) {
			if (responsibleNode == realResp) {
				rightResponsibleCounts++;
			} else {
				wrongResponsibleCounts++;
			}
		}
	}

	private static int getNumOfPresentNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfChurnAffecteedNodes = 0;

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(PastryNode.class);
			if (olNode != null && olNode instanceof PastryNode) {
				PastryNode cNode = (PastryNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.PRESENT)
					numOfChurnAffecteedNodes++;
			}
		}
		return numOfChurnAffecteedNodes;
	}

	private static int getNumOfChurnAffectedNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfChurnAffecteedNodes = 0;

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(PastryNode.class);
			if (olNode != null && olNode instanceof PastryNode) {
				PastryNode cNode = (PastryNode) olNode;

				if (cNode.absentCausedByChurn())
					numOfChurnAffecteedNodes++;
			}
		}
		return numOfChurnAffecteedNodes;
	}

	private static int getNumOfJoiningNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfJoiningNodes = 0;

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(PastryNode.class);
			if (olNode != null && olNode instanceof PastryNode) {
				PastryNode cNode = (PastryNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.TO_JOIN)
					numOfJoiningNodes++;
			}
		}
		return numOfJoiningNodes;
	}

	private PastryNode getGloabalResponsibleNode(final PastryID target) {
		List<Host> hosts = GlobalOracle.getHosts();

		List<PastryID> idsOfPresentNodes = new LinkedList<PastryID>();

		for (PastryNode pNode : pNodes) {
			if (pNode.getPeerStatus() == PeerStatus.PRESENT) {
				idsOfPresentNodes.add(pNode.getOverlayID());
			}
		}

		if (idsOfPresentNodes.size() > 0) {
			Comparator<PastryID> comp = new Comparator<PastryID>() {
				@Override
				public int compare(PastryID id1, PastryID id2) {
					/*
					 * This compares the absolute distance of the two id to the
					 * given id.
					 */
					BigInteger d1 = id1.getMinAbsDistance(target);
					BigInteger d2 = id2.getMinAbsDistance(target);

					return d1.compareTo(d2);
				}
			};
			Collections.sort(idsOfPresentNodes, comp);
			PastryID nearestID = idsOfPresentNodes.get(0);

			return pNodesForId.get(nearestID);

		} else {
			return null;
		}

	}
}
