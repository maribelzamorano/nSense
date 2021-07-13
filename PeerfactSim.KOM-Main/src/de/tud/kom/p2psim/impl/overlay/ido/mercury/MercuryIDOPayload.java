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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryPayload;

/**
 * Additionally Information for a publication in a IDO overlay.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class MercuryIDOPayload implements MercuryPayload {

	/**
	 * Overlay id of the node, which publish this message.
	 */
	private OverlayID id;

	/**
	 * The AOI radius of this node, which is publish this message.
	 */
	private int aoi;

	/**
	 * Sets the given parameters.
	 * 
	 * @param id
	 *            The overlay id of the node, which generate this message.
	 * @param aoi
	 *            The AOI radius of the node, which generate this message.
	 */
	public MercuryIDOPayload(OverlayID id, int aoi) {
		this.id = id;
		this.aoi = aoi;
	}

	/**
	 * Gets the overlay ID of the node, which has publish this message.
	 * 
	 * @return The overlay ID.
	 */
	public OverlayID getId() {
		return id;
	}

	/**
	 * Gets the AOI of the node, which has publish this message.
	 * 
	 * @return The AOI radius.
	 */
	public int getAoi() {
		return aoi;
	}
}
