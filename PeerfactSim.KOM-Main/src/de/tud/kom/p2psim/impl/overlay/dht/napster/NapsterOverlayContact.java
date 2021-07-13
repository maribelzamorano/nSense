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


package de.tud.kom.p2psim.impl.overlay.dht.napster;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterOverlayContact implements OverlayContact<NapsterOverlayID>,
		Comparable<NapsterOverlayContact> {

	private TransInfo transInfo;

	private NapsterOverlayID overlayID;

	public NapsterOverlayContact(NapsterOverlayID overlayID, TransInfo transInfo) {
		this.transInfo = transInfo;
		this.overlayID = overlayID;
	}

	public NapsterOverlayID getOverlayID() {
		return overlayID;
	}

	public TransInfo getTransInfo() {
		return transInfo;
	}

	public int compareTo(NapsterOverlayContact o) {
		return overlayID.compareTo(o.getOverlayID());
	}

	public void setOverlayID(NapsterOverlayID overlayID) {
		this.overlayID = overlayID;
	}

	public void setTransInfo(TransInfo transInfo) {
		this.transInfo = transInfo;
	}

	public String toString() {
		String ret = "[oid=" + overlayID.getID() + " -> ip="
				+ transInfo.getNetId() + "]";
		return ret;
	}

}
