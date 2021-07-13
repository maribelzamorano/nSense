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


package de.tud.kom.p2psim.impl.overlay.gnutella.gia.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaQueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaQueryManager;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Initiates a query and then waits for QueryHits. If enough QueryHits have returned, the operation is marked
 * as successful and the QueryHits are returned as result.
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaQueryOperation extends
AbstractOperation<GiaNode, List<QueryHit<GiaOverlayContact>>> implements
TransMessageListener {

	private GiaQueryManager qMgr;
	int totalHits = 0;
	private int hitsWanted;

	static final Logger log = SimLogger.getLogger(GiaQueryOperation.class);

	public GiaQueryOperation(Query q, int hitsWanted, GiaQueryManager qMgr, GiaNode component,
			OperationCallback<List<QueryHit<GiaOverlayContact>>> callback) {
		super(component, callback);
		this.qMgr = qMgr;
		this.hitsWanted = hitsWanted;
		this.query = q;
	}

	public Set<QueryHit<GiaOverlayContact>> hitContacts = new HashSet<QueryHit<GiaOverlayContact>>();
	
	private Query query;
	
	@Override
	public void execute() {
		debug("==Query started.");
		List<QueryHit<GiaOverlayContact>> localQueryHits = qMgr.startQuery(query, hitsWanted);
		addQueryHits(localQueryHits);
		if (enoughHits()) {
			finishSuccessfully();
		} else {
			debug("Not enough local hits, wait for more hits from the query that will be relayed...");
			listen();
			this.new Timeout().scheduleWithDelay(getComponent().getConfig().getQueryTimeout());
		}
	}

	public Query getQuery() {
		return query;
	}

	private void debug(String msg) {
		log.debug(Simulator.getFormattedTime(Simulator.getCurrentTime()) + msg);
	}

	public boolean enoughHits() {
		return totalHits >= hitsWanted;
	}

	public void timeoutOccured() {
		debug("Timeout occured.");
		if (enoughHits()) {
			finishSuccessfully();
		} else {
			finishUnsuccessfully();
		}
	}

	/**
	 * Starts the listening for incoming QueryHits
	 */
	private void listen() {
		getTransLayer().addTransMsgListener(this, getComponent().getPort());
	}

	/**
	 * Stops the listening for incoming QueryHits
	 */
	void stopListen() {
		getTransLayer().removeTransMsgListener(this, getComponent().getPort());
	}

	TransLayer getTransLayer() {
		return getComponent().getHost().getTransLayer();
	}

	/**
	 * Adds the query hits to the result of this operation
	 * @param newHits
	 */
	private void addQueryHits(List<QueryHit<GiaOverlayContact>> newHits) {
		hitContacts.addAll(newHits);
		totalHits += QueryHit.getTotalHits(newHits);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof GnutellaQueryHit) {
			debug("Hit message arrived.");
			GnutellaQueryHit<GiaOverlayContact> hitMsg = ((GnutellaQueryHit<GiaOverlayContact>) msg);
			if (hitMsg.getQueryUID() == query.getQueryUID()) {
				addQueryHits(hitMsg.getQueryHits());
				if (enoughHits()) finishSuccessfully();
			}
		}
	}

	@Override
	public List<QueryHit<GiaOverlayContact>> getResult() {
		return new ArrayList<QueryHit<GiaOverlayContact>>(hitContacts);
	}

	public class Timeout implements SimulationEventHandler {

		boolean listeningStopped = false;

		public void scheduleWithDelay(long delay) {
			long time = Simulator.getCurrentTime() + delay;
			scheduleAtTime(time);
		}

		public void scheduleAtTime(long time) {
			time = Math.max(time, Simulator.getCurrentTime());
			Simulator.scheduleEvent(this, time, this,
					SimulationEvent.Type.TIMEOUT_EXPIRED);
		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			timeoutOccured();
		}

	}
	
	/**
	 * Stopping of listening is encapsulated by an operation to avoid 
	 * ConcurrentModificationExceptions.
	 * @author Leo Nobach
	 *
	 */
	class StopListeningOperation extends AbstractOperation<GiaNode, Object> {
		/**
		 * @param component
		 * @param callback
		 */
		protected StopListeningOperation(GiaNode component) {
			super(component, Operations.getEmptyCallback());
		}

		@Override
		protected void execute() {
			stopListen();
		}
	
		@Override
		public Object getResult() {
			return null;
		}
	
	}

	public void finishSuccessfully() {
		new StopListeningOperation(getComponent()).scheduleImmediately();
		this.operationFinished(true);
		debug("==Query finished successfully.");
	}

	public void finishUnsuccessfully() {
		new StopListeningOperation(getComponent()).scheduleImmediately();
		this.operationFinished(false);
		debug("==Query failed.");
	}



}
