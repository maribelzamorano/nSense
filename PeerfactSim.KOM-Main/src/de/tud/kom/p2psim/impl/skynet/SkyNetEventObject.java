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

import de.tud.kom.p2psim.api.service.skynet.SkyNetEventType;

/**
 * This class contains a <code>SkyNetEventType</code> as well as the timestamp
 * of its creation. With both elements, one can determine, when the
 * SkyNet-event, whose type is specified by <code>SkyNetEventType</code>, was
 * created.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetEventObject {

	private SkyNetEventType type;

	private long initTime;

	private int metricsUpdateCounter;

	public SkyNetEventObject(SkyNetEventType eventType, long initTime) {
		// type = eventType;
		// this.initTime = initTime;
		// metricsUpdateCounter = -1;
		this(eventType, initTime, -1);
	}

	public SkyNetEventObject(SkyNetEventType eventType, long initTime,
			int metricsUpdateCounter) {
		type = eventType;
		this.initTime = initTime;
		this.metricsUpdateCounter = metricsUpdateCounter;
	}

	/**
	 * Returns a <code>SkyNetEventType</code>, which contains the type of the
	 * created SkyNet-event.
	 * 
	 * @return an instance of <code>SkyNetEventType</code>
	 */
	public SkyNetEventType getType() {
		return type;
	}

	/**
	 * Returns the time, when the corresponding event was created.
	 * 
	 * @return the timestamp of the created event
	 */
	public long getInitTime() {
		return initTime;
	}

	public int getMetricsUpdateCounter() {
		return metricsUpdateCounter;
	}
}
