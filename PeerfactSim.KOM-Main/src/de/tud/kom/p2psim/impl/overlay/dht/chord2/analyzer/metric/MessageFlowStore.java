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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MessageFlowStore implements Serializable{

	private static MessageFlowStore instance = new MessageFlowStore();

	private static Logger log = SimLogger.getLogger(MessageFlowStore.class);
			
	private HashMap<Integer, MessageFlowProxy> messageFlowStorage;
	
	public static MessageFlowStore getInstance() {
		return instance;
	}
	
	private MessageFlowStore(){
		
		// Private constructor prevents instantiation from other classes
		messageFlowStorage = new HashMap<Integer, MessageFlowProxy>();
	}
	
	public static enum Metrics {
		NoInitiate("NoInitiate"),
		NoForwardQuery("NoInitiate");
		
		private final String label;

		Metrics(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public HashMap<ChordID, double []> getMeasureValue() {
		// peer ID - >  (loolupStarter,number of lookups) 
		HashMap<ChordID, HashMap<ChordID, Integer>> lookupMap = new HashMap<ChordID, HashMap<ChordID,Integer>>();
		Set<Integer> keySet = messageFlowStorage.keySet();
		
		for(int key : keySet){
			
			MessageFlowProxy messageFlow = messageFlowStorage.get(key);
			ArrayList<ChordID> hops = messageFlow.getHops(); 
			ChordID starter = hops.get(0);
			
			for (int index = 1; index < hops.size(); index++) {
				ChordID overNode = hops.get(index);
				HashMap<ChordID, Integer> lookupStart = lookupMap.get(overNode);
				if (lookupStart == null) {
					lookupStart = new HashMap<ChordID, Integer>();
					lookupStart.put(starter, 1);
					lookupMap.put(overNode, lookupStart);
				} else {
					Integer noLookup = lookupStart.get(starter);
					if (noLookup == null) {
						lookupStart.put(starter, 1);
					} else {
						lookupStart.put(starter, noLookup + 1);
					}
				}
			}
		}
		HashMap<ChordID, double []> result = new HashMap<ChordID, double[]>();
		Set<ChordID> nodeList = lookupMap.keySet();
		for(ChordID node : nodeList){
			HashMap<ChordID, Integer> lookupStart = lookupMap.get(node);
			Set<ChordID> startersList = lookupStart.keySet();
			double[] queries = new double[2];
			queries[0] = startersList.size();
			for(ChordID starter : startersList){
				int noQuery = lookupStart.get(starter);
				queries[1] += noQuery;
			}
			queries[1] = queries[1] / queries [0];
			result.put(node, queries);
		}
		return result;
	}

	public static List<String> getMetricList() {
		ArrayList<String> metricList = new ArrayList<String>();
		for (Metrics metric : Metrics.values()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}

	
	/**
	 * Called by <code>MessageCounter</code>
	 * @param msgFlowId
	 * @param senderId
	 * @param receiverID
	 */
	public void addIntermediateHop(int msgFlowId, ChordID senderId, ChordID receiverID) {
		MessageFlowProxy messageFlow = messageFlowStorage.get(msgFlowId);
		if (messageFlow == null) {
			// new message path
			MessageFlowProxy newMessageFlow = new MessageFlowProxy(msgFlowId);
			newMessageFlow.getHops().add(senderId);
			newMessageFlow.getHops().add(receiverID);
			messageFlowStorage.put(msgFlowId, newMessageFlow);
		} else {
			// add next sender
			ArrayList<ChordID> hops = messageFlow.getHops();
			hops.add(receiverID);
		}
	}

}
