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

import de.tu.darmstadt.dvs.nSense.application.IDOApplicationNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class provides a random portal. With a certain probability will be
 * executed a portal at any position to any position on the map
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 */
public class RandomPortalNDim implements IPortalComponentNDim {

	/**
	 * The probability for the execution of a portal
	 */
	private double probability;

	@Override
	public VectorN portal(VectorN actuallyPos, IDOApplicationNDim app,
			VectorN worldDimensions) {
		if (probability != 0
				&& Simulator.getRandom().nextDouble() <= probability) {
			// for speed =0
			app.setCurrentMoveVector(new VectorN(new float[] { 0f, 0f }));
			float[] dimensions = new float[worldDimensions.getSize()];
			for (int i = 0; i < worldDimensions.getSize(); i++) {
				dimensions[i] = Simulator.getRandom().nextInt(
						(int) worldDimensions.getValue(i));
			}
			return new VectorN(dimensions);
		}

		return null;
	}

	@Override
	public void setProbability(double probability) {
		if (probability < 0 || probability > 1)
			throw new RuntimeException(
					"The probability for RandomPortal must be between 0 and 1 [0,1]");
		this.probability = probability;
	}

}
