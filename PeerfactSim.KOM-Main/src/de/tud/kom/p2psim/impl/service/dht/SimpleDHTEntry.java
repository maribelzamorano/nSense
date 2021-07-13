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

package de.tud.kom.p2psim.impl.service.dht;

import de.tud.kom.p2psim.api.overlay.dht.DHTEntry;
import de.tud.kom.p2psim.api.overlay.dht.DHTKey;
import de.tud.kom.p2psim.api.overlay.dht.DHTValue;

/**
 * An abstract DHT Entry, consisting of a Key/Value Pair
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimpleDHTEntry implements DHTEntry {

	private DHTKey key;

	private DHTValue value;

	public SimpleDHTEntry(DHTKey key, DHTValue value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public DHTKey getKey() {
		return key;
	}

	@Override
	public DHTValue getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DHTEntry) {
			DHTEntry entry = (DHTEntry) obj;
			return entry.getKey().equals(getKey());
		}
		return false;
	}

	@Override
	public int getTransmissionSize() {
		return 0;
	}

}
