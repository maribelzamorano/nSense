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

package de.tud.kom.p2psim.impl.overlay.ido.cs;

import java.awt.Point;

import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.util.Transmitable;

/**
 * This class is a container of information of a client. It contains the
 * position, area of interest radius and the id of the client.<br>
 * This class is used to transmit/disseminate information from client to server
 * and server to clients.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 */
public class ClientNodeInfo implements IDONodeInfo, Transmitable {

	/**
	 * The position of the client
	 */
	private Point position;

	/**
	 * The area of interest of the client
	 */
	private int aoi;

	/**
	 * The ID of the client
	 */
	private ClientID clientID;

	public ClientNodeInfo(Point position, int aoi, ClientID clientID) {
		this.position = position;
		this.aoi = aoi;
		this.clientID = clientID;
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public int getAoiRadius() {
		return aoi;
	}

	@Override
	public OverlayID getID() {
		return clientID;
	}

	@Override
	public int getTransmissionSize() {
		// position + aoi + clientID = 2*4Bytes + 4 Bytes + clientID
		return 8 + 4 + clientID.getTransmissionSize();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ClientNodeInfo) {
			ClientNodeInfo c = (ClientNodeInfo) o;
			return this.position.equals(c.position) && this.aoi == c.aoi
					&& this.clientID.equals(c.clientID) && super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ AOI: ");
		temp.append(getAoiRadius());
		temp.append(", position: ");
		temp.append(getPosition());
		temp.append(", clientID: ");
		temp.append(getID());
		temp.append(" ]");
		return temp.toString();
	}

}
