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


package de.tud.kom.p2psim.impl.overlay.util;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.impl.network.gnp.GnpNetLayer;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class BandwidthMonitor {

	static final long maxQueueTime = 5000000;

	private NetLayer netLayer;

	/*
	 * Just for statistic freaks. Helpful to debug congestion-related problems.
	 */
	public static int deniedAttempts = 0;

	public static int permittedAttempts = 0;

	public BandwidthMonitor(Host host) {
		this.netLayer = host.getNetLayer();
	}

	/**
	 * Tests whether this peer may send more things through the net.
	 * 
	 * @return
	 */
	public boolean enoughBandwidth() {

		if (netLayer instanceof GnpNetLayer) {

			long queueDelay = ((GnpNetLayer) netLayer).getNextFreeSendingTime()
					- Simulator.getCurrentTime();

			if (queueDelay < maxQueueTime) {
				permittedAttempts++;
				return true; // Queue not too long, enough bandwidth.
			} else {
				deniedAttempts++;
				return false; // Queue too long, not enough bandwidth, drop
				// something.
			}
		}

		/*
		 * Add other net layers here.
		 */

		permittedAttempts++;
		return true; // Don't know, so risk congestion
	}

}
