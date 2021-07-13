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

import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class KBRGnuplotAnalyzer2 extends DefaultGnuplotAnalyzer implements
		KBROverlayAnalyzer {

	// AverageResponseTime avgRespTime = new AverageResponseTime();
	QuerySuccessAndNHops qMetrics = new QuerySuccessAndNHops();

	Map<Message, Long> openQueries = new HashMap<Message, Long>();

	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		// addMetric(avgRespTime);
		addMetric(qMetrics.getAvgRespTime());
		addMetric(qMetrics.getQuerySuccess());
		addMetric(qMetrics.getQueryNHops());
	}

	@Override
	public void messageDelivered(OverlayContact contact, Message msg, int hops) {
		System.out.println("Message delivered " + msg.getPayload());
		qMetrics.querySucceeded(msg.getPayload(), 0);
		msgsDelivered++;
	}

	@Override
	public void messageForwarded(OverlayContact sender,
			OverlayContact receiver, Message msg, int hops) {
		System.out.println("Message forwarded " + msg.getPayload());
		qMetrics.addHopToQuery(msg.getPayload());
		msgsForwarded++;
	}

	@Override
	public void queryFailed(OverlayContact failedHop, Message appMsg) {
		queriesFailed++;
	}

	@Override
	public void queryStarted(OverlayContact contact, Message appMsg) {
		System.out.println("Query started" + appMsg);
		qMetrics.queryStarted(appMsg, Simulator.getCurrentTime());
		queriesStarted++;
	}

	int queriesStarted = 0;

	int msgsForwarded = 0;

	int msgsDelivered = 0;

	int queriesFailed = 0;

	public void stop(Writer w) {
		super.stop(w);
		System.out.println("Q's started: " + queriesStarted);
		System.out.println("Q's failed: " + queriesFailed);
		System.out.println("Msgs forwarded " + msgsForwarded);
		System.out.println("Msgs delivered " + msgsDelivered);
	}

}
