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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer;

import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.analyzer.AbstractEvaluationAnalyzer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * Analyzer to check the complexity of lookup operations
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class LookupComplexityAnalyzer extends AbstractEvaluationAnalyzer {

	private static LookupComplexityAnalyzer instance;

	private final List<Integer> hopCounts = new LinkedList<Integer>();

	StatHelper<Integer> stats = new StatHelper<Integer>();

	public LookupComplexityAnalyzer() {
		instance = this;

		setFlushEveryLine(true);
		setFolderName("Chord");
		setOutputFileName("LookupComplexity.dat");
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

	public static LookupComplexityAnalyzer getInstance() {
		return instance;
	}

	public void lookupFinished(int hopCount) {
		hopCounts.add(hopCount);
	}

	private static int getNumOfPresentNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfChurnAffecteedNodes = 0;

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(ChordNode.class);
			if (olNode != null && olNode instanceof ChordNode) {
				ChordNode cNode = (ChordNode) olNode;

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
			OverlayNode olNode = host.getOverlay(ChordNode.class);
			if (olNode != null && olNode instanceof ChordNode) {
				ChordNode cNode = (ChordNode) olNode;

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
			OverlayNode olNode = host.getOverlay(ChordNode.class);
			if (olNode != null && olNode instanceof ChordNode) {
				ChordNode cNode = (ChordNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.TO_JOIN)
					numOfJoiningNodes++;
			}
		}
		return numOfJoiningNodes;
	}

}
