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


import de.tu.darmstadt.dvs.nSense.overlay.NSenseContact;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants.MSG_TYPE;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.Message;

/**
 * This class represent the sensor response message in the nSense. It contains:
 * <ul>
 * <li>maximal hops of this information in the overlay</li>
 * <li>a sequence number to distinguish old and new information</li>
 * <li>the vision range radius of the new sensor node</li>
 * <li>the position of the new sensor node</li>
 * <li>an identifier for the message type</li>
 * <li>a sectorID, for the sector which was requested</li>
 * <li>contact information, because it may be a new sensor node</li>
 * </ul>
 * This message will be send to the node, which has send a
 * {@link SensorRequestMsg}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class SensorResponseMsg extends AbstractNSenseMsg {

	/**
	 * The sector identifier for this response.
	 */
	private final byte sectorID;

	/**
	 * Contact information, because it may be a new sensor node
	 */
	private final NSenseContact contact;

	/**
	 * The sequence number of the request message, to distinguish old and new
	 * response messages for the same sector.
	 */
	private final SequenceNumber sequenceNrRequest;

	/**
	 * Constructor of this class. It sets the attributes with the given
	 * parameters.
	 * 
	 * @param hopCount
	 *            The maximal hops for this information
	 * @param sequenceNr
	 *            The sequence number of this information
	 * @param visionRangeRadius
	 *            The vision range radius of the next sensor node for this
	 *            sector
	 * @param position
	 *            The position of the next sensor node for this sector
	 * @param sectorID
	 *            The identifier for which sector is response
	 * @param contact
	 *            The contact information for the next sensor node
	 * @param sequenceNrRequest
	 *            The sequence number of the corresponding
	 *            {@link SensorRequestMsg}
	 */
	public SensorResponseMsg(byte hopCount, SequenceNumber sequenceNr,
			int visionRangeRadius, VectorN position, VectorN positionDimensions, byte sectorID,
			NSenseContact contact, SequenceNumber sequenceNrRequest) {
		super(hopCount, sequenceNr, visionRangeRadius, position,positionDimensions,
				MSG_TYPE.SENSOR_RESPONSE);
		this.sectorID = sectorID;
		this.contact = contact;
		this.sequenceNrRequest = sequenceNrRequest;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMessage + sizeOfSectorID + sizeOfPSenseContact +
		// sequenceNrRequest
		return getSizeOfAbstractMessage() + Constants.BYTE_SIZE_OF_SECTOR_ID
				+ contact.getTransmissionSize() + Constants.BYTE_SIZE_OF_SEQ_NR;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * Gets the sector identifier for this response.
	 * 
	 * @return the sectorID for which is this response
	 */
	public byte getSectorID() {
		return sectorID;
	}

	/**
	 * Gets the contact information to the given content.
	 * 
	 * @return the contact information
	 */
	public NSenseContact getContact() {
		return contact;
	}

	/**
	 * Gets the sequence number of the corresponding request
	 * 
	 * @return sequence number of the corresponding request
	 */
	public SequenceNumber getSequenceNrRequest() {
		return sequenceNrRequest;
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
		temp.append(", ContactofOriginator: ");
		temp.append(getContact());
		temp.append(", sectorID: ");
		temp.append(getSectorID());
		temp.append(", sequenceNumberRequest: ");
		temp.append(getSequenceNrRequest());
		temp.append(" ]");
		return temp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SensorResponseMsg) {
			SensorResponseMsg o = (SensorResponseMsg) obj;

			return super.equals(o)
					&& (this.contact == o.contact || (this.contact != null && this.contact
							.equals(o.contact)))
					&& this.sectorID == o.sectorID
					&& (this.sequenceNrRequest == o.sequenceNrRequest || (this.sequenceNrRequest != null && this.sequenceNrRequest
							.equals(o.sequenceNrRequest)));
		}
		return false;
	}

}
