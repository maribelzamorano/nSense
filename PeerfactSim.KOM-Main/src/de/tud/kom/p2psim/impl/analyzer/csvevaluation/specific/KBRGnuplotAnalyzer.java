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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.AverageResponseTime;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops.QueryTimeoutListener;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDummyApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.QueryForDocumentMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.QueryForDocumentOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.RequestDocumentOperation;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class KBRGnuplotAnalyzer extends DefaultGnuplotAnalyzer implements
		OperationAnalyzer, NetAnalyzer, QueryTimeoutListener,
		KBROverlayAnalyzer {

	AverageResponseTime avgRespTime = new AverageResponseTime();

	QuerySuccessAndNHops qMetrics = new QuerySuccessAndNHops();

	Map<Query, Long> openQueries = new HashMap<Query, Long>();

	int queriesFailed = 0;

	int queriesSucceeded = 0;

	int queriesInitiated = 0;

	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		addMetric(avgRespTime);
		addMetric(qMetrics.getQuerySuccess());
		addMetric(qMetrics.getQueryNHops());
		qMetrics.addListener(this);
	}

	protected QuerySuccessAndNHops getQMetrics() {
		return qMetrics;
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		this.checkTimeProgress();

		if (op instanceof QueryForDocumentOperation) {
			// System.out.println("Operation QueryDoc initiated: " +
			// op.getOperationID()); //DEBUG
			OverlayID reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode().getLocalOverlayContact().getOverlayID();
			OverlayKey reqKey = ((QueryForDocumentOperation) op)
					.getKeyQueriedFor();
			Query q = new Query(reqNode, reqKey);
			long currentTime = Simulator.getCurrentTime();
			openQueries.put(q, currentTime);
			qMetrics.queryStarted(q, currentTime);
			queriesInitiated++;
			// System.out.println("Query initiated: " + q); //DEBUG

		} else if (op instanceof RequestDocumentOperation) {

			// System.out.println("Operation ReqDoc initiated: " +
			// op.getOperationID()); //DEBUG

			OverlayID reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode().getLocalOverlayContact().getOverlayID();
			OverlayKey reqKey = ((RequestDocumentOperation) op)
					.getKeyOfDocument();

			Query query2lookup = new Query(reqNode, reqKey);

			if (openQueries.containsKey(query2lookup)) {
				long startTime = openQueries.get(query2lookup);
				long endTime = Simulator.getCurrentTime();
				avgRespTime.gotResponse(endTime - startTime);
				openQueries.remove(query2lookup);
				qMetrics.querySucceeded(query2lookup, 0);
				queriesSucceeded++;
			}
		}
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		super.netMsgReceive(msg, id);

		Message olMsg = msg.getPayload().getPayload();

		/*
		 * if (olMsg instanceof LookupRequest) { LookupRequest req =
		 * (LookupRequest)olMsg; OverlayKey key = req.getKey(); OverlayID
		 * initiator = req.getStarter().getOverlayID();
		 * 
		 * Query q = new Query(initiator, key);
		 * 
		 * if (!this.getQMetrics().addHopToQuery(q)) {
		 * 
		 * if (Simulator.getCurrentTime() > 12000000000l)
		 * System.out.println("Query was not found for " + q);
		 * //this.getQMetrics().printDbg(); } else
		 * System.out.println("Query was found for: " + q); }
		 */

		if (olMsg instanceof ForwardMsg) {
			ForwardMsg req = (ForwardMsg) olMsg;
			OverlayKey key = req.getKey();

			if (req.getPayload() instanceof QueryForDocumentMessage) {
				QueryForDocumentMessage qmsg = (QueryForDocumentMessage) req
						.getPayload();
				OverlayID initiator = qmsg.getSenderContact().getOverlayID();

				Query q = new Query(initiator, key);

				if (!this.getQMetrics().addHopToQuery(q))
					System.out
							.println("Query was not initiated, but handled through messages: "
									+ q);

			} /*
			 * else if (req.getPayload() instanceof AnnounceNewDocumentMessage
			 * && INCLUDE_ANNOUNCEMENTS) { AnnounceNewDocumentMessage amsg =
			 * (AnnounceNewDocumentMessage)req.getPayload(); OverlayID initiator
			 * = amsg.getSenderContact().getOverlayID();
			 * 
			 * Query q = new Query(initiator, key);
			 * 
			 * if (!this.getQMetrics().addHopToQuery(q)) {
			 * 
			 * System.out.println("Announce Query was not found for " + q);
			 * //this.getQMetrics().printDbg(); } else
			 * System.out.println("Announce Query was found for: " + q);
			 */
		}
	}

	public class Query {

		public Query(OverlayID queryingNode, OverlayKey keyQueried) {
			super();
			this.queryingNode = queryingNode;
			this.keyQueried = keyQueried;
		}

		OverlayID queryingNode;

		OverlayKey keyQueried;

		public int hashCode() {
			return queryingNode.hashCode() + keyQueried.hashCode() * 95651;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Query))
				return false;
			Query other = (Query) o;
			return queryingNode.equals(other.queryingNode)
					&& keyQueried.equals(other.keyQueried);
		}

		public String toString() {
			String qnodeStr = String.valueOf(queryingNode);
			String keyQueriedStr = String.valueOf(keyQueried);
			return "Q from "
					+ ((qnodeStr.length() > 10) ? qnodeStr.substring(0, 10)
							+ "..." : qnodeStr)
					+ " for "
					+ ((keyQueriedStr.length() > 10) ? qnodeStr
							.substring(0, 10)
							+ "..." : keyQueriedStr);
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {
		// if (op instanceof QueryForDocumentOperation)
		// System.out.println("Operation finished: " + op.getOperationID());
	}

	@Override
	public void queryTimeouted(Object queryIdentifier) {
		openQueries.remove(queryIdentifier);
		this.queriesFailed++;
	}

	public void stop(Writer w) {
		super.stop(w);
		try {
			w.write("=========KBR Query Summary Report=========");
			;
			w.write("Total Queries failed: " + this.queriesFailed
					+ ", succeeded: " + this.queriesSucceeded + ", initiated: "
					+ this.queriesInitiated);
			w.write("==========================================");
		} catch (IOException e) { // No output
		}
		System.out.println("=========KBR Query Summary Report=========");
		System.out.println("Total Queries failed: " + this.queriesFailed
				+ ", succeeded: " + this.queriesSucceeded + ", initiated: "
				+ this.queriesInitiated);
		System.out.println("==========================================");
	}

	@Override
	public void messageDelivered(OverlayContact contact, Message msg, int hops) {
		qMetrics.querySucceeded(msg, 0);
	}

	@Override
	public void messageForwarded(OverlayContact sender,
			OverlayContact receiver, Message msg, int hops) {
		qMetrics.addHopToQuery(msg);
	}

	@Override
	public void queryFailed(OverlayContact failedHop, Message appMsg) {
		// Timeout...
	}

	@Override
	public void queryStarted(OverlayContact contact, Message appMsg) {
		qMetrics.queryStarted(appMsg, Simulator.getCurrentTime());
	}

}
