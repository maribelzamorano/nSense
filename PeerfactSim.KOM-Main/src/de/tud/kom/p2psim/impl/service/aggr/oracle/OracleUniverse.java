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


package de.tud.kom.p2psim.impl.service.aggr.oracle;

import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.service.aggr.IAggregationService;
import de.tud.kom.p2psim.api.service.aggr.NoSuchValueException;
import de.tud.kom.p2psim.impl.service.aggr.DefaultAggregationResult;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class OracleUniverse {

	Set<IAggregationService> oracleNodes = new HashSet<IAggregationService>();

	public void add(IAggregationService aggregationServiceOracle) {
		oracleNodes.add(aggregationServiceOracle);
	}

	public IAggregationResult getAggregationResult(Object identifier) {

		double minimum = Double.MAX_VALUE;
		double maximum = Double.MIN_VALUE;
		double sum = 0;
		int nodeCount = 0;
		int nodeCountVal = 0;
		long minTime = Simulator.getCurrentTime();
		long maxTime = Simulator.getCurrentTime();
		long avgTime = Simulator.getCurrentTime();

		for (IAggregationService nd : oracleNodes) {
			try {
				double val = nd.getLocalValue(identifier);
				if (val != Double.MAX_VALUE) {
					if (val < minimum)
						minimum = val;
					if (val > maximum)
						maximum = val;
					sum += val;
					nodeCountVal++;
				}
			} catch (NoSuchValueException e) {
				// Value not given, ignoring.
			}
			nodeCount++;
		}

		double avg = sum / nodeCountVal;

		double varAcc = 0d;

		for (IAggregationService nd : oracleNodes) {
			try {
				double val = nd.getLocalValue(identifier);
				if (val != Double.MAX_VALUE) {
					varAcc += (avg - val) * (avg - val);
				}
			} catch (NoSuchValueException e) {
				// Value not given, ignoring.
			}

		}

		double var = nodeCountVal <= 1 ? 0 : varAcc
				/ (nodeCountVal - 1);

		return new DefaultAggregationResult(minimum, maximum, avg, var,
				nodeCount, minTime, maxTime, avgTime);
	}

}
