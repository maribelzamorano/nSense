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

package de.tud.kom.p2psim.api.overlay.dht;

import de.tud.kom.p2psim.api.overlay.Transmitable;

/**
 * New Interface for DHT-Objects, as this allows unified handling in DHTService,
 * even for Gnutellas IResources. An Object is identified by its key and
 * contains a Value. The Value might be the object itself.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface DHTEntry extends Transmitable {

	/**
	 * Key of the Entry (for example an OverlayKey)
	 * 
	 * @return
	 */
	public DHTKey getKey();

	/**
	 * Value of the Entry, might be a reference on the DHTEntry itself
	 * 
	 * @return
	 */
	public DHTValue getValue();

}
