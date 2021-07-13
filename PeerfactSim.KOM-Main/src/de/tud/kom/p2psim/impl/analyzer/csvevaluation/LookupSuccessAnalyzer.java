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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDummyApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.QueryForDocumentOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.RequestDocumentOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class LookupSuccessAnalyzer implements OperationAnalyzer {

	String ops = "";

	Map<Query, Long> openQueries = new HashMap<Query, Long>();

	int succeededLookups = 0;

	AverageAccumulator avgLookupTime = new AverageAccumulator();

	@Override
	public void operationFinished(Operation<?> op) {
		// TODO Auto-generated method stub

	}

	@Override
	public void operationInitiated(Operation<?> op) {

		if (op instanceof QueryForDocumentOperation) {
			KBR reqNode = ((KBRDummyApplication) op.getComponent()).getNode();
			OverlayKey reqKey = ((QueryForDocumentOperation) op)
					.getKeyQueriedFor();

			openQueries.put(new Query(reqNode, reqKey), Simulator
					.getCurrentTime());
		} else if (op instanceof RequestDocumentOperation) {
			KBR reqNode = ((KBRDummyApplication) op.getComponent()).getNode();
			OverlayKey reqKey = ((RequestDocumentOperation) op)
					.getKeyOfDocument();

			Query query2lookup = new Query(reqNode, reqKey);

			if (openQueries.containsKey(query2lookup)) {
				long startTime = openQueries.get(query2lookup);
				long endTime = Simulator.getCurrentTime();
				avgLookupTime.accumulate(endTime - startTime);
				succeededLookups++;
				openQueries.remove(query2lookup);
			}
		}

		if (op.getComponent() instanceof KBRDummyApplication) {

			NetID host = ((KBRDummyApplication) op.getComponent()).getHost()
					.getNetLayer().getNetID();

			ops += Simulator.getCurrentTime() + "|" + op + "|" + host + "\n";

		}
	}

	@Override
	public void start() {
		// Nothing to do
	}

	@Override
	public void stop(Writer output) {
		try {
			output.write("========KBR Lookup Success=============\n");
			output.write("Closed Lookups:			" + succeededLookups + "\n");
			output.write("Open Lookups:			" + openQueries.size() + "\n");
			output.write("Succeeded Quota:		"
					+ NumberFormatToolkit
							.formatPercentage(getSuccessQuota(), 1) + "\n");
			output.write("Average Lookup Time: 		"
					+ NumberFormatToolkit.formatSecondsFromSimTime(
							avgLookupTime.returnAverage(), 3) + "\n");
			// output.write(ops);
			output.write("=======================================\n");
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getSuccessQuota() {
		int numOpenQueries = openQueries.size();

		return (double) succeededLookups / (numOpenQueries + succeededLookups);
	}

	public class Query {

		public Query(KBR queryingNode, OverlayKey keyQueried) {
			super();
			this.queryingNode = queryingNode;
			this.keyQueried = keyQueried;
		}

		KBR queryingNode;

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
	}

}
