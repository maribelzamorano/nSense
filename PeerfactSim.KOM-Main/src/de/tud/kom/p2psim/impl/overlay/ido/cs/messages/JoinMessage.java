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

package de.tud.kom.p2psim.impl.overlay.ido.cs.messages;

import java.awt.Point;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.MSG_TYPE;

/**
 * The Join message from the client to the Server. It contains information of
 * the node, like position and radius of the area of interest.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class JoinMessage extends CSAbstractMessage {
	/**
	 * The position of the client.
	 */
	private Point position;

	/**
	 * The radius of the area of interest of the client.
	 */
	private int aoi;

	/**
	 * Sets the position and area of interest of the client.
	 * 
	 * @param position
	 *            The position of the client.
	 * 
	 * @param aoi
	 *            The radius of the area of interest.
	 */
	public JoinMessage(Point position, int aoi) {
		super(MSG_TYPE.JOIN_MESSAGE);
		this.position = position;
		this.aoi = aoi;
	}

	@Override
	public long getSize() {
		// position + aoi = 2*4 + 4
		return super.getSize() + 8 + 4;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the Position, that is stored in the message from the client.
	 * 
	 * @return The position of the client
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Gets the radius of the area of interest, that is stored in the message
	 * from the client.
	 * 
	 * @return The radius of the area of interest of the client
	 */
	public int getAoi() {
		return aoi;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof JoinMessage) {
			JoinMessage j = (JoinMessage) o;
			return this.position.equals(j.position) && this.aoi == j.aoi
					&& super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", position: ");
		temp.append(getPosition());
		temp.append(", aoi: ");
		temp.append(getAoi());
		temp.append(" ]");
		return temp.toString();
	}

}
