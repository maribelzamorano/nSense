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

package de.tu.darmstadt.dvs.nSense.overlay.messages;

import java.util.List;

import de.tu.darmstadt.dvs.nSense.overlay.NSenseID;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants.MSG_TYPE;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.Message;

/**
 * This class represent the position update message in the pSense. It contains:
 * <ul>
 * <li>maximal hops for this message in the overlay</li>
 * <li>a sequence number to distinguish old and new information</li>
 * <li>the vision range radius of the sender</li>
 * <li>the position of the sender</li>
 * <li>a list of receivers, who gets this information</li>
 * <li>an identifier for the message type</li>
 * <li>the {@link NSenseID} of the originator of this content</li>
 * </ul>
 * This message will be send to all <b>near nodes</b> of the local node.
 * 
 * @author Christoph Münker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 * 
 */
public class PositionUpdateMsg extends AbstractPositionUpdateMsg {

	/**
	 * The {@link NSenseID} of the originator of this content
	 */
	private final NSenseID senderID;

	/**
	 * Constructor of this class. It sets the attributes with the given
	 * parameters.
	 * 
	 * @param sender
	 *            The nSenseID of the sender.
	 * @param hopCount
	 *            The maximal hops for this information.
	 * @param sequenceNr
	 *            The actual sequence number for this round of the sender.
	 * @param receiversList
	 *            The receiver list of this information
	 * @param visionRangeRadius
	 *            The vision range radius of the sender
	 * @param position
	 *            The position of the sender
	 */
	public PositionUpdateMsg(NSenseID sender, byte hopCount,
			SequenceNumber sequenceNr, List<NSenseID> receiversList,
			int visionRangeRadius, VectorN position, VectorN positionDimensions) {
		super(hopCount, sequenceNr, receiversList, visionRangeRadius, position,positionDimensions,
				MSG_TYPE.POSITION_UPDATE);
		senderID = sender;
	}

	@Override
	public long getSize() {
		return getSizeOfAbstractMessage() + senderID.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the {@link NSenseID} of the originator from the content.
	 * 
	 * @return Gets the nSenseID of the sender.
	 */
	public NSenseID getSenderID() {
		return senderID;
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
		temp.append(", PositionDimensions: ");
		temp.append(getPositionDimensions());
		temp.append(", VisionRange: ");
		temp.append(getRadius());
		temp.append(", senderID: ");
		temp.append(getSenderID());
		temp.append(", ReceiversList: ");
		temp.append(getReceiversList());
		temp.append(" ]");
		return temp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PositionUpdateMsg) {
			PositionUpdateMsg o = (PositionUpdateMsg) obj;

			return super.equals(o)
					&& (this.senderID == o.senderID || (this.senderID != null && this.senderID
							.equals(o.senderID)));
		}
		return false;
	}

}
