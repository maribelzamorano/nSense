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


package de.tud.kom.p2psim.impl.network.modular.st.ploss;

import org.apache.commons.math.random.RandomGenerator;

import de.tud.kom.p2psim.impl.network.modular.ModularNetLayer;
import de.tud.kom.p2psim.impl.network.modular.ModularNetMessage;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB;
import de.tud.kom.p2psim.impl.network.modular.st.PLossStrategy;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Applies a static packet loss of a given ratio.
 * Parameters: ratio (double from 0 to 1)
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class StaticPacketLoss implements PLossStrategy {

	private double ratio = 0.005d;	//default value 0.5%
	
	RandomGenerator rand = Simulator.getRandom();

	@Override
	public boolean shallDrop(ModularNetMessage msg, ModularNetLayer nlSender,
			ModularNetLayer nlReceiver, NetMeasurementDB db) {
		// If the message consists of multiple fragments, the loss probability 
		// will be the probability that all fragments have arrived, and
		// every fragment has the probability of "ratio" to be dropped
		return rand.nextDouble() < 1d - Math.pow(1d - ratio, msg.getNoOfFragments());
	}

	/**
	 * The average ratio of packets lost (of all packets sent).
	 * Value between 0 and 1, not in percent!
	 * 
	 * To be used by the XML configurator.
	 * 
	 * @param ratio
	 */
	public void setRatio(double ratio) {
		if (ratio < 0 || ratio > 1) throw new IllegalArgumentException("The packet loss ratio must be a value between 0 and 1.");
		this.ratio = ratio;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("ratio", ratio);
	}
	
}
