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

import de.tud.kom.p2psim.api.overlay.dht.DHTKey;

/**
 * Application-specific objects, i.e. Documents, are assigned unique identifiers
 * called <code>OverlayKey</code>s, selected from the same identifier space the
 * OverlayIDs are descended from. That is why this interface extends the
 * OverlayID interface.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @version 1.0, 11/25/2007
 * 
 */

public interface OverlayKey<T> extends Comparable<OverlayKey>, DHTKey {

	/**
	 * Returns the unique value of an OverlayID
	 * 
	 * @return the unique value of an OverlayID
	 */
	public T getUniqueValue();

	/**
	 * Encodes the OverlayID into a sequence of bytes
	 * 
	 * @return the resultant byte array
	 */
	public byte[] getBytes();
}
