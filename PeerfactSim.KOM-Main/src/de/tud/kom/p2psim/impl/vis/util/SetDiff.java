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


package de.tud.kom.p2psim.impl.vis.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Vergleicht zwei Collections und gibt die Differenz als Set zurück.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 07.11.2008
 * 
 * @param <T>
 */
public class SetDiff<T> {

	Collection<T> a;

	Collection<T> b;

	/**
	 * Standard-Konstruktor
	 * 
	 * @param setA
	 * @param setB
	 */
	public SetDiff(Collection<T> setA, Collection<T> setB) {
		this.a = setA;
		this.b = setB;
	}

	/**
	 * Gibt alle Items aus, die sich in B befinden, aber nicht in A Ist b null,
	 * wird eine leere Liste zurückgegeben, ist a null, wird b zurückgegeben
	 * 
	 * @return
	 */
	public Collection<T> getItemsNotInA() {
		Set<T> diff = new HashSet<T>();
		if (b == null)
			return diff;
		if (a == null)
			return b;

		for (T item : b) {
			if (!a.contains(item))
				diff.add(item);
		}
		return diff;
	}

	/**
	 * Gibt alle Items aus, die sich in A befinden, aber nicht in B. Ist a null,
	 * wird eine leere Liste zurückgegeben, ist b null, wird b zurückgegeben
	 * 
	 * @return
	 */
	public Collection<T> getItemsNotInB() {
		Set<T> diff = new HashSet<T>();
		if (a == null)
			return diff;
		if (b == null)
			return a;
		for (T item : a) {
			if (!b.contains(item))
				diff.add(item);
		}
		return diff;
	}

}
