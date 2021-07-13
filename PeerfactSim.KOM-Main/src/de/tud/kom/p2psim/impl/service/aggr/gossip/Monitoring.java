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


package de.tud.kom.p2psim.impl.service.aggr.gossip;

import java.util.Set;

import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;
import de.tud.kom.p2psim.impl.util.livemon.AvgAccumulatorDouble;
import de.tud.kom.p2psim.impl.util.timeoutcollections.TimeoutSet;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Monitoring {

	static AvgAccumulatorDouble nbrC = new AvgAccumulatorDouble(
			"Gossip: Mean neighbor count", 200);

	static TimeoutSet<Integer> lastSeenNCInitiatorsCounts = new TimeoutSet<Integer>(
			10 * Simulator.SECOND_UNIT);

	static volatile Object lock = new Object();

	public static void register() {
		LiveMonitoring.addProgressValueIfNotThere(nbrC);
		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				return String.valueOf(getAverage(lastSeenNodeCounts));
			}

			@Override
			public String getName() {
				return "Gossip: Average seen node count";
			}
		});

		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				return String.valueOf(getAverage(lastSeenNCInitiatorsCounts));
			}

			@Override
			public String getName() {
				return "Gossip: Average seen NC initiators count";
			}
		});

		LiveMonitoring.addProgressValue(new ProgressValue() {

			@Override
			public String getValue() {
				long totalRPCs = successfulRPCs + unsuccessfulRPCs;
				return "Succ.: "
						+ successfulRPCs
						+ ", Unsucc.: "
						+ unsuccessfulRPCs
						+ ", Succ. Quota: "
						+ (totalRPCs == 0 ? "n/a" : NumberFormatToolkit
								.formatPercentage(successfulRPCs
										/ (double) totalRPCs, 2));
			}

			@Override
			public String getName() {
				return "Gossip: RPC Success";
			}
		});
	}

	public static void onNeighborCountSeen(int neighbors) {
		nbrC.newVal(neighbors);
	}

	public static String getAverage(TimeoutSet<Integer> timeoutset) {
		synchronized (lock) {
			Set<Integer> set = timeoutset.getUnmodifiableSet();
			if (set.size() <= 0)
				return "n/a";
			int all = 0;
			for (int val : set) {
				all += val;
			}
			return NumberFormatToolkit.floorToDecimalsString(
					all / (double) set.size(), 2);
		}
	}

	public static void addNCInitiatorCount(int count) {
		synchronized (lock) {
			lastSeenNCInitiatorsCounts.addNow(count);
		}
	}

	public static void addNodeCount(int count) {
		synchronized (lock) {
			lastSeenNodeCounts.addNow(count);
		}
	}

	static TimeoutSet<Integer> lastSeenNodeCounts = new TimeoutSet<Integer>(
			10 * Simulator.SECOND_UNIT);

	static long successfulRPCs = 0;

	static long unsuccessfulRPCs = 0;

	public static void addSuccessfulRPC() {
		successfulRPCs++;
	}

	public static void addUnsuccessfulRPC() {
		unsuccessfulRPCs++;
	}

}
