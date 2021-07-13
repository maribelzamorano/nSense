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
import java.util.List;

import de.tud.kom.p2psim.impl.util.Tuple;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class UpdateInfoNodeCount {

	List<Tuple<Integer, Double>> ncList;

	/**
	 * @return the current list of UIDs of nodes (int) that started a node count
	 *         along with the current estimation (double) of this attempt.
	 */
	public List<Tuple<Integer, Double>> getNCList() {
		return ncList;
	}

	public UpdateInfoNodeCount(List<Tuple<Integer, Double>> ncList) {
		this.ncList = ncList;
	}
	
	/**
	 * @return the size in bytes of this UpdateInfoNodeCount
	 */
	public long getSize() {
		return ncList.size() * 12;
	}
	
	public int getMedianNC() {
		List<Double> vals = new ArrayList<Double>(ncList.size());
		for (Tuple<Integer, Double> tuple : ncList) {
			vals.add(tuple.getB());
		}
		Collections.sort(vals);

		int size = vals.size();
		if (size == 0)
			return -1;
		return (int) Math.round(1d / vals.get((int) Math.floor(size / 2d)));
	}
}
