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


package de.tud.kom.p2psim.impl.util.livemon;

import java.util.LinkedList;
import java.util.Queue;

import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class AvgAccumulatorDouble implements ProgressValue {

	private String name;
	private int elemsLookBack;
	
	Queue<Double> q = new LinkedList<Double>();
	Object lock = new Object();

	public AvgAccumulatorDouble(String name, int elemsLookBack) {
		this.name = name;
		this.elemsLookBack = elemsLookBack;
	}
	
	public void newVal(double val) {
		synchronized(lock) {
			q.add(val);
			if (q.size() > elemsLookBack) q.remove();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		synchronized(lock) {
			if (q.size() <= 0) return "Unknown";
			int accu = 0;
			for (Double elem : q) accu += elem;
			return NumberFormatToolkit.floorToDecimalsString(accu/((double)q.size()), 2);
		}
	}

}
