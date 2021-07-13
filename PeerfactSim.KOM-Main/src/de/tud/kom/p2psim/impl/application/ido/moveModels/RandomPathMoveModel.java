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
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.impl.application.ido.IDOApplication;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The model moves the player in a virtual world. The world should be set with
 * the setter. The speed of a player, can be increased with this move model.
 * Every player choice a point in this world. The player moves to this point in
 * small steps, which are derived in this class.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class RandomPathMoveModel implements IMoveModel {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(RandomPathMoveModel.class);

	/**
	 * The maximal move speed for the starting node.
	 */
	public double moveSpeedLimit;

	/**
	 * The world dimension in X direction.
	 */
	public int worldDimensionX;

	/**
	 * The world dimension in Y direction.
	 */
	public int worldDimensionY;

	/**
	 * The rate to change speed. It will only used, if the node activate the
	 * changing of the speed.
	 */
	public double speedChangingRate;

	/**
	 * The portal component.
	 */
	public IPortalComponent portal;

	public Map<IDOApplication, MoveInformation> moveStorage = new Hashtable<IDOApplication, MoveInformation>();

	/**
	 * Create a random move model with following properties: moveSpeedLimit =
	 * 10;<br>
	 * worldDimensionX = 200; <br>
	 * worldDimensionY = 200; <br>
	 * speedChaining Rate = 0.025; <br>
	 * <p>
	 * If you need other parameter, then you can set this over the setters in
	 * this class.
	 */

	static String id2 = "";

	public RandomPathMoveModel() {

		this(4, 200, 200, 0.025);
	}

	/**
	 * @param moveSpeedLimit
	 *            The standard speedLimit to which is started for all players.
	 *            Can changed for every player, if it is activate the changing
	 *            of the speed. The rates for the changing can set in
	 *            {@link #speedChangingRate}.
	 * @param worldDimensionX
	 *            The dimension in X direction of the world.
	 * @param worldDimensionY
	 *            The dimension in Y direction of the world.
	 * @param speedChaningRate
	 *            The rate to change speed. It will only used, if it is activate
	 *            the changing of the speed. For every call of
	 *            getNextPosition(...), the speed will be changed with this rate
	 *            for the given player.
	 */
	public RandomPathMoveModel(int moveSpeedLimit, int worldDimensionX,
			int worldDimensionY, double speedChaningRate) {

		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
		this.speedChangingRate = speedChaningRate;
		if (moveSpeedLimit <= 0) {
			log.warn("Bad value for move speed limit. It is to equals or smaller as 0.");
		}
	}

	@Override
	public Point getNextPosition(IDOApplication app) {
		MoveInformation moveInfo = moveStorage.get(app);
		if (moveInfo == null) {
			moveInfo = new MoveInformation(moveSpeedLimit,
					app.getPlayerPosition(), app.getPlayerPosition());
			moveStorage.put(app, moveInfo);
		}
		String id = app.getHost().getOverlay(IDONode.class).getOverlayID()
				.toString();
		id2 = id;
		if (portal != null) {
			Point position = portal.portal(app.getPlayerPosition(), app,
					worldDimensionX, worldDimensionY);
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

		if ((moveInfo.getTarget().x == (app.getPlayerPosition().x))
				|| (moveInfo.getTarget().y == (app.getPlayerPosition().y))) {
			moveInfo.setTarget(nextTarget());
		}

		Point nextPosition;
		double distance = app.getPlayerPosition()
				.distance(moveInfo.getTarget());
		if (distance >= moveInfo.getMaxSpeed() && moveInfo.getMaxSpeed() > 0) {
			double normX = (moveInfo.getTarget().x - app.getPlayerPosition().x)
					/ distance;
			double normY = (moveInfo.getTarget().y - app.getPlayerPosition().y)
					/ distance;
			double stepX = normX * moveInfo.getMaxSpeed();
			double stepY = normY * moveInfo.getMaxSpeed();

			// if the step is 0 in integer
			if (Math.abs(stepX) < 1 && Math.abs(stepY) < 1) {
				if (Math.abs(stepX) < Math.abs(stepY)) {
					if (stepY < 0)
						stepY = -1;
					else
						stepY = 1;
				} else {
					if (stepX < 0)
						stepX = -1;
					else
						stepX = 1;
				}

			}
			int newX = app.getPlayerPosition().x + (int) stepX;
			int newY = app.getPlayerPosition().y + (int) stepY;

			nextPosition = new Point(newX, newY);

		} else if (moveInfo.getMaxSpeed() == 0) {

			nextPosition = app.getPlayerPosition();
		} else {
			nextPosition = moveInfo.getTarget();
		}
		moveInfo.setLastPosition(app.getPlayerPosition());

		setMoveVector(app, nextPosition);

		return nextPosition;
	}

	private void setMoveVector(IDOApplication app, Point nextPosition) {
		app.setCurrentMoveVector(nextPosition.x - app.getPlayerPosition().x,
				nextPosition.y - app.getPlayerPosition().y);
	}

	private Point nextTarget() {
		RandomGenerator r = Simulator.getRandom();
		Point p = new Point(r.nextInt(worldDimensionX),
				r.nextInt(worldDimensionY));
		return p;
	}

	public void setMoveSpeedLimit(double moveSpeedLimit) {
		if (moveSpeedLimit <= 0) {
			log.warn("Bad value for move speed limit. It is to equals or smaller as 0.");
		}
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
	}

	public void setSpeedChangingRate(double speedChangingRate) {
		this.speedChangingRate = speedChangingRate;
	}

	public void setPortal(IPortalComponent portal) {
		this.portal = portal;
	}
}
