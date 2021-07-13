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

package de.tud.kom.p2psim.impl.service.mercury.filter;

import de.tud.kom.p2psim.impl.service.mercury.attribute.AttributeType;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;

/**
 * This class provides a filter for a subscription of an Integer.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MercuryFilterInteger extends AbstractMercuryFilter {

	/**
	 * The value for the filter of the attribute
	 */
	private int value;

	/**
	 * Sets the given parameters for this class
	 * 
	 * @param name
	 *            The name of the attribute
	 * @param value
	 *            The value for the filter of the attribute
	 * @param operator
	 *            The operator type of the filter
	 */
	public MercuryFilterInteger(String name, int value, OPERATOR_TYPE operator) {
		super(name, AttributeType.Integer, operator);
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public boolean operatorGreater(IMercuryAttribute attribute) {
		return ((Integer) attribute.getValue() > this.getValue());
	}
	
	@Override
	public boolean operatorGreaterEquals(IMercuryAttribute attribute) {
		return ((Integer) attribute.getValue() >= this.getValue());
	}
	
	@Override
	public boolean operatorSmaller(IMercuryAttribute attribute) {
		return ((Integer) attribute.getValue() < this.getValue());
	}
	
	@Override
	public boolean operatorSmallerEquals(IMercuryAttribute attribute) {
		return ((Integer) attribute.getValue() <= this.getValue());
	}
	
	@Override
	public int getTransmissionSize() {
		return super.getTransmissionSize() + 4;
	}

}
