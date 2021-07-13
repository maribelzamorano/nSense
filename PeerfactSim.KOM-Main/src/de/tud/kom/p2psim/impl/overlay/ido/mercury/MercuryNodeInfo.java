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

import java.awt.Point;

import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.NodeInfo;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Container to store the actually position, which is received by a peer.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 * 
 */
public class MercuryNodeInfo extends NodeInfo implements IDONodeInfo {

	/**
	 * The point of time in the simulation, which is arrived this information.
	 */
	private long lastUpdate;

	public MercuryNodeInfo(Point position, int aoi, OverlayID id) {
		super(position, aoi, id);
		this.lastUpdate = Simulator.getCurrentTime();
	}

	/**
	 * It describes the point of time, which the last update is arrived. It is
	 * set by the creation of the update with the actually time.
	 * 
	 * @return The simulation point of time of the update.
	 */
	public long getLastUpdateTime() {
		return lastUpdate;
	}
}
