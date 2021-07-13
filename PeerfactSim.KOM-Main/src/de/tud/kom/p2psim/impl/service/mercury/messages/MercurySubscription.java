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
import java.util.Vector;

import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;
import de.tud.kom.p2psim.impl.service.mercury.filter.IMercuryFilter;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * A subscription, acts as a compound filter for MercuryAttributes
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercurySubscription extends AbstractMercuryMessage {

	private List<IMercuryFilter> filters;

	private MercuryContact origin = null;

	private long validUntil = 0;

	public MercurySubscription(MercuryContact origin) {
		super();
		this.origin = origin;
		this.filters = new Vector<IMercuryFilter>();
	}
	
	public void setValidUntil(long validUntil) {
		this.validUntil = validUntil;
	}

	public void addFilter(IMercuryFilter filter) {
		if (filter != null)
			this.filters.add(filter);
	}
	
	public void addFilters(List<IMercuryFilter> filters) {
		this.filters.addAll(filters);
	}

	public List<IMercuryFilter> getFilters() {
		return this.filters;
	}

	/**
	 * is this Subscription still valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (validUntil == 0) {
			return true;
		} else {
			return Simulator.getCurrentTime() < this.validUntil;
		}
	}

	public long getValidUntil() {
		return this.validUntil;
	}

	/**
	 * Match a publication with all corresponding filters of this subscription
	 * @param attribute
	 * @return
	 */
	public boolean match(MercuryPublication publication) {
		boolean match = true;
		boolean first = true;
		for (IMercuryFilter filter : filters) {
			boolean filterMatched = false;
			for (IMercuryAttribute attribute : publication.getAttributes()) {
				if (attribute.getName().equals(filter.getName()) &&
						attribute.getType().equals(filter.getType())) {
					if (first) {
						first = !first;
						match = filter.match(attribute);
					} else {
						match = match && filter.match(attribute);
					}
					filterMatched = true;
				}
			}
			if (!filterMatched) {
				return false;
			}
		}
		return match;
	}

	public MercuryContact getOrigin() {
		return this.origin;
	}

	@Override
	public String toString() {
		return "Sub [" + getSeqNr() + "] from " + origin.toString()
				+ " Filter: " + filters.toString() + " Valid: " + isValid();
	}

	@Override
	public long getSize() {
		int size = 0;
		for (IMercuryFilter filter : filters) {
			size += filter.getTransmissionSize();
		}
		return super.getSize() + getOrigin().getTransmissionSize() + size;
	}

}
