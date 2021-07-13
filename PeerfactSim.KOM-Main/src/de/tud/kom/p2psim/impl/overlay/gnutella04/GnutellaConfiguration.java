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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.math.BigInteger;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaConfiguration {
	public final static int GNUTELLA_PING_MAX_TTL = 3;

	public final static int GNUTELLA_QUERY_MAX_TTL = 5;

	public final static double RELATIVE_CONNECT_SLOTS = 0.2;

	public final static int QUERY_KEY_MASK = 7;

	private static final int TIME_BITS_IGNORE = 6;

	private static final int TIME_BITS = 30;

	private static final int ID_BITS = 11;

	public static BigInteger generateDescriptor(BigInteger id) {

		BigInteger rand = BigInteger.valueOf(Simulator.getRandom().nextLong());
		BigInteger time = BigInteger.valueOf(Simulator.getCurrentTime())
				.divide(BigInteger.valueOf(2).pow(TIME_BITS_IGNORE));

		BigInteger timeMultiplicator = BigInteger.valueOf(2).pow(TIME_BITS);

		// mask time
		time = time.and(BigInteger.valueOf(2).pow(TIME_BITS).subtract(
				BigInteger.valueOf(1)));
		// mask id and shift
		id = id.and(
				BigInteger.valueOf(2).pow(ID_BITS).subtract(
						BigInteger.valueOf(1))).shiftLeft(TIME_BITS);
		// mask rand
		rand = rand.andNot(BigInteger.valueOf(2).pow(TIME_BITS + ID_BITS)
				.subtract(BigInteger.valueOf(1)));

		return time.add(id).add(rand);
	}
}
