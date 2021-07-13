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


package de.tud.kom.p2psim.impl.overlay.gnutella.api;

import java.util.Collection;

/**
 * A QueryHit is sent to a querying node if a resource was found matching
 * its query. The QueryHit containts information about the node that holds
 * the resource and the number of resources this node holds that match the query.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class QueryHit<TContact extends GnutellaLikeOverlayContact> {

	TContact c;

	/**
	 * Creates a new QueryHit.
	 * @param c: the node that holds the resources matching the query.
	 * @param numberOfHits: the number of hits that match the given query.
	 */
	public QueryHit(TContact c, int numberOfHits) {
		super();
		this.c = c;
		this.numberOfHits = numberOfHits;
	}

	/**
	 * Returns the contact that holds the resources matching the query.
	 * @return
	 */
	public TContact getContact() {
		return c;
	}

	public String toString() {
		return "(QHit: " + numberOfHits + " at " + c + ")";
	}

	/**
	 * Returns the number of hits that match the given query.
	 * @return
	 */
	public int getNumberOfHits() {
		return numberOfHits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + numberOfHits;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryHit other = (QueryHit) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (numberOfHits != other.numberOfHits)
			return false;
		return true;
	}

	int numberOfHits;

	/**
	 * Returns the transport representation size of this QueryHit in bytes.
	 * @return
	 */
	public long getSize() {
		return 5 + c.getSize();
	}

	/**
	 * Returns the total number of matches that have been discovered with 
	 * <b>result</b>.
	 * @param result
	 * @return
	 */
	public static int getTotalHits(Collection<? extends QueryHit> result) {
		if (result == null)
			return 0;
		int i = 0;
		for (QueryHit q : result) {
			i += q.getNumberOfHits();
		}
		return i;
	}

}
