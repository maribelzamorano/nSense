/* Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
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

import de.tu.darmstadt.dvs.nSense.application.IDOApplicationNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This move model generates a random new position, derived from the old
 * position and an offset, which is limited by the move speed.
 * 
 * @author Maribel Zamorano
 * @version 25/11/2013
 * 
 */
public class RandomMoveModelNDim implements IMoveModelNDim {
	public int moveSpeedLimit;

	public VectorN worldDimensions;

	public RandomMoveModelNDim() {//to-do update to variable dimensions
		this(10, new VectorN(new float[] { 200f, 200f }));
	}

	/**
	 * The portal component.
	 */
	public IPortalComponentNDim portal;

	public RandomMoveModelNDim(int moveSpeedLimit, VectorN worldDimensions) {
		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensions = worldDimensions;
	}

	@Override
	public VectorN getNextPosition(IDOApplicationNDim app) {
		RandomGenerator r = Simulator.getRandom();

		VectorN pos = app.getPlayerPosition();

		VectorN newPos;

		if (portal != null) {
			VectorN position = portal.portal(app.getPlayerPosition(), app,
					worldDimensions);
			// if a portal is used.
			if (position != null) {
				return position;
			}
		}

		do {
			float[] dimensions = new float[worldDimensions.getSize()];
			for (int i = 0; i < worldDimensions.getSize(); i++) {
				dimensions[i] = pos.getValue(i)
						+ (r.nextInt(2 * moveSpeedLimit + 1) - moveSpeedLimit);
			}
			newPos = new VectorN(dimensions);

		} while (isNewPos(newPos));
		setMoveVector(app, newPos);
		return newPos;
	}

	private boolean isNewPos(VectorN newPos) {//to-do probar
		for (int i = 0; i < newPos.getSize(); i++) {
			if (newPos.getValue(i) < 0
					|| newPos.getValue(i) > worldDimensions.getValue(i))
				return true;
		}
		return false;
	}

	private void setMoveVector(IDOApplicationNDim app, VectorN nextPosition) {//to-do probar
		float[] dimensions = new float[worldDimensions.getSize()];
		for (int i = 0; i < worldDimensions.getSize(); i++) {
			dimensions[i] = nextPosition.getValue(i)-app.getPlayerPosition().getValue(i);
		}
		app.setCurrentMoveVector(new VectorN(dimensions));
	}

	public void setMoveSpeedLimit(int moveSpeedLimit) {
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public void setWorldDimensions(VectorN worldDimensions) {
		this.worldDimensions = worldDimensions;
	}

	public void setPortal(IPortalComponentNDim portal) {
		this.portal = portal;
	}
}
