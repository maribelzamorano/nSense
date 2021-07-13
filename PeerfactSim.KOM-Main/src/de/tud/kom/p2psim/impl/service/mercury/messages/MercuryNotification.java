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

package de.tud.kom.p2psim.impl.service.mercury.messages;

import java.util.List;

import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;

/**
 * Response to a subscription
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryNotification extends AbstractMercuryMessage {

	private List<IMercuryAttribute> attributes = null;
	
	private MercuryContact origin = null;

	private MercuryContact rendevouzPoint = null;
	
	public MercuryNotification(List<IMercuryAttribute> attributes,
			MercuryContact origin, MercuryContact rendevouzPoint) {
		super();
		this.attributes = attributes;
		this.rendevouzPoint = rendevouzPoint;
		this.origin = origin;
	}
	
	public List<IMercuryAttribute> getAttributes() {
		return this.attributes;
	}

	public MercuryContact getOrigin() {
		return this.origin;
	}

	public MercuryContact getRendevouzPoint() {
		return this.rendevouzPoint;
	}

	@Override
	public String toString() {
		return "Notification [" + getSeqNr() + "] Origin: " + origin.toString()
				+ " attr: "
				+ attributes.toString();
	}

	@Override
	public long getSize() {
		int size = 0;
		for (IMercuryAttribute attr : attributes) {
			size += attr.getTransmissionSize();
		}
		return super.getSize() + size + origin.getTransmissionSize()
				+ rendevouzPoint.getTransmissionSize();
	}

}
