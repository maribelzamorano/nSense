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

import de.tud.kom.p2psim.api.overlay.Transmitable;
import de.tud.kom.p2psim.impl.service.mercury.attribute.AttributeType;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;

/**
 * Interface for Filters
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface IMercuryFilter extends Transmitable {

	/**
	 * The operator types of the filter. A filter can contain one of this
	 * operator.
	 * 
	 * @author Christoph Muenker
	 */
	public static enum OPERATOR_TYPE {
		/**
		 * <
		 */
		smaller,
		/**
		 * >
		 */
		greater,
		/**
		 * =
		 */
		equals,
		/**
		 * <=
		 */
		smallerEquals,
		/**
		 * >=
		 */
		greaterEquals,
		/**
		 * For example j* or *
		 */
		prefix,
		/**
		 * For example *j or *
		 */
		postfix;
	}

	/**
	 * Gets the attribute type of the filter back.
	 * 
	 * @return The attribute type for the filter.
	 */
	public AttributeType getType();

	/**
	 * Gets the value for the filter back.
	 * 
	 * @return The value of the filter.
	 */
	public Comparable getValue();

	/**
	 * Gets the name of the attribute back.
	 * 
	 * @return The attribute name.
	 */
	public String getName();

	/**
	 * Gets the operator of the filter back.
	 * 
	 * @return The operator of this filter.
	 */
	public OPERATOR_TYPE getOperator();

	/**
	 * Determine the matching of the given attribute with the filter.
	 * 
	 * TODO: desribe
	 * 
	 * @param attribute
	 * @return
	 */
	public boolean match(IMercuryAttribute attribute);
}
