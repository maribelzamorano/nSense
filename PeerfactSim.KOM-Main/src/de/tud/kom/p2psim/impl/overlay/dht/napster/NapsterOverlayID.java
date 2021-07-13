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


package de.tud.kom.p2psim.impl.overlay.dht.napster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterOverlayID implements OverlayID<BigInteger> {

	private BigInteger id;

	public NapsterOverlayID(NetID id) {
		this.id = getID(id.toString());
	}

	public NapsterOverlayID(BigInteger id) {
		this.id = id;
	}

	public BigInteger getID() {
		return id;
	}

	public byte[] getBytes() {
		byte[] buf = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();
			buf = bos.toByteArray();

		} catch (IOException e) {
			System.err.println("IOException!");
		}
		return buf;
	}

	public BigInteger getUniqueValue() {
		// not needed
		return id;
	}

	public int compareTo(OverlayID arg0) {
		if (arg0 == null) {
			return 1;
		} else {
			BigInteger fid = ((NapsterOverlayID) arg0).getID();
			return id.compareTo(fid);
		}
	}

	public boolean equals(OverlayID arg0) {
		if (arg0 == null) {
			return false;
		} else {
			BigInteger fid = ((NapsterOverlayID) arg0).getID();
			return id.compareTo(fid) == 0;
		}
	}

	private BigInteger getID(String s) {
		MessageDigest md;
		byte[] sha1hash = new byte[20];
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(s.getBytes("iso-8859-1"), 0, s.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.println("UnsupportedEncodingException");
			e.printStackTrace();
		}
		return new BigInteger(1, sha1hash);
	}

}
