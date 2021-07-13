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

package de.tud.kom.p2psim.impl.service.mercury.dht;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;
import de.tud.kom.p2psim.impl.service.mercury.attribute.AttributeType;

/**
 * ID-Mapping for Chord, translate between attribute value and OverlayID/Key
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryIDMappingChord implements MercuryIDMapping {

	BigDecimal maxValue = new BigDecimal("2").pow(ChordID.KEY_BIT_LENGTH);
	
	@Override
	public ChordKey map(MercuryAttributePrimitive attribute,
			Object value) {
		
		// TODO implement other types
		// FIXME wraparound for value == maxValue
		
		if (attribute.getType().equals(AttributeType.Integer)) {
			Integer range = (Integer) attribute.getMax()
					- (Integer) attribute.getMin();
			BigDecimal val = new BigDecimal(value.toString())
					.subtract(new BigDecimal(attribute.getMin().toString()));
			BigDecimal resultingKey = this.maxValue.multiply(val)
					.divideToIntegralValue(
					new BigDecimal(range.toString()));
			return new ChordKey(resultingKey.toBigInteger());
		} else {
			return null;
		}
		
	}

	public Integer getInteger(MercuryAttributePrimitive attribute, OverlayID id) {
		ChordID cid = (ChordID) id;
		if (attribute.getType().equals(AttributeType.Integer)) {
			Integer range = (Integer) attribute.getMax()
					- (Integer) attribute.getMin();
			BigDecimal resultingInt = new BigDecimal(range.toString())
					.multiply(new BigDecimal(cid.getUniqueValue()))
					.divideToIntegralValue(
							maxValue);
			return resultingInt.intValue();
		} else {
			return null;
		}
	}

	public OverlayID getNextID(OverlayID id) {
		ChordID cid = (ChordID) id;
		return new ChordID(cid.getUniqueValue().add(new BigInteger("1")));
	}

}
