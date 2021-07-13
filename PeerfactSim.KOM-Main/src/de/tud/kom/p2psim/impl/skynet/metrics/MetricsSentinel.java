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


package de.tud.kom.p2psim.impl.skynet.metrics;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.analyzing.writers.Coor2RootEntry;
import de.tud.kom.p2psim.impl.skynet.analyzing.writers.Coor2RootWriter;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricsSentinel {

	private static Logger log = SimLogger.getLogger(MetricsSentinel.class);

	private SkyNetNodeInterface skyNetNode;

	public MetricsSentinel(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	public void interpolateKnowledge(MetricsEntry entryToSend) {
		Coor2RootWriter ctrWriter = Coor2RootWriter.getInstance();
		if (skyNetNode.getMetricsInterpretation().getActualSystemStatistics() != null) {
			long timestamp = skyNetNode.getMetricsInterpretation()
					.getStatisticsTimestamp();
			if (ctrWriter.checkForNewData(skyNetNode.getSkyNetNodeInfo(),
					timestamp)) {
				log.info(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ " which is responsible for the Coordinator-Key "
						+ skyNetNode.getSkyNetNodeInfo().getCoordinatorKey()
								.getPlainSkyNetID() + " at level "
						+ skyNetNode.getSkyNetNodeInfo().getLevel()
						+ ", is going to interpolate its"
						+ " metrics with the ones of the root,"
						+ " which were created at "
						+ Simulator.getFormattedTime(timestamp));
				HashMap<String, MetricsAggregate> rootMetrics = skyNetNode
						.getMetricsInterpretation().getActualSystemStatistics()
						.getMetrics();
				HashMap<String, MetricsAggregate> coordinatorMetrics = entryToSend
						.getMetrics();
				HashMap<String, Coor2RootEntry> dataMap = new HashMap<String, Coor2RootEntry>();
				boolean first = true;
				Iterator<String> nameIter = rootMetrics.keySet().iterator();
				String name = null;
				MetricsAggregate rootAg = null;
				MetricsAggregate coordinatorAg = null;
				double factor = -1;
				while (nameIter.hasNext()) {
					name = nameIter.next();
					rootAg = rootMetrics.get(name);
					coordinatorAg = coordinatorMetrics.get(name);
					if (first) {
						first = false;
						factor = (coordinatorAg.getNodeCount() / (double) rootAg
								.getNodeCount());
						dataMap.put("NodeCount", new Coor2RootEntry(
								"NodeCount", coordinatorAg
										.getNodeCount(), coordinatorAg
										.getNodeCount()
										/ factor, rootAg.getNodeCount(),
								factor));
					}
					double interpolatedSum = coordinatorAg.getSumOfAggregates()
							/ factor;
					double interpolatedCount = coordinatorAg
							.getNodeCount()
							/ factor;

					dataMap.put(name, new Coor2RootEntry(name, coordinatorAg
							.getAverage(), interpolatedSum / interpolatedCount,
							rootAg.getAverage(), -1));
				}
				ctrWriter.writeData(skyNetNode.getSkyNetNodeInfo(), dataMap,
						timestamp);
			}
		}
	}
}
