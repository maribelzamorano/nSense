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
 * This message relays a query from an ultrapeer to its neighbors.
 * Instead of using this message, leaves prefer the Gnutella06LeafQueryRequest/Response.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Gnutella06Query extends AbstractGnutellaMessage implements Cloneable {
	
	private Query query;
	private int ttl;
	private Gnutella06OverlayContact upInitiator;
	private int maximumHitsWanted;

	/**
	 * Creates a new query message with the given parameters
	 * 
	 * @param upInitiator: The ultrapeer that initiated the query
	 * @param q: The query that has been made
	 * @param ttl: The time to live of this query in order to limit the depth
	 * @param maximumHitsWanted: The maximum number of hits the initiator wants
	 */
	public Gnutella06Query(Gnutella06OverlayContact upInitiator, Query q, int ttl, int maximumHitsWanted) {
		this.query = q;
		this.ttl = ttl;
		this.upInitiator = upInitiator;
		this.maximumHitsWanted = maximumHitsWanted;
	}
	
	/**
	 * Decreases the time-to-live field of this query message
	 */
	public void decreaseTTL() {
		this.ttl--;
	}
	
	/**
	 * Returns the time-to-live field of this query message
	 * @return
	 */
	public int getTTL() {
		return ttl;
	}
	
	/**
	 * Returns the maximum hits that are still wanted by the initiator
	 * @return
	 */
	public int getMaximumHitsWanted() {
		return maximumHitsWanted;
	}
	
	public Gnutella06Query clone() {
		return new Gnutella06Query(upInitiator, query, ttl, maximumHitsWanted);
	}
	
	/**
	 * Returns the query encapsulated in this message.
	 * @return
	 */
	public Query getQueryInfo() {
		return query;
	}

	@Override
	public long getGnutellaPayloadSize() {
		return query.getSize() + upInitiator.getSize() + 3;
	}
	
	/**
	 * Returns the ultrapeer that initiated this query
	 * @return
	 */
	public Gnutella06OverlayContact getUPIntiator() {
		return upInitiator;
	}
	
	
	public String toString() {
		return "QUERY: query=" + query + ", ttl=" + ttl 
			+ ", sender=" + upInitiator + ", maximumHitsWanted=" + maximumHitsWanted;
	}

	/**
	 * Sets the maximum hits wanted for this query to a new value.
	 * @param maximumHitsWanted
	 */
	public void setMaximumHitsWanted(int maximumHitsWanted) {
		this.maximumHitsWanted = maximumHitsWanted;
	}

}
