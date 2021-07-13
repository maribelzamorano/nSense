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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordOverlayAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.LookupProxy.Status;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class collects and evaluates the lookup result
 *  
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class LookupStore extends AbstractMetricStore{

	private static Logger log = SimLogger.getLogger(LookupStore.class);
			
	private static LookupStore instance = new LookupStore();
	
	private LinkedList<LookupProxy> lookupList;
	
	
	public static enum Metrics {
		AverageLookupTimeInSec("Average Lookup Time In Sec"),
		AverageHopsPerLookup("Average Hops per Lookup"),
		LookupPerMin("NoLookup per min"),
		RationSuccLookup("RationSuccLookup"),
		RationValidLookupResult("RationValidLookupResult"),
		RationUnfinishedLookup("RationUnfinishedLookup"),
		SumUnfinishedLookup("Number of unfinished or time out Lookup"),
		SumStartedLookup("Number of started Lookup");
		
		private final String label;

		Metrics(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public static LookupStore getInstance() {
		return instance;
	}

	private LookupStore() {
		// Private constructor prevents instantiation from other classes
		lookupList = new LinkedList<LookupProxy>();
	}
	
	@Override
	public List<String> getMetricList() {
		ArrayList<String> metricList = new ArrayList<String>();
		for (Metrics metric : Metrics.values()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}
	
	@Override
	public double getMeasureValue(String metric, long begin, long end) {
		double min = (double) (end - begin) / Simulator.MINUTE_UNIT;
		
		if (Metrics.AverageLookupTimeInSec.equals(Metrics.valueOf(metric))) {
			return getAverageLookupTime(begin, end);
		}
		else if (Metrics.AverageHopsPerLookup.equals(Metrics.valueOf(metric))) {
			return getAverageHopsPerLookup(begin, end);
		}
		else if (Metrics.LookupPerMin.equals(Metrics.valueOf(metric))) {
			return getNoLookup(begin, end) / min;
		}
		else if (Metrics.RationSuccLookup.equals(Metrics.valueOf(metric))) {
			return getRationSuccLookup(begin, end);
		}
		else if (Metrics.RationValidLookupResult.equals(Metrics.valueOf(metric))) {
			return getRationValidLookupResult(begin, end);
		}
		else if (Metrics.RationUnfinishedLookup.equals(Metrics.valueOf(metric))) {
			return getRationUnfinishedLookup(begin, end);
		}
		else if (Metrics.SumUnfinishedLookup.equals(Metrics.valueOf(metric))) {
			return getSumUnfinishedLookup(begin);
		}
		else if (Metrics.SumStartedLookup.equals(Metrics.valueOf(metric))) {
			return getSumStartedLookup(begin);
		}
		else{
			return -1;
		}
	}
	
	/**
	 * in interval [begin, end) 
	 */
	private double getAverageLookupTime(long beginTime, long endTime) {
		double sumtime = 0;
		double count = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
			&& lookup.getStartTimestamp() < endTime
			&& lookup.isValidResult() ) {
				sumtime += lookup.getReplyTimestamp()
						- lookup.getStartTimestamp();
				count++;
			}
		}
		if (count != 0) {
			return sumtime / (count * Simulator.SECOND_UNIT);
		} else {
			return 0;
		}
	}
	
	private double getAverageHopsPerLookup(long beginTime, long endTime) {
		double sumHops = 0;
		double count = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
			&& lookup.getStartTimestamp() < endTime
			&& lookup.isValidResult() ) {
				sumHops += lookup.getHop();
				count++;
			}
		}
		if (count != 0) {
			return sumHops / count;
		} else {
			return 0;
		}
	}
	
	private double getNoLookup(long beginTime, long endTime) {
		double noLookup = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
					&& lookup.getStartTimestamp() < endTime) {
				noLookup++;
			}
		}
		return noLookup;
	}
	
	private double getRationSuccLookup(long beginTime, long endTime) {
		double sumLookup = 0;
		double finishedLookup = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
					&& lookup.getStartTimestamp() < endTime){
				sumLookup++;
				if (LookupProxy.Status.FINISHED.equals(lookup.getEndStatus())) {
					finishedLookup++;
				}
			}
		}
		if (sumLookup != 0) {
			return finishedLookup / sumLookup;
		} else {
			return 1.0;
		}
	}
	
	private double getRationValidLookupResult(long beginTime, long endTime) {
		double sumLookup = 0;
		double validLookup = 0;
		double timeout = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
					&& lookup.getStartTimestamp() < endTime) {
				sumLookup++;
				if (lookup.isValidResult()) {
					validLookup++;
				}else{
					log.error("invalid look up "
							+ " id = " + lookup.getLookupID()
							+ " reply = " + (double)lookup.getReplyTimestamp() /Simulator.MINUTE_UNIT);
					if(lookup.getReplyTimestamp()<0){
						timeout++;
					}
				}
			}
		}
		if (sumLookup - validLookup > 0){
			log.error("invalid loook up"
					+" number = " + (sumLookup - validLookup) 
					+" / " + sumLookup
					+" at [" + beginTime/Simulator.MINUTE_UNIT+" "+endTime/Simulator.MINUTE_UNIT+"]"
					+" timeout = " + timeout);
					
		}
		if (sumLookup != 0) {
			return validLookup / sumLookup;
		} else {
			return 1.0;
		}
	}
	
	private double getRationUnfinishedLookup(long beginTime, long endTime) {
		double sumLookup = 0;
		double unfinishedLookup = 0;
		for (LookupProxy lookup : lookupList) {
			if (beginTime <= lookup.getStartTimestamp()
					&& lookup.getStartTimestamp() < endTime) {
				sumLookup++;
				if (lookup.getEndStatus() == null || 
						lookup.getEndStatus().equals(LookupProxy.Status.TIMEOUT)) {
					unfinishedLookup++;
				}
			}
		}
		if (sumLookup != 0) {
			return unfinishedLookup / sumLookup;
		} else {
			return 1.0;
		}
	}
	
	private double getSumUnfinishedLookup(long begin) {

		int sumLookup = 0;
		for (LookupProxy lookup : lookupList) {

			if (lookup.getStartTimestamp() < begin
					&& ((lookup.getEndStatus() == null) || (lookup
							.getEndStatus() == Status.TIMEOUT))) 
			{
				sumLookup++;
			}
		}
		return sumLookup;
	}
	
	private double getSumStartedLookup(long begin) {

		int sumLookup = 0;
		for (LookupProxy lookup : lookupList) {
			if (lookup.getStartTimestamp() < begin) {
				sumLookup++;
			}
		}
		return sumLookup;
	}
	
	public void registerNewLookup(ChordContact starter, int id, long startTime){
		
		if(! ChordOverlayAnalyzer.lookupStats ){
			return;
		}
		LookupProxy lookup = new LookupProxy(id, startTime);
		lookupList.add(lookup);
	}
	
	public void lookupTimeOut(int id, long timeStamp) {
		
		if(! ChordOverlayAnalyzer.lookupStats ){
			return;
		}
		for (LookupProxy lookup : lookupList) {
			if (lookup.getLookupID() == id) {
				if(lookup.getEndStatus()!= null){
					// already has a value
					return;
				}
				lookup.setEndStatus(LookupProxy.Status.TIMEOUT);
				lookup.setValidResult(false);
				lookup.setReplyTimestamp(timeStamp);
				return;
			}
		}
		log.error("Lookup is not in store id = " + id);
	}
	
	public void lookupFinished(int id, long timeStamp,int hopCount, boolean valid) {
		
		if(! ChordOverlayAnalyzer.lookupStats ){
			return;
		}
		for (LookupProxy lookup : lookupList) {
			if (lookup.getLookupID() == id) {
				lookup.setEndStatus(LookupProxy.Status.FINISHED);
				lookup.setReplyTimestamp(timeStamp);
				lookup.setHop(hopCount);
				lookup.setValidResult(valid);
				return;
			}
		}
		log.error("Lookup is not in store id = " + id);
	}

	public LinkedList<LookupProxy> getLookupList() {
		return lookupList;
	}

	public int getNumOfLookup(){
		
		return lookupList.size();
	}
}
