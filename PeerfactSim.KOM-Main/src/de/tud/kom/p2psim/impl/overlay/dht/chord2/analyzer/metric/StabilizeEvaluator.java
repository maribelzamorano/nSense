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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordOverlayAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.MathHelper;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.RoutingTableContructor;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class evaluates the stabilization of Chord overlay
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class StabilizeEvaluator implements Serializable{

	private static Logger log = SimLogger.getLogger(StabilizeEvaluator.class);
			
	private ArrayList<ChordContact> contactList = new ArrayList<ChordContact>();

	private HashMap<ChordContact, ChordRoutingTable> routingTableMap = new HashMap<ChordContact, ChordRoutingTable>();

	public static enum Metrics {
		NoOnlinePeer("Number of online Peers"),
		InvalidNeighbours("Number of invalid Neighbours"),
		InvalidFingerPoints("Number of invalid finger points"),
		StabilityRation("Stability ration of Overlay"),
		InvalidDistantNeighbours("Invalid Distant Neighbours");
		private final String label;

		Metrics(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public double getMeasureValue(String metric) {
		
		if (Metrics.NoOnlinePeer.equals(Metrics.valueOf(metric))) {
			return getNumberOfPeer();
		}
		else if (Metrics.InvalidNeighbours.equals(Metrics.valueOf(metric))) {
			return getNoInvalidNeighbours();
		}
		else if (Metrics.InvalidFingerPoints.equals(Metrics.valueOf(metric))) {
			return getNoInvalidFingerPoints();
		}
		else if (Metrics.InvalidDistantNeighbours.equals(Metrics.valueOf(metric))) {
			return getNoInvalidDistantNeighbours();
		}
		else {
			return -1;
		}
	}

	public static List<String> getMetricList() {
		ArrayList<String> metricList = new ArrayList<String>();
		for (Metrics metric : Metrics.values()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}

	private double getNumberOfPeer(){
		return contactList.size();
	}
	
	private double getNoInvalidNeighbours() {
		
		double invalidNeighbour = 0;
		ArrayList<ChordContact> sortedContacts = (ArrayList<ChordContact>) contactList.clone();
		Collections.sort(sortedContacts);
		
		for (ChordContact key : sortedContacts) {
			int index = sortedContacts.indexOf(key);
			ChordRoutingTable routingTable = routingTableMap.get(key);
			// check predecessor and successor
			ChordContact succ, pred;
			if (index == sortedContacts.size() - 1) {
				succ = sortedContacts.get(0);
			} else {
				succ = sortedContacts.get(index + 1);
			}

			if (index == 0) {
				pred = sortedContacts.get(sortedContacts.size() - 1);
			} else {
				pred = sortedContacts.get(index - 1);
			}

			if (!routingTable.getPredecessor().equals(pred)) {
				invalidNeighbour++;
				log.error("invalid predecessor "
						+" node = " + key
						+" value = " + routingTable.getPredecessor()
						+" expected = " + pred);
			}
			if (!routingTable.getSuccessor().equals(succ)) {
				invalidNeighbour++;
				log.error("invalid successsor "
						+" node = " + key
						+" value = " + routingTable.getSuccessor()
						+" expected = " + succ);
				ChordContact invalsucc = routingTable.getSuccessor();
//				ChordRoutingTable succTable = routingTableMap.get(invalsucc);
//				if(succTable == null){
//					log.error("pred of succ null routing table");
//				}else{
//					log.error("pred of succ " + succTable.getPredecessor());
//				}
				for(ChordContact key2 : routingTableMap.keySet()){
					if(key2.getOverlayID().equals(invalsucc.getOverlayID())){
						ChordRoutingTable succTable2 = routingTableMap.get(key2);
						log.error("pred of succ " + succTable2.getPredecessor());
					}
				}
			}
		}
		return invalidNeighbour;
	}
	
	private double getNoInvalidDistantNeighbours() {
		double invalid = 0;
		ArrayList<ChordContact> sortedContacts = (ArrayList<ChordContact>) contactList.clone();
		Collections.sort(sortedContacts);
		
		for (ChordContact key : sortedContacts) {
			int index = sortedContacts.indexOf(key);
			ChordRoutingTable routingTable = routingTableMap.get(key);
			// successor list
			int begin = (index + 1) % sortedContacts.size();
			List<ChordContact> successors = getSubList((ArrayList<ChordContact>)sortedContacts.clone(), begin,
					ChordConfiguration.STORED_NEIGHBOURS, true); 
			for (int i = 0; i < ChordConfiguration.STORED_NEIGHBOURS; i++) {
				ChordContact succ = routingTable.getDistantSuccessor(i);
				if (succ == null || ! successors.contains(succ)) {
					log.info("Invalid Distant Neighbours Successor"
							+ " node = " + key
							+ " actualy = " + succ
							+ " expect = ...");
					invalid++;
				}
			}
			//predecessor list
			begin = (index - 1 + sortedContacts.size()) % sortedContacts.size();
			List<ChordContact> predcessors = getSubList((ArrayList<ChordContact>)sortedContacts.clone(), begin,
					ChordConfiguration.STORED_NEIGHBOURS, false); 
			for (int i = 0; i < ChordConfiguration.STORED_NEIGHBOURS; i++) {
				ChordContact pred = routingTable.getDistantPredecessor(i);
				if (pred == null || ! predcessors.contains(pred)) {
					log.info("Invalid Distant Neighbours Predecessor"
							+ " node = " + key
							+ " actualy = " + pred
							+ " expect = ...");
					invalid++;
				}
			}
		}
		return invalid;
	}
	
	
	private List<ChordContact> getSubList(List<ChordContact> list,
			int beginIndex, int length, boolean clockwise) {
		List<ChordContact> result = new ArrayList<ChordContact>();
		int count = 0;
		while (count < length) {
			result.add(list.get(beginIndex));
			if (clockwise) {
				beginIndex++;
				if (beginIndex == list.size()) {
					beginIndex = 0;
				}
			} else {
				beginIndex--;
				if (beginIndex < 0) {
					beginIndex = list.size() - 1;
				}
			}
			count++;
		}
		return result;
	}
	
	private double getNoInvalidFingerPoints() {

		int invalidFingerPoint = 0;
		initRingForm();
		for (ChordContact key : contactList) {
			ChordRoutingTable routingTable = routingTableMap.get(key);
			Set<ChordContact> contactSet = new HashSet<ChordContact>(contactList);
			ChordContact[] expeted = RoutingTableContructor.getFingerTable(key, contactSet);
			
			for (int i = 0; i < ChordID.KEY_BIT_LENGTH; i++) {
				if(! routingTable.getFingerEntry(i).equals(expeted[i])){
				BigInteger point = MathHelper.getFingerStartValue(key
				.getOverlayID().getValue(), i);
					String pointHex = point.toString(16);
					while (pointHex.length() < 40) {
						pointHex = "0" + pointHex;
					}
					log.debug("invalid finger point node = " + key
							+" point = " + pointHex.substring(0, 5)
							+" value = " + routingTable.getFingerEntry(i)
							+" expected = " + expeted[i]
							+" index " + i);
					invalidFingerPoint++;
				}
			}	
		}
		return invalidFingerPoint;
	}
	
	private ArrayList<BigInteger> sortedContact;
	
	private void initRingForm() {
		sortedContact = new ArrayList<BigInteger>();
		int bitLength = ChordID.KEY_BIT_LENGTH;
		for (ChordContact node : contactList) {
			sortedContact.add(node.getOverlayID().getValue());
			BigInteger twoPowBitLength = new BigInteger("2").pow(bitLength);
			sortedContact.add(node.getOverlayID().getValue().add(twoPowBitLength));
		}
		Collections.sort(sortedContact);
	}
	
	// Getter and Setter

	public ArrayList<ChordContact> getContactList() {
		return contactList;
	}

	public void setContactList(ArrayList<ChordContact> contactList) {

		if(! ChordOverlayAnalyzer.stabilizeStats ){
			return;
		}
		this.contactList = contactList;
	}

	public HashMap<ChordContact, ChordRoutingTable> getRoutingTableMap() {
		return routingTableMap;
	}

	public void setRoutingTableMap(
			HashMap<ChordContact, ChordRoutingTable> routingTableMap) {
		
		if(! ChordOverlayAnalyzer.stabilizeStats ){
			return;
		}
		this.routingTableMap = routingTableMap;
	}

	

}
