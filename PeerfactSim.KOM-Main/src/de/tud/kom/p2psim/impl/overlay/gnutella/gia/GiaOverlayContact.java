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


/**
 * 
 */
package de.tud.kom.p2psim.impl.overlay.gnutella.gia;

import java.util.Comparator;

import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaOverlayID;

/**
 * Contact information of a Gia peer in the overlay network.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaOverlayContact extends GnutellaLikeOverlayContact {

	private int capacity;

	/**
	 * @param id
	 * @param transInfo
	 * @param isUltrapeer
	 * @param capacity
	 */
	public GiaOverlayContact(GnutellaOverlayID id, TransInfo transInfo,
			int capacity) {
		super(id, transInfo);
		this.capacity = capacity;
	}
	
	/**
	 * Returns the Gia capacity assigned to this node
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns a new comparator for comparing the node's capacities.
	 * @return
	 */
	public static Comparator<GiaOverlayContact> getCapacityComparator() {
		return new Comparator<GiaOverlayContact>() {

			@Override
			public int compare(GiaOverlayContact o1, GiaOverlayContact o2) {
				return o1.getCapacity() - o2.getCapacity();
				
			}
			
		};
	}

}
