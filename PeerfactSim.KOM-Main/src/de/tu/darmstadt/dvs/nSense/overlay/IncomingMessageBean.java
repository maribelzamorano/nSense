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

package de.tu.darmstadt.dvs.nSense.overlay;

import de.tu.darmstadt.dvs.nSense.overlay.messages.AbstractNSenseMsg;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;

/**
 * Combine the message and the contact information from the originator of the
 * content of the message.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 */
public class IncomingMessageBean {

	/**
	 * The contact information from the originator of the content of this stored
	 * message.
	 */
	private final NSenseContact contact;

	/**
	 * The message that is to store.
	 */
	private final AbstractNSenseMsg msg;

	/**
	 * Constructor of this class, it sets the attributes of this class with the
	 * given parameters.
	 * 
	 * @param contact
	 *            The contact information from the originator of the content of
	 *            this message.
	 * @param msg
	 *            The message that is to store.
	 */
	public IncomingMessageBean(NSenseContact contact, AbstractNSenseMsg msg) {
		this.contact = contact;
		this.msg = msg;
	}

	/**
	 * Gets the contact from the originator of the content of the message
	 * 
	 * @return The contact to the originator of the content of the message
	 */
	public NSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets the message, that is stored in this bean
	 * 
	 * @return The message, that is stored in this bean
	 */
	public AbstractNSenseMsg getMessage() {
		return msg;
	}

	/**
	 * Gets the sequence number of the stored message ({@link #msg})
	 * 
	 * @return The sequence number of the stored message
	 */
	public SequenceNumber getSeqNr() {
		return msg.getSequenceNr();
	}

	@Override
	public String toString() {
		return "[ " + contact.toString() + ", " + msg.toString() + " ]";
	}
}
