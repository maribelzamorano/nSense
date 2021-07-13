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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Configuration for the IDO with mercury.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 * 
 */
public class MercuryIDOConfiguration {

	/**
	 * Time between two operation, to check for a heartbeat msg
	 */
	public static final long TIME_BETWEEN_HEARTBEAT_OPERATION = 5000 * Simulator.MILLISECOND_UNIT;

	/**
	 * Maximal time between two publication for the position.
	 */
	public static final long INTERVAL_BETWEEN_HEARTBEATS = 5000 * Simulator.MILLISECOND_UNIT;

	/**
	 * The valid time of a received node information. After this time, the
	 * information will be discarded.
	 */
	public static final long TIME_TO_VALID_OF_NODE_INFOS = 2500 * Simulator.MILLISECOND_UNIT;

	/**
	 * The time interval between two subscriptions.
	 */
	public static final long TIME_BETWEEN_SUBSCRIPTION_UPDATE = 500 * Simulator.MILLISECOND_UNIT;

	/**
	 * Constant for the area of interest (aoi) of a mercury ido node
	 */
	public static int AOI = 200;
}
