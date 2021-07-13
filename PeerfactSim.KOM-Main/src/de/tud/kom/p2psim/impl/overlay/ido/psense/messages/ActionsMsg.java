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

package de.tud.kom.p2psim.impl.overlay.ido.psense.messages;

import java.awt.Point;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.Constants.MSG_TYPE;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.SequenceNumber;

/**
 * This class describe an abstract action message. It has only payload as data,
 * to simulate traffic for an action message in a game.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class ActionsMsg extends AbstractPSenseMsg {

	/**
	 * The payload size for the abstract action message
	 */
	private int actionSize;

	/**
	 * Constructor of the abstract message. It sets the attributes of this
	 * class.
	 * 
	 * @param hopCount
	 *            The number of allowing hops in the overlay for this message.
	 * @param sequenceNr
	 *            A consecutively number to distinguish old and new messages.
	 * @param radius
	 *            The radius for the message.
	 * @param position
	 *            The position.
	 * @param actionSize
	 *            The payload size for the abstract action message
	 */
	public ActionsMsg(byte hopCount, SequenceNumber sequenceNr, int radius,
			Point position, int actionSize) {
		super(hopCount, sequenceNr, radius, position, MSG_TYPE.ACTION_MSG);
		this.actionSize = actionSize;
	}

	@Override
	public long getSize() {
		return super.getSizeOfAbstractMessage() + actionSize;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Returns the size of the action
	 * 
	 * @return the action size
	 */
	public int getActionSize() {
		return actionSize;
	}
}
