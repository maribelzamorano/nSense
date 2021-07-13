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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.specific;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops.QueryTimeoutListener;
import de.tud.kom.p2psim.impl.application.filesharing2.FSEventListener;
import de.tud.kom.p2psim.impl.application.filesharing2.FSEvents;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class FSGnuplotAnalyzer extends DefaultGnuplotAnalyzer implements FSEventListener, QueryTimeoutListener  {

	QuerySuccessAndNHops qMetrics = new QuerySuccessAndNHops();
	
	Map<Message, Long> openQueries = new HashMap<Message, Long>();
	
	public FSGnuplotAnalyzer() {
		super();
		LiveMonitoring.addProgressValue(this.new QueryStatus());
	}
	
	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		addMetric(qMetrics.getAvgRespTime());
		addMetric(qMetrics.getQuerySuccess());
		addMetric(qMetrics.getQueryNHops());
		qMetrics.addListener(this);
		FSEvents.getInstance().addListener(this);
	}
	
	int queriesStarted = 0;
	int queriesSucceeded = 0;
	int queriesFailed = 0;
	
	int storesStarted = 0;
	int storesSucceeded = 0;
	
	public void stop(Writer w) {
		super.stop(w);
		System.out.println("Q's started: " + queriesStarted);
		System.out.println("Q's failed: " + queriesFailed);
		System.out.println("Q's succeeded: " + queriesSucceeded);
		System.out.println("Stores started: " + storesStarted);
		System.out.println("Stores succeeded: " + storesSucceeded);
	}

	@Override
	public void lookupStarted(OverlayContact initiator, Object queryUID) {
		//System.out.println("Query started" + queryUID);
		qMetrics.queryStarted(queryUID, Simulator.getCurrentTime());
		queriesStarted++;
	}

	@Override
	public void lookupSucceeded(OverlayContact initiator, Object queryUID, int hops) {
		//System.out.println("Message delivered " + queryUID);
		if (qMetrics.querySucceeded(queryUID, hops)) queriesSucceeded++;
	}

	@Override
	public void publishStarted(OverlayContact initiator, int keyToPublish, Object queryUID) {
		storesStarted++;
	}

	@Override
	public void publishSucceeded(OverlayContact initiator, OverlayContact holder, int keyPublished, Object queryUID) {
		storesSucceeded++;
	}

	@Override
	public void queryTimeouted(Object queryIdentifier) {
		queriesFailed++;
	}
	
	
	public class QueryStatus implements ProgressValue {

		@Override
		public String getName() {
			return "Queries";
		}

		@Override
		public String getValue() {
			
			int allQs = (queriesSucceeded+queriesFailed);
			
			return "Succ: " + queriesSucceeded + ", Fail: " + queriesFailed
			 + ((allQs==0)?"":(", Quota: " + (queriesSucceeded*100)/allQs + "%"));
		}
		
	}


	@Override
	public void lookupMadeHop(Object queryUID, OverlayContact hop) {
		if (!qMetrics.addHopToQuery(queryUID)) {
			//System.out.println("Hop assignment: Query" + queryUID + "was never started.");
		}
	}
	
}

