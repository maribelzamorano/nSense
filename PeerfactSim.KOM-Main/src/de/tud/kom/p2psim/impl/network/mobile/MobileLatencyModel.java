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



package de.tud.kom.p2psim.impl.network.mobile;

import de.tud.kom.p2psim.api.network.NetLatencyModel;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
  * 
 * @author Carsten Snider <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MobileLatencyModel implements NetLatencyModel {
	
	/**
	 * Constructor
	 * 
	 */
		private long delay;
	
	public MobileLatencyModel() {
		this.delay=Simulator.MILLISECOND_UNIT;
	}

	public void setScalingTime(double s)
	{
		this.delay=(long) (Simulator.MILLISECOND_UNIT * s);
	}
	
	public long getLatency(NetLayer sender, NetLayer receiver) {
		
		MobileMovementManager mv=((MobileNetLayer) sender).getMv();
		mv.updateMovement();
		return (delay * mv.getHopCount((MobileNetLayer) sender, (MobileNetLayer) receiver));
	}
}
