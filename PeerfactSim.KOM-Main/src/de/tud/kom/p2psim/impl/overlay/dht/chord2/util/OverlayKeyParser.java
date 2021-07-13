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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.util;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.scenario.Parser;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class OverlayKeyParser implements Parser {

	@Override
	public Class getType() {
		return OverlayKey.class;
	}

	@Override
	public Object parse(String stringValue) {
		return new ChordKey(new BigInteger(stringValue));
	}

}
