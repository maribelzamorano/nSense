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


package de.tud.kom.p2psim.impl.skynet.addressresolution;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class Chord2AddressResolutionImpl extends
		AbstractAddressResolution<ChordID, SkyNetID, ChordKey> {

	private static Logger log = SimLogger
			.getLogger(Chord2AddressResolutionImpl.class);

	private static Chord2AddressResolutionImpl ari;

	private Chord2AddressResolutionImpl(int size) {
		byte[] bound = new byte[size + 1];
		bound[0] = 1;
		upperBound = new BigDecimal(new BigInteger(bound));
		log.debug("the upper bound of the addressSpace is "
				+ upperBound.toPlainString());
	}

	public static Chord2AddressResolutionImpl getInstance(int size) {
		if (ari == null) {
			ari = new Chord2AddressResolutionImpl(size);
		}
		return ari;
	}

	@Override
	public ChordID getOverlayID(SkyNetID skyNetID) {
		BigDecimal dec = skyNetID.getID().multiply(upperBound);
		BigInteger chordID = null;
		try {
			chordID = dec.toBigIntegerExact();
		} catch (Exception e) {
			log.fatal("Unable to create exact integer out of "
					+ dec.toPlainString());
		}
		return new ChordID(chordID.subtract(BigInteger.ONE));
	}

	@Override
	public SkyNetID getSkyNetID(ChordID overlayID) {
		BigDecimal chordID = new BigDecimal(overlayID.getValue())
				.add(BigDecimal.ONE);
		BigDecimal skyNetID = chordID.divide(upperBound);
		return new SkyNetID(skyNetID);
	}

	@Override
	public ChordKey getOverlayKey(SkyNetID skyNetKey) {
		BigInteger chordKey = skyNetKey.getID().multiply(upperBound)
				.toBigIntegerExact();
		return new ChordKey(chordKey.subtract(BigInteger.ONE));
	}

}
