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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.ultrapeer;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IQueryInfo;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaQueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.Gnutella06ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.messages.Gnutella06Query;
import de.tud.kom.p2psim.impl.util.timeoutcollections.TimeoutSet;

/**
 * Manages incoming queries, decides if to answer them with a QueryHit or to
 * relay them
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class QueryHandler {

	Ultrapeer owner;

	private Gnutella06ConnectionManager<Object> upMgr;

	private Gnutella06ConnectionManager<LeafInfo> leafMgr;

	TimeoutSet<Integer> queryUIDsReceived;

	TimeoutSet<Integer> queryUIDsRelayed;

	/**
	 * Creates a new query handler
	 * @param owner: the ulrapeer that owns this query handler
	 * @param leafMgr: the connection manager for leaves connected to this node
	 * @param upMgr: the connection manager for ultrapeers connected to this node
	 */
	public QueryHandler(Ultrapeer owner, Gnutella06ConnectionManager<LeafInfo> leafMgr,
			Gnutella06ConnectionManager<Object> upMgr) {
		this.owner = owner;
		this.upMgr = upMgr;
		this.leafMgr = leafMgr;
		long cacheTimeout = owner.getConfig().getQueryCacheTimeout();
		queryUIDsReceived = new TimeoutSet<Integer>(cacheTimeout);
		queryUIDsRelayed = new TimeoutSet<Integer>(cacheTimeout);
	}

	/**
	 * Begins a <b>new</b> query with the given query info (not for relaying them).
	 * Does it by calling the Dynamic Query operation.
	 * @param info
	 */
	public void startQuery(IQueryInfo info, int hitsWanted) {
		Query q = new Query(info);
		owner.getLocalEventDispatcher().queryStarted(owner.getOwnContact(), q);
		new DynamicQueryOperation(q, hitsWanted, this, owner, leafMgr, upMgr,
				this.new QueryCallback(q)).scheduleImmediately();
	}

	/**
	 * Processes the result of the dynamic query operation started locally.
	 * @author 
	 *
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
			owner.getLocalEventDispatcher().querySucceeded(
					owner.getOwnContact(), q,
					QueryHit.getTotalHits(op.getResult()));
		}

	}

	/**
	 * Processes a query received from a neighbor.
	 * @param queryMsg
	 */
	public void foreignUPQueryAttempt(Gnutella06Query queryMsg) {
		Query query = queryMsg.getQueryInfo();
		List<QueryHit<Gnutella06OverlayContact>> localHits = browseLocallyAndLeaves(query.getInfo());

		int numLocalHits = QueryHit.getTotalHits(localHits);
		int remainingHits = queryMsg.getMaximumHitsWanted() - numLocalHits;
		int queryUID = query.getQueryUID();

		if (numLocalHits > 0 && !queryUIDsReceived.contains(queryUID)) {
			sendMessage(new GnutellaQueryHit<Gnutella06OverlayContact>(queryUID, localHits), queryMsg
					.getUPIntiator());
		}
		markQueryAsReceived(queryUID);

		owner.getLocalEventDispatcher().queryMadeHop(queryUID,
				owner.getOwnContact());

		if (remainingHits > 0 && queryMsg.getTTL() > 1
				&& !queryUIDsRelayed.contains(queryUID)) {
			queryMsg.decreaseTTL();
			queryMsg.setMaximumHitsWanted(remainingHits);
			for (Gnutella06OverlayContact relayContact : upMgr
					.getConnectedContacts()) {
				sendMessage(queryMsg.clone(), relayContact);
			}
			markQueryAsRelayed(queryUID);
		}
	}

	/**
	 * Marks the given query UID as relayed in the query cache.
	 * @param queryUID
	 */
	public void markQueryAsRelayed(int queryUID) {
		queryUIDsRelayed.addNow(queryUID);
	}

	/**
	 * Marks the given query UID as received in the query cache.
	 * @param queryUID
	 */
	public void markQueryAsReceived(int queryUID) {
		queryUIDsReceived.addNow(queryUID);
	}

	/**
	 * Sends an asynchronous message to the given contact
	 * @param msg
	 * @param to
	 */
	private void sendMessage(AbstractGnutellaMessage msg,
			Gnutella06OverlayContact to) {
		owner.getHost().getTransLayer().send(msg, to.getTransInfo(),
				to.getTransInfo().getPort(), TransProtocol.UDP);
	}

	/**
	 * Browses locally for resources. The replication info received from
	 * the leaves as well as the own shared resources are considered.
	 * @param query
	 * @return
	 */
	public List<QueryHit<Gnutella06OverlayContact>> browseLocallyAndLeaves(IQueryInfo query) {
		List<QueryHit<Gnutella06OverlayContact>> hits = browseLeaves(query);
		QueryHit<Gnutella06OverlayContact> localHit = browseLocally(query);
		if (localHit != null)
			hits.add(localHit);
		return hits;

	}

	/**
	 * Browses for hits in the local resource set.
	 * Returns null if there is no hit locally
	 * 
	 * @param query
	 * @return
	 */
	public QueryHit<Gnutella06OverlayContact> browseLocally(IQueryInfo query) {
		int hitCount = query.getNumberOfMatchesIn(owner.getResources());

		if (hitCount > 0)
			return new QueryHit<Gnutella06OverlayContact>(owner.getOwnContact(), hitCount);
		else
			return null;
	}

	/**
	 * Browses for hits in the replication data received from connected leaves.
	 * @param query
	 * @return
	 */
	public List<QueryHit<Gnutella06OverlayContact>> browseLeaves(IQueryInfo query) {

		List<QueryHit<Gnutella06OverlayContact>> hits = new ArrayList<QueryHit<Gnutella06OverlayContact>>();

		for (Gnutella06OverlayContact leaf : leafMgr.getConnectedContacts()) {
			LeafInfo info = leafMgr.getMetadata(leaf);
			int hitCount = query.getNumberOfMatchesIn(info.getLeafResources());

			if (hitCount > 0)
				hits.add(new QueryHit<Gnutella06OverlayContact>(leaf, hitCount));
		}
		return hits;
	}

}
