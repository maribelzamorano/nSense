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

package de.tu.darmstadt.dvs.nSense;

import de.tu.darmstadt.dvs.nSense.application.IDONodeInfoNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * A basic implementation of a {@link IDONodeInfoNDim}. It provides a container
 * of information for a node.
 * 
 * @author Maribel Zamorano
 * @version 10/11/2013
 */
public class NodeInfoNDim implements IDONodeInfoNDim {

	/**
	 * The position of a node
	 */
	protected VectorN position;

	protected VectorN positionDimensions;

	/**
	 * The area of interest of a node
	 */
	protected int aoi;

	/**
	 * The id of a node.
	 */
	protected OverlayID id;

	/**
	 * Constructor of this class. It sets the given values.
	 * 
	 * @param position
	 *            The position of the node.
	 * @param positionDimensions2
	 * @param aoi
	 *            The area of interest of the node.
	 * @param id
	 *            The ID of the node.
	 */
	public NodeInfoNDim(VectorN position, VectorN positionDimensions, int aoi,
			OverlayID id) {
		this.position = position;
		this.positionDimensions = positionDimensions;
		this.aoi = aoi;
		this.id = id;
	}

	@Override
	public VectorN getPosition() {
		return position;
	}

	@Override
	public int getAoiRadius() {
		return aoi;
	}

	@Override
	public OverlayID getID() {
		return id;
	}

	@Override
	public VectorN getPositionDimensions() {
		return positionDimensions;
	}

}
