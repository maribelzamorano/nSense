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

package de.tud.kom.p2psim.impl.service.mercury;

import de.tud.kom.p2psim.api.overlay.Transmitable;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Information needed to contact another MercuryService, including attribute
 * range this node is responsible for. This Information is transmitted within a
 * MercuryMessage
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryContact implements Transmitable {

	private String attributeName = null;

	private TransInfo transInfo = null;

	private Comparable[] range;


	public MercuryContact(String attributeName, TransInfo transInfo,
			Comparable[] range) {
		this.attributeName = attributeName;
		this.transInfo = transInfo;
		this.range = range;
	}

	/**
	 * get the name of the Attribute this node is responsible for
	 * 
	 * @return
	 */
	public String getAttribute() {
		return attributeName;
	}

	/**
	 * get this nodes TransInfo
	 * 
	 * @return
	 */
	public TransInfo getTransInfo() {
		return transInfo;
	}

	/**
	 * Get min and max value of the attribute range this node is responsible for
	 * 
	 * @return
	 */
	public Comparable[] getRange() {
		return range;
	}

	/**
	 * update Range
	 * 
	 * @param range
	 */
	public void setRange(Comparable rangeMin, Comparable rangeMax) {
		if (rangeMin != null) {
			this.range[0] = rangeMin;
		}
		if (rangeMax != null) {
			this.range[1] = rangeMax;
		}
	}

	@Override
	public int getTransmissionSize() {
		// TODO generic way for TransInfo-Size instead of 5?
		return attributeName.getBytes().length + 6;
	}

	/**
	 * Equals does not take into account the range of this contact, as this
	 * information might change over time and should be updated rather than
	 * force a new Contact
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MercuryContact) {
			MercuryContact contact = (MercuryContact) obj;
			return contact.getAttribute().equals(this.getAttribute())
					&& contact.getTransInfo().equals(this.getTransInfo());
		}
		return false;
	}

	@Override
	public String toString() {
		return "MC: " + attributeName + " " + range[0] + "-" + range[1] + " IP"
				+ getTransInfo().getNetId().toString();
	}

}
