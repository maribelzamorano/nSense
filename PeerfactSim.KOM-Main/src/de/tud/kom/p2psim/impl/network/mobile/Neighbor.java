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

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Neighbor extends Position {
	private double distanceToReceiver;
	private int delay;
	
	public Neighbor(double x, double y, double sqrt) {
		super();
		super.setXPos(x);
		super.setYPos(y);
		distanceToReceiver=sqrt;
	}


	public double getDistanceToReceiver() {
		return distanceToReceiver;
	}


	public void setSteps(int steps) {
		this.delay = steps;

	}

	public int getSteps() {
		return delay;
	}
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Neighbor))
			return false;
		Neighbor point2 = (Neighbor) o;
		return point2.getXPos() == this.getXPos() && point2.getYPos() == this.getYPos();
	}

}
