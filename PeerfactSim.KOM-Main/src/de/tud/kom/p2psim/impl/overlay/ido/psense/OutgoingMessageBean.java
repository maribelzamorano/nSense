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

package de.tud.kom.p2psim.impl.overlay.ido.psense;

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.AbstractPSenseMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.PositionUpdateMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.SensorRequestMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.SensorResponseMsg;

/**
 * Combine the message, the receiverList and the contact information to the
 * receiver of the message. <br>
 * The given receiverList should have the same Pointer as the List in message.
 * For {@link SensorRequestMsg} and {@link SensorResponseMsg}, should this
 * <code>null</code> or an empty list. This has the advantage, that a message
 * must be delete, that the receiver stay up to date.
 * 
 * @author Christoph Münker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class OutgoingMessageBean {

	/**
	 * The contact information to the receiver of the content of this stored
	 * message.
	 */
	private final PSenseContact contact;

	/**
	 * List of receivers to this message
	 */
	private final List<PSenseID> receivers;

	/**
	 * The message that is to store.
	 */
	private final AbstractPSenseMsg msg;

	/**
	 * Constructor of this class, it sets the attributes of this class with the
	 * given parameters.
	 * 
	 * @param contact
	 *            The contact information from the receiver of the content of
	 *            this message.
	 * @param receivers
	 *            A list of the receivers of this message. (only used for
	 *            {@link PositionUpdateMsg}s and {@link ForwardMsg}s. For other
	 *            message should be used <code>null</code> or an empty list.
	 * @param msg
	 *            The message that is to store.
	 */
	public OutgoingMessageBean(PSenseContact contact, List<PSenseID> receivers,
			AbstractPSenseMsg msg) {
		this.contact = contact;
		this.msg = msg;
		this.receivers = receivers;
	}

	/**
	 * Gets the contact from the receiver of the content of the message
	 * 
	 * @return The contact to the receiver of the content of the message
	 */
	public PSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets a list of receivers of this message
	 * 
	 * @return list of receivers of this message
	 */
	public List<PSenseID> getReceivers() {
		return receivers;
	}

	/**
	 * Gets the message, that is stored in this bean
	 * 
	 * @return The message, that is stored in this bean
	 */
	public AbstractPSenseMsg getMessage() {
		return msg;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OutgoingMessageBean) {
			OutgoingMessageBean o = (OutgoingMessageBean) obj;
			return (this.contact == o.contact || (this.contact != null && this.contact
					.equals(o.contact)))
					&& (this.receivers == o.receivers || (this.receivers != null && this.receivers
							.equals(o.receivers)))
					&& (this.msg == o.msg || (this.msg != null && this.msg
							.equals(o.msg)));
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ contact: ");
		temp.append(getContact());
		temp.append(", msg: ");
		temp.append(getMessage());
		temp.append(", receivers: ");
		temp.append(getReceivers());
		temp.append(" ]");
		return temp.toString();
	}
}
