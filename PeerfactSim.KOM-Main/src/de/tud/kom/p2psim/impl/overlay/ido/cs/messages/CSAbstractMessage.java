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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.MSG_TYPE;

/**
 * An abstract Message for the Client/Server IDO-System. It provides the
 * transmit of the {@link MSG_TYPE}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public abstract class CSAbstractMessage implements Message {

	/**
	 * The message type of the message
	 */
	private MSG_TYPE msgType;

	/**
	 * The constructor of the Message. It sets the given msgType.
	 * 
	 * @param msgType
	 */
	public CSAbstractMessage(MSG_TYPE msgType) {
		this.msgType = msgType;
	}

	/**
	 * Gets the type of the message back.
	 * 
	 * @return The type of the message.
	 */
	public MSG_TYPE getMsgType() {
		return this.msgType;
	}

	@Override
	public long getSize() {
		// ein Byte fpr MSG_TYPE
		return 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CSAbstractMessage) {
			CSAbstractMessage j = (CSAbstractMessage) o;
			return this.msgType.equals(j.msgType);
		}
		return false;
	}

}
