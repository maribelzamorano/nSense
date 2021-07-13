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
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.ERROR_TYPES;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.MSG_TYPE;

/**
 * Error Message from the Server to the Client. It contains a enum from
 * {@link ERROR_TYPES}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ErrorMessage extends CSAbstractMessage {

	/**
	 * The errorType
	 */
	private ERROR_TYPES errorType;

	/**
	 * Sets the errorType. Additionally it sets the MessageType.
	 * 
	 * @param errType
	 *            The errorType of the message
	 */
	public ErrorMessage(ERROR_TYPES errType) {
		super(MSG_TYPE.ERROR_MESSAGE);
		this.errorType = errType;
	}

	@Override
	public long getSize() {
		// abstractMsg + ErrorType = abstractMsg + 1
		return super.getSize() + 1;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the error type of the message from the server back.
	 * 
	 * @return The error type from the server.
	 */
	public ERROR_TYPES getErrorType() {
		return errorType;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ErrorMessage) {
			ErrorMessage e = (ErrorMessage) o;
			return this.errorType.equals(e.errorType) && super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append("[ ErrorType: ");
		temp.append(getErrorType());
		temp.append(" ]");
		return temp.toString();
	}

}
