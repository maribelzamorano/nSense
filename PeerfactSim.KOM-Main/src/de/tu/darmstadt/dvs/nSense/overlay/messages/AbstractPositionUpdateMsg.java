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
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.overlay.NSenseID;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants.MSG_TYPE;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * That class abstracts the message of the a position update. It adds only one
 * attribute {@link #receiversList} to the {@link AbstractNSenseMsg}. This
 * attribute is used for a normal position update message and a forwarded
 * position update.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public abstract class AbstractPositionUpdateMsg extends AbstractNSenseMsg {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(AbstractNSenseMsg.class);

	/**
	 * A list of receivers, which has get the information of this message
	 */
	private List<NSenseID> receiversList;

	/**
	 * Constructor of the abstract message. It sets the attributes of this
	 * class.
	 * 
	 * @param hopCount
	 *            The number of allowing hops in the overlay for this message.
	 * @param sequenceNr
	 *            A consecutively number to distinguish old and new messages.
	 * @param receiversList
	 *            A list of receivers, which gets the message.
	 * @param visionRangeRadius
	 *            The vision range radius.
	 * @param position
	 *            The position.
	 * @param msgType
	 *            To distinguish the type of the message. The values are in the
	 *            {@link Constants} class and start with <code>MSG_*</code>.
	 */
	public AbstractPositionUpdateMsg(byte hopCount, SequenceNumber sequenceNr,
			List<NSenseID> receiversList, int visionRangeRadius,
			VectorN position, VectorN positionDimensions, MSG_TYPE msgType) {
		super(hopCount, sequenceNr, visionRangeRadius, position,
				positionDimensions, msgType);

		this.receiversList = receiversList;
	}

	@Override
	public int getSizeOfAbstractMessage() {
		int size = 0;
		size += super.getSizeOfAbstractMessage();
		if (receiversList != null && receiversList.size() > 0) {
			// the size of the list represents the number of receivers
			// all receivers has the same TransmissionSize.
			size += receiversList.size()
					* receiversList.get(0).getTransmissionSize();
		}
		return size;
	}

	/**
	 * Gets a copy of list of receivers back.
	 * 
	 * @return A list of receivers. If {@link #receiversList} <code>null</code>,
	 *         then return a empty List.
	 */
	public List<NSenseID> getReceiversList() {
		List<NSenseID> copyReceiversList = new Vector<NSenseID>();
		if (receiversList != null)
			copyReceiversList.addAll(receiversList);
		return copyReceiversList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPositionUpdateMsg) {
			AbstractPositionUpdateMsg o = (AbstractPositionUpdateMsg) obj;

			return super.equals(o)
					&& (this.receiversList == o.receiversList || (this.receiversList != null && this.receiversList
							.equals(o.receiversList)));
		}
		return false;
	}
}
