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

import java.util.List;
import java.util.Vector;

import de.tu.darmstadt.dvs.nSense.application.IDONodeInfoNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class stores the information to a node.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 * 
 */
public class NSenseNodeInfo implements IDONodeInfoNDim {

	/**
	 * Describe the AOI radius
	 */
	private final int visionRangeRadius;

	private final NSenseContact contact;

	private final long lastUpdate;

	private final SequenceNumber sequenceNr;

	private List<NSenseID> receiversList;

	private byte hops;

	private final VectorN position;
	
	private final VectorN positionDimensions;


	public NSenseNodeInfo(int visionRangeRadius, VectorN position, VectorN positionDimensions,
			NSenseContact contact, SequenceNumber sequenceNr,
			List<NSenseID> receiversList, byte hops) {
		this.visionRangeRadius = visionRangeRadius;
		this.position = position;
		this.positionDimensions=positionDimensions;
		this.contact = contact;
		this.lastUpdate = Simulator.getCurrentTime();
		this.sequenceNr = sequenceNr;
		this.hops = hops;
		if (receiversList == null)
			this.receiversList = new Vector<NSenseID>();
		else
			this.receiversList = receiversList;
	}

	/**
	 * The same methode, how getAoiRadius()
	 * 
	 * @return Return the vision range radius of the node or rather the AOI
	 */
	public int getVisionRangeRadius() {
		return visionRangeRadius;
	}

	public NSenseContact getContact() {
		return contact;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public SequenceNumber getSequenceNr() {
		return sequenceNr;
	}

	public List<NSenseID> getReceiversList() {
		return receiversList;
	}

	public byte getHops() {
		return hops;
	}

	public void updateReceiversList(List<NSenseID> moreReceivers) {
		for (NSenseID id : moreReceivers) {
			if (!receiversList.contains(id)) {
				receiversList.add(id);
			}
		}
	}

	@Override
	public VectorN getPosition() {
		return position;
	}

	@Override
	public OverlayID getID() {
		return contact.getOverlayID();
	}

	/**
	 * The same method, like getVisionRangeRadius()
	 * 
	 * @return the AOI radius
	 */
	@Override
	public int getAoiRadius() {
		return visionRangeRadius;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ contact: ");
		temp.append(getContact());
		temp.append(", hops: ");
		temp.append(getHops());
		temp.append(", lastUpdate: ");
		temp.append(getLastUpdate());
		temp.append(", Position: ");
		temp.append(getPosition());
		temp.append(", PositionDimensions: ");
		temp.append(getPositionDimensions());
		temp.append(", receiversList: ");
		temp.append(getReceiversList());
		temp.append(", sequenceNr: ");
		temp.append(getSequenceNr());
		temp.append(", VisionRangeRadius: ");
		temp.append(getVisionRangeRadius());
		temp.append(" ]");
		return temp.toString();
	}

	/**
	 * @return the positionDimensions
	 */
	@Override
	public VectorN getPositionDimensions() {
		return positionDimensions;
	}

}
