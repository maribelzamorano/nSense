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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordOverlayAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;

/**
 * This class analyzers some metrics such as the number of online Peers
 * at a specified time period, the number of join and leave events in an
 * interval 
 *
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class PeerStore extends AbstractMetricStore {

	private static PeerStore instance = new PeerStore();

	private LinkedList<OverlayNodeProxy> nodeList = new LinkedList<OverlayNodeProxy>();

	private int num_registered_Node;
	
	public static PeerStore getInstance() {
		return instance;
	}

	private PeerStore() {
		// Private constructor prevents instantiation from other classes
	}

	enum Metrics {
		NoOnlinePeer("Number of online Peers"),
		NoOnlineEvent("NoOnlineEvent"),
		NoOfflineEvent("NoOfflineEvent");
		
		private final String label;

		Metrics(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public List<String> getMetricList() {
		
		ArrayList<String> metricList = new ArrayList<String>();
		for (Metrics metric : Metrics.values()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}

	@Override
	public double getMeasureValue(String metric, long beginTime, long endTime) {
		long atTime = (beginTime + endTime) / 2;
		if (Metrics.NoOnlinePeer.equals(Metrics.valueOf(metric))) {
			return getNumberOfPeerAtTime(atTime);
		} 
		else if (Metrics.NoOnlineEvent.equals(Metrics.valueOf(metric))) {
			return getNoOnlineEvent(beginTime, endTime);
		}
		else if (Metrics.NoOfflineEvent.equals(Metrics.valueOf(metric))) {
			return getNoOfflineEvent(beginTime, endTime);
		}
		else {
			return -1;
		}

	}
	
	public double getNumberOfPeerAtTime(long atTime) {
		double count = 0;
		for (OverlayNodeProxy node : nodeList) {
			if (isNodeOnline(atTime, node)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * in interval [begin, end) 
	 */
	public double getNoOnlineEvent(long beginTime, long endTime) {
		double count = 0;
		for (OverlayNodeProxy node : nodeList) {
			if (beginTime <= node.getJoin_Timestamp()
					&& node.getJoin_Timestamp() < endTime) {
				count++;
			}
		}
		return count;
	}
	
	public double getNoOfflineEvent(long beginTime, long endTime) {
		double count = 0;
		for (OverlayNodeProxy node : nodeList) {
			if (beginTime <= node.getLeave_Timestamp()
					&& node.getLeave_Timestamp() < endTime) {
				count++;
			}
		}
		return count;
	}
	
	private boolean containNode(ChordNode chordNode) {
		for (OverlayNodeProxy node : nodeList) {
			if (node.getId().equals(chordNode.getOverlayID())) {
				return true;
			}
		}
		return false;
	}

	private boolean isNodeOnline(long atTime, OverlayNodeProxy node) {
		return (node.getJoin_Timestamp() <= atTime)
				&& ((atTime < node.getLeave_Timestamp()) || (node
						.getLeave_Timestamp() < 0));
	}
	
	public void registerNewJoin(ChordNode chordNode,  long timeStamp) {
		
		if(! ChordOverlayAnalyzer.peerStats ){
			return;
		}
		if (!containNode(chordNode)) {
			num_registered_Node++;
		}
		OverlayNodeProxy joinNode = new OverlayNodeProxy(chordNode.getOverlayID());
		joinNode.setJoin_Timestamp(timeStamp);
		nodeList.add(joinNode);
	}

	public void registerNodeLeave(ChordNode chordNode, long timeStamp) {
		
		if(! ChordOverlayAnalyzer.peerStats ){
			return;
		}
		for (OverlayNodeProxy node : nodeList) {
			// leave_Timestamp must have no value because node can join and rejoin voluntarily
			if (node.getId().equals(chordNode.getOverlayID())
					&& node.getLeave_Timestamp() < 0) {
				node.setLeave_Timestamp(timeStamp);
			}
		}
	}

	// getter and setter
	public LinkedList<OverlayNodeProxy> getNodeList() {
		return nodeList;
	}

	public int getNum_registered_Node() {
		return num_registered_Node;
	}

	
}
