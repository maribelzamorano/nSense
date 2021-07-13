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


package de.tud.kom.p2psim.api.overlay;

import java.awt.Point;

/**
 * This interface is used, to store information about a node in the Overlay. It
 * is used by a {@link IDONode}, to store Information of known nodes.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public interface IDONodeInfo {

	/**
	 * The Position of the node.
	 * 
	 * @return The position.
	 */
	public Point getPosition();

	/**
	 * The AOI radius of the node.
	 * 
	 * @return The AOI radius of the node
	 */
	public int getAoiRadius();

	/**
	 * The overlay ID of the node.
	 * 
	 * @return The overlay ID of this node in the overlay.
	 */
	public OverlayID getID();
}
