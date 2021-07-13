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

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.application.IDOApplicationNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The model moves the player in a virtual world. The world should be set with
 * the setter. The speed of a player, can be increased with this move model.
 * Every player choice a point in this world. The player moves to this point in
 * small steps, which are derived in this class.
 * 
 * @author Maribel Zamorano
 * @version 01/11/2013
 */
public class RandomPathMoveModelNDim implements IMoveModelNDim {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger
			.getLogger(RandomPathMoveModelNDim.class);

	/**
	 * The maximal move speed for the starting node.
	 */
	public double moveSpeedLimit;

	/**
	 * The world dimension
	 */
	public int dimension;

	/**
	 * The world position dimensions
	 */
	public VectorN worldDimensions;

	/**
	 * The rate to change speed. It will only used, if the node activate the
	 * changing of the speed.
	 */
	public double speedChangingRate;

	/**
	 * The portal component.
	 */
	public IPortalComponentNDim portal;

	public Map<IDOApplicationNDim, MoveInformationNDim> moveStorage = new Hashtable<IDOApplicationNDim, MoveInformationNDim>();

	/**
	 * Create a random move model with following properties: moveSpeedLimit =
	 * 10;<br>
	 * worldDimensions = 200; each <br>
	 * speedChaining Rate = 0.025; <br>
	 * <p>
	 * If you need other parameter, then you can set this over the setters in
	 * this class.
	 */
	public RandomPathMoveModelNDim() {

		this.moveSpeedLimit = 3.5;
		this.dimension = Configuration.DIMENSION;
		float[] dimensions = new float[dimension];
		for (int i = 0; i < dimension; i++) {
			dimensions[i] = 1200f;
		}
		this.worldDimensions = new VectorN(dimensions);
		this.speedChangingRate = 0.025;
	}

	/**
	 * @param moveSpeedLimit
	 *            The standard speedLimit to which is started for all players.
	 *            Can changed for every player, if it is activate the changing
	 *            of the speed. The rates for the changing can set in
	 *            {@link #speedChangingRate}.
	 * @param worldDimensions
	 *            The dimension in all directions of the world.
	 * @param speedChaningRate
	 *            The rate to change speed. It will only used, if it is activate
	 *            the changing of the speed. For every call of
	 *            getNextPosition(...), the speed will be changed with this rate
	 *            for the given player.
	 */
	public RandomPathMoveModelNDim(int moveSpeedLimit, VectorN worldDimensions,
			double speedChaningRate) {
		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensions = worldDimensions;
		this.speedChangingRate = speedChaningRate;

		if (moveSpeedLimit <= 0) {
			log.warn("Bad value for move speed limit. It is to equals or smaller as 0.");
		}

	}

	@Override
	public VectorN getNextPosition(IDOApplicationNDim app) {
		MoveInformationNDim moveInfo = moveStorage.get(app);
		if (moveInfo == null) {
			moveInfo = new MoveInformationNDim(moveSpeedLimit,
					app.getPlayerPositionDimensions(),
					app.getPlayerPositionDimensions());
			moveStorage.put(app, moveInfo);
		}

		if (portal != null) {
			VectorN position = portal.portal(app.getPlayerPositionDimensions(),
					app, worldDimensions);
			// if a portal is used.
			if (position != null) {
				moveInfo.setLastPosition(position);
				return position;
			}
		}

		if (app.speedChanging() != 0) {
			double newMaxSpeed = moveInfo.getMaxSpeed() + speedChangingRate
					* app.speedChanging();
			moveInfo.setMaxSpeed(newMaxSpeed);
		}
		if (moveInfo.getTarget().equals(app.getPlayerPositionDimensions())) {
			moveInfo.setTarget(nextTarget());
		}

		VectorN nextPosition;

		double distance = app.getPlayerPositionDimensions().distance(
				moveInfo.getTarget());
		if (distance >= moveInfo.getMaxSpeed() && moveInfo.getMaxSpeed() > 0) {
			float[] dimensions = new float[worldDimensions.getSize()];
			for (int i = 0; i < worldDimensions.getSize(); i++) {
				double norm = (moveInfo.getTarget().getValue(i) - app
						.getPlayerPositionDimensions().getValue(i)) / distance;
				double step = norm * moveInfo.getMaxSpeed();
				// if the step is 0 in integer
				if (Math.abs(step) < 1) {// to-do probar
					if (step < 0)
						step = -1;
					else
						step = 1;
				}
				int newDim = (int) (app.getPlayerPositionDimensions().getValue(
						i) + step);
				dimensions[i] = newDim;
			}
			nextPosition = new VectorN(dimensions);
		} else if (moveInfo.getMaxSpeed() == 0) {
			nextPosition = app.getPlayerPositionDimensions();
		} else {
			nextPosition = moveInfo.getTarget();
		}
		moveInfo.setLastPosition(app.getPlayerPositionDimensions());
		setMoveVector(app, nextPosition);
		return nextPosition;
	}

	private void setMoveVector(IDOApplicationNDim app, VectorN nextPosition) {
		float[] dimensions = new float[worldDimensions.getSize()];
		for (int i = 0; i < worldDimensions.getSize(); i++) {
			dimensions[i] = (nextPosition.getValue(i) - app
					.getPlayerPositionDimensions().getValue(i));
		}
		app.setCurrentMoveVector(new VectorN(dimensions));
	}

	private VectorN nextTarget() {
		float[] dimensions = new float[worldDimensions.getSize()];
		for (int i = 0; i < worldDimensions.getSize(); i++) {
			dimensions[i] = Simulator.getRandom().nextInt(
					(int) worldDimensions.getValue(i));
		}
		return new VectorN(dimensions);
	}

	public void setMoveSpeedLimit(double moveSpeedLimit) {
		if (moveSpeedLimit <= 0) {
			log.warn("Bad value for move speed limit. It is to equals or smaller as 0.");
		}
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public void setWorldDimensions(VectorN worldDimensions) {
		this.worldDimensions = worldDimensions;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public void setSpeedChangingRate(double speedChangingRate) {
		this.speedChangingRate = speedChangingRate;
	}

	public void setPortal(IPortalComponentNDim portal) {
		this.portal = portal;
	}
}
