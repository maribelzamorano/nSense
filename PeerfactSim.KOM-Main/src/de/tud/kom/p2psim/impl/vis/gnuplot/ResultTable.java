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


package de.tud.kom.p2psim.impl.vis.gnuplot;

import java.util.Collection;
import java.util.HashMap;

/**
 * Ergebnistabelle: Beliebige Objekte als Spalten-Überschrift.
 * 
 * Zeit, Objekt1, Objekt2 ..... Objekt n t1,
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ResultTable {

	long[] t;

	HashMap<Object, String[]> metric_values = new HashMap<Object, String[]>();

	int length;

	public ResultTable(int length, final Collection<Object> obj) {
		this.length = length;
		t = new long[length];
		for (Object o : obj) {
			metric_values.put(o, new String[length]);
		}
	}

	/**
	 * Gibt die Objekte zurück, die die Spaltenüberschrift definieren.
	 * 
	 * @return
	 */
	public Collection<Object> getObjects() {
		return metric_values.keySet();
	}

	public void setTimeAt(int pos, long time) {
		t[pos] = time;
	}

	public long getTimeAt(int pos) {
		return t[pos];
	}

	public void setValueForAt(Object o, int pos, String value) {
		if (metric_values.get(o) == null)
			throw new IllegalArgumentException(o
					+ " ist nicht enthalten in der Tabelle");
		metric_values.get(o)[pos] = value;
	}

	public String getValueForAt(Object o, int pos) {
		return metric_values.get(o)[pos];
	}

}
