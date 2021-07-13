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



package de.tud.kom.p2psim.impl.overlay;

import de.tud.kom.p2psim.api.overlay.OverlayID;

/**
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class DefaultOverlayID implements OverlayID<Integer> {
	protected Integer myId;

	public byte[] myBytes;

	public DefaultOverlayID(Integer id) {
		myId = id;
		myBytes = myId.toString().getBytes();
	}

	public DefaultOverlayID(String id) {
		myId = Integer.parseInt(id);
		myBytes = id.getBytes();
	}

	public Integer getUniqueValue() {
		return myId;
	}

	public boolean equals(Object theOther) {
		if (theOther == this)
			return true;
		if (!(theOther instanceof OverlayID))
			return false;
		OverlayID compId = (OverlayID) theOther;
		if (((Integer) compId.getUniqueValue()).intValue() == myId.intValue())
			return true;
		return false;
	}

	public int hashCode() {
		return myId.hashCode();
	}

	public String toString() {
		return myId.toString();
	}

	public byte[] getBytes() {
		return myBytes;
	}

	public int compareTo(OverlayID o) {
		DefaultOverlayID gId = (DefaultOverlayID) o;
		if (gId.myId.intValue() == myId.intValue())
			return 0;
		else if ((myId.intValue() - gId.myId.intValue()) > 0)
			return 1;
		else
			return -1;
	}
}
