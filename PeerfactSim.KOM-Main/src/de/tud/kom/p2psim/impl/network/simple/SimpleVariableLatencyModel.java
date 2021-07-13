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



package de.tud.kom.p2psim.impl.network.simple;

import org.apache.commons.math.random.RandomGenerator;

import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.impl.simengine.Simulator;

// TODO: Check this class on correctness

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SimpleVariableLatencyModel extends SimpleStaticLatencyModel {

	private long variation = -1;

	RandomGenerator random;

	public SimpleVariableLatencyModel(long base, long variation,
			RandomGenerator generator) {
		super(base);
		this.variation = variation;
		this.random = Simulator.getRandom();
	}

	public SimpleVariableLatencyModel() {
		super(-1l);
	}

	public void setBase(long base) {
		this.setLatency(base);
	}

	public void setVariation(long variation) {
		this.variation = variation;
	}

	public long getLatency(NetLayer sender, NetLayer receiver) {
		double distance = getDistance((SimpleNetLayer) sender,
				(SimpleNetLayer) receiver);
		// double latency = (distance * (staticLatency + (random.nextDouble() -
		// 0.5) * variation)) * Simulator.MILLISECOND_UNIT;
		double latency = (distance * (staticLatency + (random.nextDouble() - 0.5)
				* variation * Simulator.MILLISECOND_UNIT));
		return Math.round(latency);
	}

}
