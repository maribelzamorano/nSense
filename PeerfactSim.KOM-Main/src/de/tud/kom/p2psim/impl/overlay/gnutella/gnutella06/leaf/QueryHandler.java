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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.leaf;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IQueryInfo;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.Gnutella06ConnectionManager;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Handles queries for leaves. Queries of leaves are just relayed
 * to an ultrapeer.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class QueryHandler {

	Leaf owner;
	private Gnutella06ConnectionManager<?> mgr;
	
	static Logger log = SimLogger.getLogger(QueryHandler.class);

	/**
	 * Creates the query handler for the given leaf and its connection manager
	 * mgr.
	 * @param owner
	 * @param mgr
	 */
	public QueryHandler(Leaf owner, Gnutella06ConnectionManager<?> mgr) {
		this.owner = owner;
		this.mgr = mgr;
	}
	
	/**
	 * Starts a query with the given query info by relaying it to an ultrapeer
	 * @param info
	 */
	public void startQuery(IQueryInfo info, int hitsWanted) {
		Query q = new Query(info);
		owner.getLocalEventDispatcher().queryStarted(owner.getOwnContact(), q);
		Gnutella06OverlayContact peerForQuery = mgr.getRandomContact();
		if (peerForQuery != null) {
			new LeafQueryOperation(owner, q, hitsWanted, peerForQuery, this.new QueryCallback(q)).scheduleImmediately();
		} else {
			log.debug("Cannot start query because node " + owner.getOwnContact() + " has no ultrapeer." );
		}
	}
	
	/**
	 * Called when the leaf query was finished. 
	 */
	class QueryCallback implements OperationCallback<List<QueryHit>> {

		private Query q;

		public QueryCallback(Query q) {
			this.q = q;
		}

		@Override
		public void calledOperationFailed(Operation<List<QueryHit>> op) {
			owner.getLocalEventDispatcher().queryFailed(owner.getOwnContact(),
					q, QueryHit.getTotalHits(op.getResult()));
		}

		@Override
		public void calledOperationSucceeded(Operation<List<QueryHit>> op) {
			owner.getLocalEventDispatcher().querySucceeded(owner.getOwnContact(),
					q, QueryHit.getTotalHits(op.getResult()));
		}

	}
	
}
