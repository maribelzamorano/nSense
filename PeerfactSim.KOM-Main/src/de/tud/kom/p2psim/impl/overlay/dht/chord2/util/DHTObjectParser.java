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

import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.SimpleDHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.scenario.Parser;

/**
 * This class implements the interface {@link Parser}, which is used to parse a
 * string from the action file. This string is then converted to the respective
 * format that is required to initialize the DHTObject.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DHTObjectParser implements Parser {

	@Override
	public Class getType() {
		return DHTObject.class;
	}

	@Override
	public Object parse(String stringValue) {
		SimpleDHTObject obj = new SimpleDHTObject();
		obj.setKey(new ChordKey(new BigInteger(stringValue)));
		return obj;
	}

}
