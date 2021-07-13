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


package de.tud.kom.p2psim.impl.overlay;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This is a helper class to be able to reuse the SHA1 hash generation of a
 * string.
 * 
 * The idea was taken from the implementation of chord.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class IDGenerationHelper {

	/*
	 * The default id length is set to 160
	 */
	public static final int DEFAULT_ID_LENGTH = 160;

	private static final BigInteger TWO = new BigInteger("2");

	/**
	 * @param stringToHash
	 * @param numberOfBits
	 * @return the BigInteger interpretation of the SHA1-Hash
	 */
	public static BigInteger getSHA1Hash(String stringToHash, int numberOfBits) {
		MessageDigest md;
		byte[] sha1hash = new byte[numberOfBits];
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(stringToHash.getBytes("iso-8859-1"), 0,
					stringToHash.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.println("UnsupportedEncodingException");
			e.printStackTrace();
		}
		BigInteger value = new BigInteger(1, sha1hash);

		// Make sure the value does not have more than numberOfBits bits
		value = value.mod(TWO.pow(numberOfBits));

		return value;
	}

	/**
	 * @param stringToHash
	 * @return the BigInteger interpretation of the SHA1-Hash
	 */
	public static BigInteger getSHA1Hash(String stringToHash) {
		return getSHA1Hash(stringToHash, DEFAULT_ID_LENGTH);
	}
}
