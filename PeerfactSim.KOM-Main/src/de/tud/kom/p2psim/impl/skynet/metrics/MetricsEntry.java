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
import java.util.Set;

import de.tud.kom.p2psim.api.service.aggr.IAggregationMap;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.service.aggr.NoSuchValueException;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;

/**
 * The <code>MetricsEntry</code>-class comprises all aggregated metrics, which
 * are defined by SkyNet, and identifies the node, that measured or aggregated
 * the metrics in this class.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class MetricsEntry implements IAggregationMap<String> {

	private SkyNetNodeInfo nodeInfo;

	private HashMap<String, MetricsAggregate> metrics;

	public MetricsEntry(SkyNetNodeInfo nodeInfo,
			HashMap<String, MetricsAggregate> metrics) {
		this.nodeInfo = nodeInfo;
		this.metrics = metrics;
	}

	/**
	 * This method returns the ID of the node, that measured or aggregated the
	 * metrics, which are currently stored within this class.
	 * 
	 * @return the <code>SkyNetNodeInfo</code>-object of the originating node
	 */
	public SkyNetNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	// public void setNodeInfo(SkyNetNodeInfo nodeInfo) {
	// this.nodeInfo = nodeInfo;
	// }

	/**
	 * This method returns the set of metrics, which are currently comprised
	 * within the <code>MetricsEntry</code>-object.
	 * 
	 * @return the stored metrics of the <code>MetricsEntry</code>-object
	 */
	public HashMap<String, MetricsAggregate> getMetrics() {
		return metrics;
	}

	// public void setMetrics(HashMap<String, MetricsAggregate> metrics) {
	// this.metrics = metrics;
	// }

	/**
	 * This method calculates the size of the stored data and returns its value.
	 * 
	 * @return the size of the comprised data
	 */
	public long getSize() {
		// instead of determining the size of the string that describes the
		// metric, we think of two bytes whose bitfield encode the current
		// metric. This assumptions allows us to address at most 2^16 metrics,
		// which should currently suffice.
		if (metrics != null) {

			return metrics.size()
					* (2 * SkyNetConstants.BYTE_SIZE + SkyNetConstants.METRIC_AGGREGATE);
		} else {
			return 0l;
		}
	}

	@Override
	public int getMapSize() {
		return metrics.size();
	}

	@Override
	public IAggregationResult getAggregationResult(String identifier)
			throws NoSuchValueException {
		IAggregationResult result = metrics.get(identifier);
		if (result == null)
			throw new NoSuchValueException(identifier);
		return result;
	}

	@Override
	public Set<String> getKeySet() {
		return metrics.keySet();
	}
}
