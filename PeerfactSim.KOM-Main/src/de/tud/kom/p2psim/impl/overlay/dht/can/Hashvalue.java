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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Generats a SHA-1 Hashvalue.
 * 
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Hashvalue {
	
	public BigInteger getHash(TransInfo transInfo) {
		return getHash(transInfo.getNetId().toString());
	}
	
	/**
	 * Generats a SHA-1 hashvalue from the Parameter
	 * 
	 * @param s
	 * 		String for the hash
	 * @return
	 */
	public BigInteger getHash(String s) {
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
