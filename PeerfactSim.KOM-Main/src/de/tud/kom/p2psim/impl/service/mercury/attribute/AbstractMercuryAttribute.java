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

package de.tud.kom.p2psim.impl.service.mercury.attribute;

import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;

/**
 * Base class for Attributes (name, type, value) in Mercury. Attribute is meant
 * in the context of Filters, this is not to be confused with
 * {@link MercuryAttributePrimitive}.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractMercuryAttribute implements IMercuryAttribute {

	private String name;
	
	private AttributeType type;
	
	
	public AbstractMercuryAttribute(String name, AttributeType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public AttributeType getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMercuryAttribute) {
			IMercuryAttribute atr = (IMercuryAttribute) obj;
			return (atr.getName().equals(this.getName()) 
					&& atr.getType().equals(this.getType())
					&& atr.getValue().equals(this.getValue()));
		} else {
			return false;
		}
	}
	
	@Override
	public int getTransmissionSize() {
		return name.getBytes().length + 1; // +1 = type
	}

	@Override
	public String toString() {
		return this.name;
	}

}
