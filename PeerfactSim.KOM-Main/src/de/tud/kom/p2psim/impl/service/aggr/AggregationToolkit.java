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

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.toolkits.TimeToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class AggregationToolkit {

	static final String separator = "\t";
	
	static final TimeToolkit tk;
	
	static {
		tk = new TimeToolkit(Simulator.MILLISECOND_UNIT);
		tk.setSeparateTimeUnitsWhitespace(false);
	}
	
	public static String printResultCSV(Host host, long time, long duration, IAggregationResult res) {
		return host.getNetLayer().getNetID() + separator
			+ tk.timeStringFromLong(time) + separator
			+ tk.timeStringFromLong(duration) + separator
			+ "succ" + separator
			+ res.getAverage() + separator
			+ res.getMaximum() + separator
			+ res.getMinimum() + separator
			+ res.getVariance() + separator
			+ res.getNodeCount();
	}
	
	public static String printDescLineCSV() {
		return  "#host" + separator
			+ "time" + separator
			+ "duration" + separator
			+ "status" + separator
			+ "mean" + separator
			+ "max" + separator
			+ "min" + separator
			+ "variance" + separator
			+ "count";
	}

	public static String printResultFailedCSV(Host host, long time, long duration) {
		return host.getNetLayer().getNetID() + separator
			+ tk.timeStringFromLong(time) + separator
			+ tk.timeStringFromLong(duration) + separator
			+ "fail" + separator
			+ "NaN" + separator
			+ "NaN" + separator
			+ "NaN" + separator
			+ "NaN" + separator
			+ "NaN";
	}
	
}
