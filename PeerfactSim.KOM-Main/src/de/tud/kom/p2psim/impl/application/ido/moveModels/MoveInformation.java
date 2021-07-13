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

/**
 * This class stores move information. The allowed maximal speed, the target
 * point and the last position. It is used to derive the next move in many move
 * models.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class MoveInformation {

	/**
	 * The maximal speed, which can the node move. It is the standard speed, if
	 * a node moves.
	 */
	private double maxSpeed;

	/**
	 * The target point on the map of a node.
	 */
	private Point target;

	/**
	 * The last position on the map of a node.
	 */
	private Point lastPosition;

	/**
	 * Stores the given parameter.
	 * 
	 * @param maxSpeed
	 *            maximal speed of a node
	 * @param target
	 *            the target of a node
	 * @param lastPosition
	 *            the last position of a node
	 */
	public MoveInformation(double maxSpeed, Point target, Point lastPosition) {
		this.maxSpeed = maxSpeed;
		this.lastPosition = lastPosition;
		this.target = target;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Point getTarget() {
		return target;
	}

	public void setTarget(Point target) {
		this.target = target;
	}

	public Point getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(Point lastPosition) {
		this.lastPosition = lastPosition;
	}
}
