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

import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;

/**
 * This class stores move information. The allowed maximal speed, the target
 * point and the last position. It is used to derive the next move in many move
 * models.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 */
public class MoveInformationNDim {

	/**
	 * The maximal speed, which can the node move. It is the standard speed, if
	 * a node moves.
	 */
	private double maxSpeed;

	/**
	 * The target point on the map of a node.
	 */
	private VectorN target;

	/**
	 * The last position on the map of a node.
	 */
	private VectorN lastPosition;

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
	public MoveInformationNDim(double maxSpeed, VectorN target, VectorN lastPosition) {
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

	public VectorN getTarget() {
		return target;
	}

	public void setTarget(VectorN target) {
		this.target = target;
	}

	public VectorN getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(VectorN lastPosition) {
		this.lastPosition = lastPosition;
	}
}
