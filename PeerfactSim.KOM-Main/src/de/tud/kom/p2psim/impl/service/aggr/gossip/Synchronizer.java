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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Coordinates all the superior timing at every gossiping aggregation
 * node.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Synchronizer {
	
	static final Logger log = SimLogger.getLogger(Synchronizer.class);
	
	long epoch = 0;
	int cycles = 0;
	private GossipingAggregationService parent;
	
	public Synchronizer(GossipingAggregationService parent) {
		this.parent = parent;
	}
	
	public void reset() {
		parent.resetRaw();
		cycles = 0;
		epoch++;
		log.debug(parent.getIdStr() + ": Resetting for epoch " + epoch + " by initiating.");
	}
	
	/**
	 * Returns whether the given epoch is not outdated.
	 * @param epoch
	 * @return
	 */
	public boolean onEpochSeen(long epoch) {
		if (epoch > this.epoch) {
			cycles = 0;
			this.epoch = epoch;
			parent.resetRaw();
			//log.debug(parent.getIdStr() + ": Resetting for epoch " + epoch + " while seen foreign sync message.");
		}
		return epoch >= this.epoch;
	}

	public void onCycleFinished() {
		cycles++;
		if (cycles >= parent.getConf().getRestartCount()) {
			reset();
		}
		//log.debug(parent.getIdStr() + ": Increasing cycles to " + cycles);
	}

	public long getEpoch() {
		return epoch;
	}
	
	public int getCycle() {
		return cycles;
	}
	
	public long getTimeToNextEpoch() {
		return Math.round((parent.getConf().getRestartCount()-cycles+0.5d) * parent.getConf().getUpdatePeriod());
	}
	
}
