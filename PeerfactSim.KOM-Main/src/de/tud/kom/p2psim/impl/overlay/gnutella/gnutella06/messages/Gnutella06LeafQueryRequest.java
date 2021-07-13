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

import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;

/**
 * This message is sent by a leaf to request an ultrapeer to start a query for it.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Gnutella06LeafQueryRequest extends AbstractGnutellaMessage {

	private Query q;
	private Gnutella06OverlayContact requester;
	private int hitsWanted;

	/**
	 * Creates a new leaf query request.
	 * @param q: the query that shall be made
	 * @param requester: the leaf that sent the request
	 */
	public Gnutella06LeafQueryRequest(Query q, int hitsWanted, Gnutella06OverlayContact requester) {
		this.q = q;
		this.requester = requester;
		this.hitsWanted = hitsWanted;
	}
	
	/**
	 * Returns the query encapsulated into this message.
	 * @return
	 */
	public Query getQuery() {
		return q;
	}
	
	public int getHitsWanted() {
		return hitsWanted;
	}
	
	/**
	 * Returns the initiator of this query request.
	 * @return
	 */
	public Gnutella06OverlayContact getRequester() {
		return requester;
	}
	
	@Override
	public long getGnutellaPayloadSize() {
		return q.getSize() + requester.getSize() + 2;
	}
	
	public String toString() {
		return "LEAF_QUERY_REQUEST: query=" + q + ", requestor=" + requester;
	}

}
