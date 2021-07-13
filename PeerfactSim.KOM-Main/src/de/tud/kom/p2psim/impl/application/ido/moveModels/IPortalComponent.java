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

/**
 * An interface for the portal component. This can be implemented by a class of
 * {@link IMoveModel}. This interface provide a jump to a wide position, which
 * represent a portal. It should be call every calculation of a new position.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04/05/2011
 */
public interface IPortalComponent {

	/**
	 * This methode execute a portal with a certain probability. Additionally it
	 * sets the moveVector in the {@link IDOApplication}, to that the speed to
	 * 0.
	 * 
	 * @param actuallyPos
	 *            The actually position
	 * @param app
	 *            The IDO-Application to set the moveVector
	 * @param worldDimensionX
	 *            The world dimension in X
	 * @param worldDimensionY
	 *            The world dimension in Y
	 * @return A new position, if a portal is executed or null.
	 */
	public Point portal(Point actuallyPos, IDOApplication app,
			int worldDimensionX, int worldDimensionY);

	/**
	 * Sets the probability for a the using of a portal.
	 * 
	 * @param probability
	 */
	public void setProbability(double probability);
}
