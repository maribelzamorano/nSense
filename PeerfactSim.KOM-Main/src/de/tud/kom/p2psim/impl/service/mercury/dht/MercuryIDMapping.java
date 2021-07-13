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

package de.tud.kom.p2psim.impl.service.mercury.dht;

import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;

/**
 * This class maps an attribute value to the ID-Range of the given DHT-Overlay.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface MercuryIDMapping {

	/**
	 * get correct OverlayKey for a Attribute-Value-Combination
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public OverlayKey map(MercuryAttributePrimitive attribute, Object value);

	/**
	 * get the attribute Integer representation of a given OverlayID.
	 * 
	 * @param attribute
	 * @param id
	 * @return
	 */
	public Integer getInteger(MercuryAttributePrimitive attribute, OverlayID id);

	/**
	 * id + 1, may be more complex for other Overlays?
	 * 
	 * @param id
	 * @return
	 */
	public OverlayID getNextID(OverlayID id);

}
