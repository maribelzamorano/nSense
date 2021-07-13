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

package de.tud.kom.p2psim.impl.application.ido.moveModels;

import java.awt.Point;

import org.apache.commons.math.random.RandomGenerator;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This position distribution generates random positions, limited by the
 * dimensions of the world.
 * 
 * @author Julius Rueckert
 * @version 01/06/2011
 * 
 */
public class RandomPositionDistribution implements IPositionDistribution {
	public int worldDimensionX;

	public int worldDimensionY;

	public RandomPositionDistribution() {
		this(200, 200);
	}

	public RandomPositionDistribution(int worldDimensionX, int worldDimensionY) {
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
	}

	@Override
	public Point getNextPosition() {

		RandomGenerator r = Simulator.getRandom();
		Point p = new Point(r.nextInt(worldDimensionX),
				r.nextInt(worldDimensionY));
		return p;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
	}

}
