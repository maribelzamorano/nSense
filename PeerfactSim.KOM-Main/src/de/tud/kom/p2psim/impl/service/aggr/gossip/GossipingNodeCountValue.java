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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.Tuple;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.toolkits.CollectionHelpers;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GossipingNodeCountValue {

	static final Logger log = SimLogger
			.getLogger(GossipingNodeCountValue.class);

	private int lastNC;

	/**
	 * whether or not to use the node count debugger, outputting additional
	 * information useful for debugging.
	 */
	static final boolean NC_DEBUG = true;

	Map<Integer, Double> ncVals = new HashMap<Integer, Double>();

	private GossipingAggregationService parent;

	public String toString() {
		return "(lastNC=" + lastNC + ")";
	}

	public GossipingNodeCountValue(GossipingAggregationService parent) {
		this.parent = parent;
		restart();
		log.debug("Created " + this + " from local value ");
		if (NC_DEBUG)
			NCDebugger.register(this);
	}

	private GossipingNodeCountValue(UpdateInfo info,
			GossipingAggregationService parent) {
		this.parent = parent;
		log.debug("Created " + this + " from foreign info " + info);
		if (NC_DEBUG)
			NCDebugger.register(this);
	}

	public static GossipingNodeCountValue fromInfo(UpdateInfo info,
			GossipingAggregationService parent) {
		return new GossipingNodeCountValue(info, parent);
	}

	public void restart() {
		lastNC = getNC();
		if (NC_DEBUG)
			NCDebugger.onNCCalculated(this.getEpoch(), lastNC);
		ncVals.clear();
	}

	public UpdateInfoNodeCount extractInfo() {
		return new UpdateInfoNodeCount(Tuple.tupleListFromMap(ncVals));
	}

	public void merge(UpdateInfoNodeCount info2merge, String dbgNote) {

		Set<Integer> idsOfMergeInfo = new HashSet<Integer>();
		for (Tuple<Integer, Double> tpl : info2merge.getNCList()) {
			int id = tpl.getA();
			Double oldVal = ncVals.get(id);
			if (oldVal == null)
				oldVal = 0d;
			double newVal = (tpl.getB() + oldVal) / 2d;
			// System.out.println("Merging " + val + " with " + tpl.getB() +
			// " at " + parent.getUID() + ", key " + id);
			ncVals.put(id, newVal);
			if (NC_DEBUG)
				NCDebugger.valueUpdate(this, id, oldVal, newVal, tpl.getB(),
						dbgNote, getEpoch());
			idsOfMergeInfo.add(id);
		}
		for (Entry<Integer, Double> valE : ncVals.entrySet()) {
			int key = valE.getKey();
			if (!idsOfMergeInfo.contains(key)) {
				double val = valE.getValue();
				double newVal = val / 2d;
				ncVals.put(key, newVal);
				if (NC_DEBUG)
					NCDebugger.valueUpdate(this, key, val, newVal, null,
							dbgNote, getEpoch());
			}
		}
		if (parent.getSync().getCycle() == parent.getConf()
				.getNodeCountStartCycle())
			startNCProcedure();
	}

	public void mergeOnOutdatedNeighbor() {
		for (Entry<Integer, Double> valE : ncVals.entrySet()) {
			int key = valE.getKey();
			double val = valE.getValue();
			double newVal = val / 2d;
			ncVals.put(key, newVal);
			if (NC_DEBUG)
				NCDebugger.valueUpdate(this, key, val, newVal, null,
						"outdated", getEpoch());
		}
	}

	public int getLastNC() {
		return lastNC;
	}

	public void createNCInstance() {
		// log.debug("Creating NC instance with id " + parent.getUID() +
		// " at node with NetID " + parent.getHost().getNetLayer().getNetID());
		ncVals.put(parent.getUID(), 1d);
		if (NC_DEBUG)
			NCDebugger.valueUpdate(this, parent.getUID(), null, 1d, null,
					"init", getEpoch());
	}

	private void startNCProcedure() {
		double prob2NCLeader = parent.getConf().getConcurrentNCLeaders()
				/ (double) (lastNC <= 0 ? parent.getConf()
						.getInitiallyAssumedNodeCount() : lastNC);
		// double prob2NCLeader =
		// parent.getConf().getConcurrentNCLeaders()/(double)INIT_NC;
		// System.out.println("PROB:" + prob2NCLeader);
		if (NC_DEBUG)
			NCDebugger.newEpoch(parent.getSync().getEpoch());
		if (Simulator.getRandom().nextDouble() < prob2NCLeader)
			createNCInstance();
	}

	public int getNC() {
		// int result = getAverageNC();
		int result = getMedianNC();

		Monitoring.addNCInitiatorCount(ncVals.size());
		Monitoring.addNodeCount(result);
		return result;
	}

	private int getMedianNC() {
		int result = (int) Math.min(Math.round(1d / CollectionHelpers
				.getQuantile(ncVals.values(), 0.5d)), Integer.MAX_VALUE);
		if (NC_DEBUG) {
			List<Double> l = new ArrayList<Double>(ncVals.values());
			for (int i = 0; i < l.size(); i++) {
				l.set(i, Math.rint(1d / l.get(i)));
			}
			Collections.sort(l);
			log.debug("Returning median of list values " + l);
		}
		return result;
	}

	long getEpoch() {
		return parent.getSync().getEpoch();
	}

	Map<Integer, Double> getNCVals() {
		return ncVals;
	}
}
