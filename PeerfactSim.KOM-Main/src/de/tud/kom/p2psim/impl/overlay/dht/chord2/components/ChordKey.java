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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * ChordKey is used to identify messages and data objects while ChordID identify
 * ChordNode. ChordKey and ChordID has the same value space.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordKey implements OverlayKey<BigInteger> {

	private final BigInteger key;

	public ChordKey(BigInteger _key) {
		this.key = _key;
	}

	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public BigInteger getUniqueValue() {
		return key;
	}

	@Override
	public int compareTo(OverlayKey o) {
		ChordKey compareKey = (ChordKey) o;
		return this.key.compareTo(compareKey.getKey());
	}

	public BigInteger getKey() {
		return key;
	}

	public ChordID getCorrespondingID() {
		return new ChordID(key);
	}

	@Override
	public String toString() {
		String idString = key.toString(16);
		while (idString.length() < 160 / 4) {
			idString = "0" + idString;
		}
		// return "[id = " + idString +" ]";
		return "[id = " + idString.substring(0, 5) + ".."
				+ idString.substring(idString.length() - 5) + "]";
	}

	@Override
	public final int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ChordKey other = (ChordKey) obj;
		if (key == null) {
			if (other.getKey() != null)
				return false;
		} else if (!key.equals(other.getKey()))
			return false;
		return true;
	}

	@Override
	public int getTransmissionSize() {
		return getCorrespondingID().getBytes().length;
	}
}
