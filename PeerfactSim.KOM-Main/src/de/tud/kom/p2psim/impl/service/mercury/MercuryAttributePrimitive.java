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

package de.tud.kom.p2psim.impl.service.mercury;

import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.service.mercury.attribute.AttributeType;

/**
 * A Mercury-Attribute, not to be confused with the usage of the term <code>Attribute</code> in conjunction
 * with Filters (in package <code>mercury.attribute</code> and <code>mercury.filter</code>).
 * This is simply a class to store attribute names and types used by the application and their ranges
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MercuryAttributePrimitive {
	
	private String name = null;
	
	private AttributeType type = null;
	
	private Comparable min = null;
	
	private Comparable max = null;
	
	private long expiresAfter = 0;
	
	
	
	public MercuryAttributePrimitive() {
		// Intentionally left blank. Needed by config.xml
	}
	
	public String getName() {
		return this.name;
	}
	
	public AttributeType getType() {
		return this.type;
	}
	
	public Comparable getMin() {
		return this.min;
	}
	
	public Comparable getMax() {
		return this.max;
	}
	
	public long getExpirationTime() {
		return this.expiresAfter;
	}
	
	public boolean doesExpire() {
		return this.expiresAfter > 0;
	}

	/*
	 * Setters only for one-time usage through config.xml
	 */
	private boolean nameSet = false;
	private boolean typeSet = false;
	private boolean minSet = false;
	private boolean maxSet = false;
	
	public void setName(String name) {
		if (!nameSet)
			this.name = name;
		nameSet = true;
	}
	
	public void setType(String type) {
		if (!typeSet) {
			this.type = AttributeType.valueOf(type);
			if (this.type == null) {
				throw new ConfigurationException("Attribute Type "+type+" is not specified for Mercury");
			}
		}
		typeSet = true;
	}
	
	public void setRangemin(String min) {
		if (!minSet) {
			if (this.type == AttributeType.Integer) {
				this.min = Integer.parseInt(min);
			} else {
				System.err
						.println("Other Types than Integer are not yet specified by MercuryAttributePrimitive");
			}
		}
		minSet = true;
	}
	
	public void setRangemax(String max) {
		if (!maxSet) {
			if (this.type == AttributeType.Integer) {
				this.max = Integer.parseInt(max);
			} else {
				System.err
						.println("Other Types than Integer are not yet specified by MercuryAttributePrimitive");
			}
		}
		maxSet = true;
	}
	
	public void setSubscriptionExpiresAfter(long expiresAfter) {
		this.expiresAfter = expiresAfter;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MercuryAttributePrimitive) {
			MercuryAttributePrimitive attr = (MercuryAttributePrimitive) obj;
			return attr.getName().equals(this.getName());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
