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


import de.tu.darmstadt.dvs.nSense.overlay.NSenseID;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants.MSG_TYPE;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseID;

/**
 * This class represent the sensor request message in the pSense. It contains:
 * <ul>
 * <li>maximal hops for this message in the overlay</li>
 * <li>a sequence number to distinguish old and new information</li>
 * <li>the vision range radius of the sender</li>
 * <li>the position of the sender</li>
 * <li>an identifier for the message type</li>
 * <li>a sectorID, for the requested sector</li>
 * <li>the {@link PSenseID} of the originator of this content</li>
 * </ul>
 * This message will be send to all <b>sensor nodes</b> of the local node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class SensorRequestMsg extends AbstractNSenseMsg {

	/**
	 * The sector identifier for this request.
	 */
	private final byte sectorID;

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
	 * @param visionRangeRadius
	 *            The vision range radius of the sender
	 * @param position
	 *            The position of the sender
	 * @param sectorID
	 *            The requested sector id
	 */
	public SensorRequestMsg(NSenseID sender, byte hopCount,
			SequenceNumber sequenceNr, int visionRangeRadius, VectorN position,VectorN positionDimensions,
			byte sectorID) {
		super(hopCount, sequenceNr, visionRangeRadius, position,positionDimensions,
				MSG_TYPE.SENSOR_REQUEST);
		this.sectorID = sectorID;
		senderID = sender;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMessage + sizeOfSectorID + sizeOfSenderID
		return getSizeOfAbstractMessage() + Constants.BYTE_SIZE_OF_SECTOR_ID
				+ senderID.getTransmissionSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the sector identifier for this request.
	 * 
	 * @return the sectorID for that is requested
	 */
	public byte getSectorID() {
		return sectorID;
	}

	/**
	 * Gets the {@link NSenseID} of the originator from the content.
	 * 
	 * @return Gets the pSenseID of the sender.
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
		temp.append(", sectorID: ");
		temp.append(getSectorID());
		temp.append(" ]");
		return temp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SensorRequestMsg) {
			SensorRequestMsg o = (SensorRequestMsg) obj;

			return super.equals(o)
					&& this.sectorID == o.sectorID
					&& (this.senderID == o.senderID || (this.senderID != null && this.senderID
							.equals(o.senderID)));
		}
		return false;
	}
}
