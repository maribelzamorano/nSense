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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.service.skynet.AliasInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;

/**
 * The abstract class implements four of the five predefined methods from the
 * <code>AliasInfo</code>-interface, which every of the actual extending classes
 * may use. This class is used to relieve the extending classes of implementing
 * these common getter- and setter-methods.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public abstract class AbstractAliasInfo implements AliasInfo {

	protected int numberOfUpdates;

	protected SkyNetNodeInfo nodeInfo;

	protected long timestampOfUpdate = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.skynet.AliasInfo#getNumberOfUpdates()
	 */
	public int getNumberOfUpdates() {
		return numberOfUpdates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.skynet.AliasInfo#setNumberOfUpdates(int)
	 */
	public void setNumberOfUpdates(int numberOfUpdates) {
		this.numberOfUpdates = numberOfUpdates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.skynet.AliasInfo#getNodeInfo()
	 */
	public SkyNetNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.skynet.AliasInfo#getTimestampOfUpdate()
	 */
	public long getTimestampOfUpdate() {
		return timestampOfUpdate;
	}

}
