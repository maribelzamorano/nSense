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

package de.tu.darmstadt.dvs.nSense.application.moveModels;

import org.apache.commons.math.random.RandomGenerator;

import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This position distribution generates random positions, limited by the
 * dimensions of the world.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 * 
 */
public class RandomPositionDistributionNDim implements
		IPositionDistributionNDim {
	public VectorN worldDimensions;

	/**
	 * The world dimension
	 */
	public int dimension;

	private VectorN position;

	public VectorN getPosition() {
		return position;
	}

	public void setPosition(VectorN position) {
		this.position = position;
	}

	public RandomPositionDistributionNDim() {
		this.dimension = Configuration.DIMENSION;
		float[] dimensions = new float[dimension];
		for (int i = 0; i < dimension; i++) {
			dimensions[i] = 1200f;
		}
		this.worldDimensions = new VectorN(dimensions);

	}

	public RandomPositionDistributionNDim(VectorN worldDimensions) {
		this.worldDimensions = worldDimensions;
	}

	@Override
	public VectorN getNextPosition() {
		float[] dimensions = new float[Configuration.DIMENSION];
		for (int i = 0; i < Configuration.DIMENSION; i++) {
			dimensions[i] = getPosition().getValue(i);
		}
		VectorN p = new VectorN(dimensions);
		return p;
	}

	@Override
	public VectorN getNextPositionDimensions() {
		RandomGenerator r = Simulator.getRandom();
		float[] dimensions = new float[worldDimensions.getSize()];
		for (int i = 0; i < worldDimensions.getSize(); i++) {
			dimensions[i] = r.nextInt((int) worldDimensions.getValue(i));
		}
		VectorN p = new VectorN(dimensions);
		setPosition(p);
		return p;
	}

	public void setWorldDimensions(VectorN worldDimensions) {
		this.worldDimensions = worldDimensions;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

}
