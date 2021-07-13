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


package de.tud.kom.p2psim.impl.overlay.dht.can;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * Generates the hash values for the data files. So it is used for store and
 * lookup. Implements OverlayKey
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version Februar 2010
 * 
 */
public class DataID implements OverlayKey<BigInteger> {

	private BigInteger id;

	/**
	 * Generates a hash from the String.
	 * 
	 * @param data
	 *            used to generate the hash.
	 */
	public DataID(String data) {
		this.id = new Hashvalue().getHash(data);

	}

	/**
	 * sets a hash directly
	 * 
	 * @param data
	 *            long hash
	 */
	public DataID(long data) {
		this.id = new BigInteger(String.valueOf(data));

	}

	/**
	 * sets a hash directly
	 * 
	 * @param data
	 *            BigIntger hash
	 */
	public DataID(BigInteger data) {
		this.id = data;

	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId() {
		return id;
	}

	public String toString() {
		return id.toString();
	}

	/**
	 * Gives the x value of the hash. Therefore it takes the modular value of
	 * the first half of the hash and CanConfig.CanSize
	 * 
	 * @return
	 */
	public BigInteger getXValue() {
		BigInteger divider = new BigInteger("10").pow(
				id.toString().length() / 2).divide(
				new BigInteger(String.valueOf(CanConfig.CanSize)));
		if (id.mod(new BigInteger("2")).longValue() == 0)
			return (new BigInteger(id.toString()
					.subSequence(0, id.toString().length() / 2).toString()))
					.divide(divider);

		return (new BigInteger(id.toString()
				.subSequence(0, (id.toString().length() / 2) - 1).toString()))
				.mod(new BigInteger(String.valueOf(CanConfig.CanSize)));
	}

	/**
	 * Gives the x value of the hash. Therefore it takes the modular value of
	 * the first half of the hash and CanConfig.CanSize
	 * 
	 * @return
	 */
	public BigInteger getYValue() {
		BigInteger divider = new BigInteger("10").pow(
				id.toString().length() - id.toString().length() / 2).divide(
				new BigInteger(String.valueOf(CanConfig.CanSize)));

		return (new BigInteger(
				id.toString()
						.subSequence(id.toString().length() / 2,
								id.toString().length()).toString()))
				.mod(new BigInteger(String.valueOf(CanConfig.CanSize)));
	}

	public BigInteger[] getValue() {
		BigInteger[] output = { getXValue(), getYValue() };
		return output;
	}

	public int[] getIntValue() {
		int[] output = { getXValue().intValue(), getYValue().intValue() };
		return output;
	}

	/**
	 * Checks if the x value of the hash is in the same region as the x corner
	 * of the area
	 * 
	 * @param area
	 * @return
	 */
	public boolean sameXValue(CanArea area) {
		if (getXValue().intValue() >= area.getArea()[0]
				&& getXValue().intValue() <= area.getArea()[1])
			return true;
		return false;
	}

	/**
	 * Checks if the y value of the hash is in the same region as the y corner
	 * of the area
	 * 
	 * @param area
	 * @return
	 */
	public boolean sameYValue(CanArea area) {
		if (getYValue().intValue() >= area.getArea()[2]
				&& getYValue().intValue() <= area.getArea()[3])
			return true;
		return false;
	}

	/**
	 * Checks if the x value of the hash should be in this area
	 * 
	 * @param area
	 * @return true if it should be in the area
	 */
	public boolean includedInArea(CanArea area) {
		if (sameXValue(area) && sameYValue(area))
			return true;

		return false;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getUniqueValue() {
		return id;
	}

	@Override
	public int compareTo(OverlayKey arg0) {
		return id.compareTo(((DataID) arg0).getUniqueValue());
	}

	@Override
	public int getTransmissionSize() {
		return 16; // FIXME 16?
	}

}
