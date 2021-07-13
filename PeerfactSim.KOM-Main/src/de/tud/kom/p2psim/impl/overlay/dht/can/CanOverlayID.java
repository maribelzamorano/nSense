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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Creates the ID of the peer. Implements OverlayID. The ID is created as a
 * hashvalue of the TransInfo and is stored as a BigInteger.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanOverlayID implements OverlayID<BigInteger> {

	private BigInteger id;

	/**
	 * Creates a new id, from the hash value of the TransInfo
	 * 
	 * @param transInfo
	 */
	public CanOverlayID(TransInfo transInfo) {
		this.id = new Hashvalue().getHash(transInfo);
	}

	/**
	 * Creates a new id from the param
	 * 
	 * @param id
	 *            gives the new id.
	 */
	public CanOverlayID(BigInteger id) {
		this.id = id;
	}

	public byte[] getBytes() {
		byte[] output = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();
			output = bos.toByteArray();

		} catch (IOException e) {
			System.err.println("IOException!");
		}
		return output;
	}

	public BigInteger getUniqueValue() {
		return id;
	}

	public BigInteger getValue() {
		return id;
	}

	public void setID(BigInteger newID) {
		this.id = newID;
	}

	public String toString() {
		return id.toString();
	}

	/**
	 * gives an Integer value of the id.
	 * 
	 * @return
	 */
	public int getIDint() {
		return id.intValue();
	}

	/**
	 * Compares two IDs.
	 */
	public int compareTo(OverlayID arg0) {
		CanOverlayID ID = (CanOverlayID) arg0;
		return this.id.compareTo(ID.getValue());
	}
}
