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

package de.tud.kom.p2psim.impl.overlay.ido.von;

import java.awt.Point;

import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class is used to encapsulate the important node data which has to be
 * send within various messages.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonNodeInfo implements IDONodeInfo, Transmitable {

	private final VonContact contact;

	private final Point position;

	private final int aoiRadius;

	public VonNodeInfo(VonContact contact, Point position, int aoiRadius) {
		this.contact = contact;
		this.position = position;
		this.aoiRadius = aoiRadius;
	}

	public VonContact getContact() {
		return contact;
	}

	public int getAoiRadius() {
		return aoiRadius;
	}

	public int getTransmissionSize() {
		// size = sizeOfVonContact + sizeOfPoint + sizeOfAOI = sizeOfVonContact
		// + (2*sizeOfInt) + sizeOfInt = sizeOfVonContact + 8byte + 4byte =
		// sizeOfVonContact + 12byte
		return contact.getTransmissionSize() + 12;
	}

	@Override
	public String toString() {
		return "[contact=" + contact + " position=" + position + " aoiRadius="
				+ aoiRadius + "]";
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public OverlayID getID() {
		return contact.getOverlayID();
	}
}
