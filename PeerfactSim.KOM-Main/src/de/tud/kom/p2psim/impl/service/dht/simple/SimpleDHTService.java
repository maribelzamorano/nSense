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

package de.tud.kom.p2psim.impl.service.dht.simple;

import de.tud.kom.p2psim.impl.service.dht.AbstractDHTService;

/**
 * Provides a Storage for DHT-Objects, mainly to act as a replacement for
 * HashMaps in OverlayNodes if DHTServices are to be used. It is used to
 * preserve compatibility to older configuration files which do not specify a
 * DHTService but instead rely on the implementation of the specific Overlay
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimpleDHTService extends AbstractDHTService {

	public SimpleDHTService() {
		super(null);
	}

}
