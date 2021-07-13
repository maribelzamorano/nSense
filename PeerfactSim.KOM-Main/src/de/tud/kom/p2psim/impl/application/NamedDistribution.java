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


package de.tud.kom.p2psim.impl.application;

import de.tud.kom.p2psim.impl.scenario.XMLConfigurableConstructor;
import de.tud.kom.p2psim.impl.util.stat.distributions.Distribution;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class NamedDistribution {

	private String name;
	private Distribution value;

	@XMLConfigurableConstructor({"name", "value"})
	public NamedDistribution(String name, Distribution value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Distribution getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "NamedDistribution [name=" + name + ", value=" + value + "]";
	}
	
}
