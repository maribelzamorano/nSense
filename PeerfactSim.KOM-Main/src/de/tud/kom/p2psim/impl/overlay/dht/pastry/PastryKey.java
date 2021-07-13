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


package de.tud.kom.p2psim.impl.overlay.dht.pastry;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.overlay.Transmitable;

/**
 * This class represents the overlay Key used by pastry.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryKey implements OverlayKey<BigInteger>, Transmitable {

	/**
	 * The value of the Key
	 */
	private BigInteger key;

	public PastryKey(BigInteger value) {
		key = value.mod(PastryID.NUM_OF_DISTINCT_IDS);
	}

	public PastryKey(PastryID id) {
		key = id.getUniqueValue();
	}

	@Override
	public int compareTo(OverlayKey otherKey) {
		return key.compareTo(((PastryKey) otherKey).getUniqueValue());
	}

	@Override
	public BigInteger getUniqueValue() {
		return key;
	}

	@Override
	public byte[] getBytes() {
		// FIXME: What to do here?
		return null;
	}

	/**
	 * @return the pastry ID corresponding to this pastry key
	 */
	public PastryID getCorrespondingId() {
		return new PastryID(this);
	}

	@Override
	public int getTransmissionSize() {
		return (int) Math.ceil(PastryConstants.ID_BIT_LENGTH / 8d);
	}
}
