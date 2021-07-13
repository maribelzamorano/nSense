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


package de.tud.kom.p2psim.impl.overlay.gnutella.common.messages;

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;

/**
 * Sent to the initiator of a query by a peer (ultrapeer in Gnutella06) that shares a matching document,
 * either by itself or through one of its leaves. Encapsulates multiple query hits,
 * one for every match in the local content, including the replicated content (QRP, One-hop replication).
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaQueryHit<TContact extends GnutellaLikeOverlayContact> extends AbstractGnutellaMessage {

	private List<QueryHit<TContact>> hits;

	private int queryUID;

	/**
	 * Creates a new query hit message, given the query's identifier and the given
	 * list of query hits.
	 * @param queryUID
	 * @param hits
	 */
	public GnutellaQueryHit(int queryUID, List<QueryHit<TContact>> hits) {
		this.hits = hits;
		this.queryUID = queryUID;
	}

	/**
	 * Returns the UID of the query that caused this query hit message.
	 * @return
	 */
	public int getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the query hits that are encapsulated in this message.
	 * @return
	 */
	public List<QueryHit<TContact>> getQueryHits() {
		return hits;
	}

	@Override
	public long getGnutellaPayloadSize() {
		int size = 4;
		for (QueryHit hit : hits) {
			size += hit.getSize();
		}
		return size;
	}

	public String toString() {
		return "QUERY_HIT: queryUID=" + queryUID + ", hits=" + hits;
	}

}
