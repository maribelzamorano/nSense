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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A HashMap that can contain multiple values per key.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.01.2009
 * 
 */
public class MultiMap<Key, Value> {

	protected Map<Key, Set<Value>> map = new HashMap<Key, Set<Value>>();

	protected Map<Value, Key> reverseMap = new HashMap<Value, Key>();

	/**
	 * Gets all values that are associated to the given key. If no value is
	 * given, an empty set is returned.
	 * 
	 * @param k
	 * @return
	 */
	public Set<Value> get(Key k) {
		Set<Value> v = map.get(k);
		if (v == null) {
			v = new MultiMapSet(k);
			map.put(k, v);
		}
		return v;
	}

	public Set<Value> deleteAllForKey(Key k) {
		Set<Value> v = map.remove(k);
		if (v == null) {
			v = new MultiMapSet(k);
			map.put(k, v);
		}
		return v;
	}

	public boolean contains(Key k) {
		return map.containsKey(k);
	}

	public class MultiMapSet extends HashSet<Value> {

		private Key keyFrom;

		public MultiMapSet(Key keyFrom) {
			this.keyFrom = keyFrom;
		}

		public void setMappingActive() {
			map.put(keyFrom, this);
		}

		@Override
		public boolean add(Value e) {
			setMappingActive();
			return super.add(e);
		}

		@Override
		public boolean addAll(Collection<? extends Value> c) {
			setMappingActive();
			return super.addAll(c);
		}

	}

}
