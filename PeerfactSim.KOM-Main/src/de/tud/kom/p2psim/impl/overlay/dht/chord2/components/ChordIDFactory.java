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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.IDGenerationHelper;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This class creates ChordId instance by using SHA-1 Hash function.
 * 
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordIDFactory {

	private static final ChordIDFactory INSTANCE = new ChordIDFactory();

	private static Logger log = SimLogger.getLogger(ChordIDFactory.class);

	private ChordIDFactory() {
		// Private constructor prevents instantiation from other classes
	}

	public static ChordIDFactory getInstance() {
		return INSTANCE;
	}

	public BigInteger createNewID(TransInfo transInfo) {
		// use SHA-1 Hash value of transInfo as id
		return IDGenerationHelper.getSHA1Hash(transInfo.getNetId().toString(),
				ChordID.KEY_BIT_LENGTH);
	}

	public ChordID createRandomChordID() {
		// use SHA-1 Hash value of transInfo as id
		BigInteger id = IDGenerationHelper.getSHA1Hash(new Integer(Simulator
				.getRandom().nextInt()).toString(), ChordID.KEY_BIT_LENGTH);
		return new ChordID(id);
	}

	public ChordID getChordID(String s) {
		BigInteger id = IDGenerationHelper.getSHA1Hash(s,
				ChordID.KEY_BIT_LENGTH);
		return new ChordID(id);
	}

	public ChordID getChordID(BigInteger id) {
		return new ChordID(id);
	}
}
