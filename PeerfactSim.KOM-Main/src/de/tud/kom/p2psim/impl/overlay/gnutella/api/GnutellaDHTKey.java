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

package de.tud.kom.p2psim.impl.overlay.gnutella.api;

import de.tud.kom.p2psim.api.overlay.dht.DHTKey;

/**
 * As Gnutella uses Integers as "rank", this class is needed to support
 * DHTKey-Generation and operations like equals on this key
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaDHTKey<T> implements DHTKey {

	private T identifier;

	/**
	 * Create a key for given Rank
	 * 
	 * @param rank
	 */
	public GnutellaDHTKey(T identifier) {
		this.identifier = identifier;
	}

	public T getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GnutellaDHTKey) {
			GnutellaDHTKey o = (GnutellaDHTKey) obj;
			return identifier.equals(o.getIdentifier());
		}
		return false;
	}

	@Override
	public int getTransmissionSize() {
		return 4;
	}

}
