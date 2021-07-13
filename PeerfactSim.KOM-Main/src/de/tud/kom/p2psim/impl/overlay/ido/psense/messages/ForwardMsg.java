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
import java.util.List;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseContact;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseID;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.Constants.MSG_TYPE;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.SequenceNumber;

/**
 * This class represent the forward message in the pSense. It contains:
 * <ul>
 * <li>the remaining hops for this message in the overlay</li>
 * <li>an identifier for the message type</li>
 * <li>the sequence number from message of the originator</li>
 * <li>the vision range radius from the originator of the message, that is
 * forwarded</li>
 * <li>the position from the originator of the message, that is forwarded</li>
 * <li>a list of receivers, who has gets this information</li>
 * <li>the {@link PSenseContact} information of the source of the message, that
 * is to forward. It contains the PSenseID of the originator</li>
 * </ul>
 * This message will be send to nodes, that are not in the receiver list of the
 * source message and is a near node for the originator of content.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 * 
 */
public class ForwardMsg extends AbstractPositionUpdateMsg {

	/**
	 * Contact information of the originator of the content of the forwarded
	 * message.
	 */
	private final PSenseContact contact;

	/**
	 * Constructor of the forward message. It sets the attributes of this class
	 * with the given parameters.
	 * 
	 * @param hopCount
	 *            The remaining hops for the content of the message
	 * @param sequenceNr
	 *            The sequence number from message of the originator
	 * @param receiversList
	 *            The receivers list, who has gets this information
	 * @param visionRangeRadius
	 *            The vision range radius from the originator of the message,
	 *            that is forwarded
	 * @param position
	 *            The position from the originator of the message, that is
	 *            forwarded
	 * @param contact
	 *            The contact information of the originator of the message, that
	 *            is forwarded
	 */
	public ForwardMsg(byte hopCount, SequenceNumber sequenceNr,
			List<PSenseID> receiversList, int visionRangeRadius,
			Point position, PSenseContact contact) {
		super(hopCount, sequenceNr, receiversList, visionRangeRadius, position,
				MSG_TYPE.FORWORD);
		this.contact = contact;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMessage + sizeOfContact
		return getSizeOfAbstractMessage() + contact.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the contact information of the originator of the content from the
	 * message.
	 * 
	 * @return the contact information
	 */
	public PSenseContact getContact() {
		return contact;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", hopCount: ");
		temp.append(getHopCount());
		temp.append(", sequenceNumber: ");
		temp.append(getSequenceNr());
		temp.append(", Position: ");
		temp.append(getPosition());
		temp.append(", VisionRange: ");
		temp.append(getRadius());
		temp.append(", ContactofOriginator: ");
		temp.append(getContact());
		temp.append(", ReceiversList: ");
		temp.append(getReceiversList());
		temp.append(" ]");
		return temp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ForwardMsg) {
			ForwardMsg o = (ForwardMsg) obj;

			return super.equals(o)
					&& (this.contact == o.contact || (this.contact != null && this.contact
							.equals(o.contact)));
		}
		return false;
	}

}
