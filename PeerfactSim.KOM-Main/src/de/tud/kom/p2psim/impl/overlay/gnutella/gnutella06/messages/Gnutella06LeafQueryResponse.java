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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.messages;

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;

/**
 * Sent to a leaf as a response to a query request the leaf has made. Carries
 * the results.
 * 
 * @author Leo Nobach  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Gnutella06LeafQueryResponse extends AbstractGnutellaMessage {

	public List<QueryHit> queryHits;

	private int queryUID;

	/**
	 * Creates a response message with the list of query hits received and
	 * the UID of the query.
	 * @param queryHits
	 * @param queryUID
	 */
	public Gnutella06LeafQueryResponse(List<QueryHit> queryHits, int queryUID) {
		this.queryHits = queryHits;
		this.queryUID = queryUID;
	}

	/**
	 * Returns a list of query hits the ultrapeer has received for the leaf's query.
	 * @return
	 */
	public List<QueryHit> getQueryHits() {
		return queryHits;
	}

	/**
	 * Returns the UID of the query that led to the results.
	 * @return
	 */
	public int getQueryUID() {
		return queryUID;
	}

	@Override
	public long getGnutellaPayloadSize() {
		int size = 4;
		for (QueryHit qHit : queryHits) {
			size += qHit.getSize();
		}

		return size;
	}

	public String toString() {
		return "LEAF_QUERY_RESPONSE: hits=" + queryHits + ", uid=" + queryUID;
	}

}
