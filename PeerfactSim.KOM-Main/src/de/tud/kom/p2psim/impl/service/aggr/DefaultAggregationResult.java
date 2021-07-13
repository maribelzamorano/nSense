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


package de.tud.kom.p2psim.impl.service.aggr;

import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class DefaultAggregationResult implements IAggregationResult {

	double minimum;

	double maximum;

	double generalizedMean;

	double variance;

	int nodeCount;

	long minTime;

	long maxTime;

	long avgTime;

	public DefaultAggregationResult(double minimum, double maximum,
			double generalizedMean, double variance, int nodeCount,
			long minTime, long maxTime, long avgTime) {
		super();
		this.minimum = minimum;
		this.maximum = maximum;
		this.generalizedMean = generalizedMean;
		this.variance = variance;
		this.nodeCount = nodeCount;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.avgTime = avgTime;
	}

	@Override
	public double getMinimum() {
		return minimum;
	}

	@Override
	public double getMaximum() {
		return maximum;
	}

	@Override
	public double getAverage() {
		return generalizedMean;
	}

	@Override
	public double getVariance() {
		return variance;
	}

	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	@Override
	public long getMinTime() {
		return minTime;
	}

	@Override
	public long getMaxTime() {
		return maxTime;
	}

	@Override
	public long getAvgTime() {
		return avgTime;
	}

}
