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

import de.tud.kom.p2psim.impl.application.ido.IDOApplication;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class provides a random portal. With a certain probability will be
 * executed a portal at any position to any position on the map
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05/04/2011
 */
public class RandomPortal implements IPortalComponent {

	/**
	 * The probability for the execution of a portal
	 */
	private double probability;

	@Override
	public Point portal(Point actuallyPos, IDOApplication app,
			int worldDimensionX, int worldDimensionY) {
		if (probability != 0
				&& Simulator.getRandom().nextDouble() <= probability) {
			// for speed =0
			app.setCurrentMoveVector(0, 0);
			return new Point(Simulator.getRandom().nextInt(worldDimensionX),
					Simulator.getRandom().nextInt(worldDimensionY));
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
