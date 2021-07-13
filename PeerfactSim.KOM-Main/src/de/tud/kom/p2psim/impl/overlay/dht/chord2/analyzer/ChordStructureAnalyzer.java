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

import java.util.HashMap;
import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.analyzer.AbstractEvaluationAnalyzer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * Analyzer to regularly check the structure of the Chord ring
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class ChordStructureAnalyzer extends AbstractEvaluationAnalyzer {

	public ChordStructureAnalyzer() {
		setFlushEveryLine(true);
		setFolderName("Chord");
		setOutputFileName("Structure.dat");
	}

	@Override
	protected String generateEvaluationMetrics() {
		String line = "";

		HashMap<ChordID, ChordNode> cNodes = getAllPresentChordNodes();
		int nodeCount = cNodes.size();

		boolean succBasicRingConnected = true;
		int succBasicRingBreaks = 0;
		boolean succRingIncludesAll = true;
		int succNotIncluded = 0;
		boolean succExtendedRingConnected = true;
		int succCountedRingSize = 0;

		if (cNodes.size() > 0) {
			ChordNode startNode = getStartingNode(cNodes);
			if (startNode != null) {
				succCountedRingSize++;
				cNodes.remove(startNode.getOverlayID());
				ChordNode currentNode = startNode;

				while (succExtendedRingConnected && succRingIncludesAll
						&& !cNodes.isEmpty()) {
					ChordContact nextContact = currentNode
							.getChordRoutingTable().getSuccessor();

					List<ChordContact> allContacts = currentNode
							.getChordRoutingTable().getAllDistantSuccessor();

					if (nextContact != null) {
						ChordID nextID = nextContact.getOverlayID();

						if (nextID.compareTo(startNode.getOverlayID()) == 0) {
							// Ring is closed

							if (!cNodes.isEmpty()) {
								succNotIncluded = cNodes.size();
								succRingIncludesAll = false;
							}

						} else {
							currentNode = cNodes.remove(nextID);

							if (currentNode == null) {
								succBasicRingConnected = false;
								succBasicRingBreaks++;

								for (ChordContact alternativeSucc : allContacts) {
									currentNode = cNodes.remove(alternativeSucc
											.getOverlayID());
									if (currentNode != null)
										break;
								}

								if (currentNode == null)
									succExtendedRingConnected = false;
							}

							if (currentNode != null)
								succCountedRingSize++;
						}
					}
				}
			}
		}

		cNodes = getAllPresentChordNodes();

		boolean predBasicRingConnected = true;
		int predBasicRingBreaks = 0;
		boolean predRingIncludesAll = true;
		int predNotIncluded = 0;
		boolean predExtendedRingConnected = true;
		int predCountedRingSize = 0;

		if (cNodes.size() > 0) {
			ChordNode startNode = getStartingNode(cNodes);
			if (startNode != null) {
				predCountedRingSize++;
				cNodes.remove(startNode.getOverlayID());
				ChordNode currentNode = startNode;

				while (predExtendedRingConnected && predRingIncludesAll
						&& !cNodes.isEmpty()) {
					ChordContact nextContact = currentNode
							.getChordRoutingTable().getPredecessor();

					List<ChordContact> allContacts = currentNode
							.getChordRoutingTable().getAllDistantPredecessor();

					if (nextContact != null) {
						ChordID nextID = nextContact.getOverlayID();

						if (nextID.compareTo(startNode.getOverlayID()) == 0) {
							// Ring is closed

							if (!cNodes.isEmpty()) {
								predNotIncluded = cNodes.size();
								predRingIncludesAll = false;
							}

						} else {
							currentNode = cNodes.remove(nextID);

							if (currentNode == null) {
								predBasicRingConnected = false;
								predBasicRingBreaks++;

								for (ChordContact alternativeSucc : allContacts) {
									currentNode = cNodes.remove(alternativeSucc
											.getOverlayID());
									if (currentNode != null)
										break;
								}

								if (currentNode == null)
									predExtendedRingConnected = false;
							}

							if (currentNode != null)
								predCountedRingSize++;
						}
					}
				}
			}
		}

		line += Simulator.getCurrentTime() / Simulator.SECOND_UNIT + "\t"
				+ Simulator.getCurrentTime() / Simulator.MINUTE_UNIT + "\t"
				+ nodeCount + "\t" + getNumOfJoiningNodes() + "\t"
				+ getNumOfChurnAffectedNodes() + "\t" + succCountedRingSize
				+ "\t" + succBasicRingConnected + "\t" + succBasicRingBreaks
				+ "\t" + succExtendedRingConnected + "\t" + succRingIncludesAll
				+ "\t" + succNotIncluded + "\t" + predCountedRingSize + "\t"
				+ predBasicRingConnected + "\t" + predBasicRingBreaks + "\t"
				+ predExtendedRingConnected + "\t" + predRingIncludesAll + "\t"
				+ predNotIncluded + "\n";

		return line;
	}

	private static HashMap<ChordID, ChordNode> getAllPresentChordNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		HashMap<ChordID, ChordNode> chordNodes = new HashMap<ChordID, ChordNode>();

		for (Host host : hosts) {
			OverlayNode olNode = host.getOverlay(ChordNode.class);
			if (olNode != null && olNode instanceof ChordNode) {
				ChordNode cNode = (ChordNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.PRESENT)
					chordNodes.put(cNode.getOverlayID(), cNode);
			}
		}
		return chordNodes;
	}

	private static ChordNode getStartingNode(HashMap<ChordID, ChordNode> nodes) {
		if (nodes.isEmpty())
			return null;
		return nodes.entrySet().iterator().next().getValue();
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

	@Override
	protected String generateHeadlineForMetrics() {
		String headline = "#time[sec]\n" + "#time[min]\n" + "#PRESENT nodes\n"
				+ "#TO_JOIN nodes\n" + "#CHURN nodes\n" + "#Succ ring size\n"
				+ "#Succ ring connected?\n" + "#Succ num succ ring breaks\n"
				+ "#Succ ring connected (using backups)?\n"
				+ "#Succ ring includes all?\n"
				+ "#Succ num not included nodes\n" + "#Pred ring size\n"
				+ "#Pred ring connected?\n" + "#Pred num pred ring breaks\n"
				+ "#Pred ring connected (using backups)?\n"
				+ "#Pred ring includes all?\n"
				+ "#Pred num not included nodes\n";

		return headline;
	}
}
