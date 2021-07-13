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


/**
 * 
 */
package de.tud.kom.p2psim.impl.overlay.gnutella.gia;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.IGnutellaEventListener.ConnBreakCause;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IConnection;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IManageableConnection;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaPongMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.operations.GiaConnectOperationX;
import de.tud.kom.p2psim.impl.util.timeoutcollections.TimeoutSet;
import de.tud.kom.p2psim.impl.util.toolkits.CollectionHelpers;
import de.tud.kom.p2psim.impl.util.toolkits.Predicate;

/**
 * Connection manager of Gia peers. Gia has a lot of differences to other overlays, especially regarding
 * its connection procedure. This component runs a three-way handshake with a special acceptance decider,
 * before the peers are connected.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaConnectionManager extends ConnectionManager<GiaConnectionMetadata, GiaOverlayContact, IGiaConfig, GiaPongMessage> {

	GiaNode giaOwner;
	private int capacity;
	private IGiaConfig config;
	
	TimeoutSet<GiaOverlayContact> hostCache;
	private PeriodicAdaptationTrigger trigger;
	long lastSchedAdaptIntvl;
	
	/**
	 * @param owner
	 * @param config
	 * @param size
	 * @param pongHdlr
	 * @param foreignAttemptKickProb
	 */
	public GiaConnectionManager(GiaNode owner,
			IGiaConfig config, int capacity) {
		super(owner, config, maxNbrsOf(capacity, config));
		
		this.config = config;
		this.capacity = capacity;
		this.giaOwner = owner;
		hostCache = new TimeoutSet<GiaOverlayContact>(config.getHostCacheTimeout());
	}
	
	public String toString() {
		return "tr=" + (trigger != null) + ", " + super.toString() + ", hostCache=" + hostCache + ", adaptIntvl = " + getAdaptationInterval() + ", lastIntvl=" + lastSchedAdaptIntvl;
	}
	
	public void startTrigger() {
		if (trigger == null)
			(trigger = new PeriodicAdaptationTrigger()).scheduleImmediately();
	}
	
	public void stopTrigger() {
		if (trigger != null) {
			trigger.stop();
			trigger = null;
		}
	}
	
	/**
	 * Reserves connection before. The state is set to "Pending", until the "Connection succeeded" is triggered in the returned object.
	 * @param to
	 * @return
	 */
	public IManageableConnection reserveForeignConnection(GiaOverlayContact to) {
		return new Connection(to, true);
	}
	
	/**
	 * Drops nodeToDrop and inserts node nodeToInsert, but only if nodeToDrop was found before. Otherwise, it returns false 
	 * @return
	 */
	public IManageableConnection dropAndInsert(GiaOverlayContact nodeToDrop, GiaOverlayContact nodeToInsert) {
		if (this.closeConnection(nodeToDrop, nodeToInsert)) {
			return new Connection(nodeToInsert, true);
		}
		return null;
	}
	
	protected Operation createNewConnectOperation(GiaOverlayContact contact, IManageableConnection connection, OperationCallback<Object> callback) {
		return new GiaConnectOperationX(giaOwner, contact, this, connection, callback);
	}
	
	public int getDegree() {
		//return getNumberOfContacts();
		return this.getNumberOfContactsInState(ConnectionState.Connected);
		//TODO: beinhaltet Degree auch Connections die gerade im Progress sind?
	}
	
	/**
	 * Returns the maximum number of peers this peer may be connected to.
	 * @return
	 */
	public int getMaxNbrs() {
		return maxNbrsOf(capacity, config);
	}
	
	/**
	 * Returns the minimum number of peers this peer may be connected to.
	 * @return
	 */
	public int getMinNbrs() {
		return minNbrsOf(capacity, config);
	}
	
	/**
	 * Returns the maximum number of peers a peer with given capacity and config may be connected to.
	 * @param capacity
	 * @param config
	 * @return
	 */
	public static int maxNbrsOf(int capacity, IGiaConfig config) {
		return Math.max(config.getMinNbrs(), Math.min(config.getMaxNbrs(), capacity/config.getMinAlloc()));
		//Math.max is a nasty hack. Does not conform to the specification, maybe error in specification?
	}
	
	/**
	 * Returns the minimum number of contacts a peer with given capacity and config may be connected to.
	 * @param capacity
	 * @param config
	 * @return
	 */
	public static int minNbrsOf(int capacity, IGiaConfig config) {
		return config.getMinNbrs();
	}
	
	/**
	 * Returns the satisfaction level of this peer. This is a number between 0 and 1 that shows how satisfied
	 * a peer is with its connectivity.
	 * @return
	 */
	public double getSatisfactionLevel() {
		if (getDegree() < getMinNbrs()) return 0d;
		double total = 0;
		for (GiaOverlayContact n : this.getConnectedContacts()) {
			total += n.getCapacity()/(double)this.getMetadata(n).getLastDegreeObserved();
		}
		double result = total/capacity;
		if (result > 1d || this.getDegree() >= getMaxNbrs()) return 1d;
		return result;	
	}
	
	/**
	 * Returns the adaptation interval of this peer. On every adaptation attempt, the peer initiates
	 * a connection handshake to another peer from the host cache in order to ensure a well-suited
	 * topology.
	 * 
	 * @return
	 */
	public long getAdaptationInterval() {
		return (long)(config.getAdaptationMaxInterval() * 
			Math.pow(config.getAdaptationAggressiveness(), -(1 - getSatisfactionLevel())));
	}
	
	@Override
	public void seenContact(GiaOverlayContact c) {
		if (hostCache.size() > config.getHostCacheSize()) hostCache.removeOldest();
		hostCache.addNow(c);
		startTrigger();
	}
	
	/**
	 * Called when a connection has been lost.
	 */
	@Override
	protected void lostAConnection(IConnection<GiaOverlayContact, GiaConnectionMetadata> c, ConnBreakCause cause) {
		hostCache.remove(c.getContact());
		super.lostAConnection(c, cause);
	}
	
	GiaOverlayContact getContactToConnectTo() {
		
		if (hostCache.isEmpty()) return null;
		
		Set<GiaOverlayContact> candidates = new HashSet<GiaOverlayContact>();
		CollectionHelpers.filter(hostCache.getUnmodifiableSet(), candidates, new Predicate<GiaOverlayContact>() {

			@Override
			public boolean isTrue(GiaOverlayContact object) {
				return !(GiaConnectionManager.this.knowsContact(object) || object.equals(giaOwner.getOwnContact()));
			}
			
		});
		
		if (candidates.isEmpty()) return null;
		
		GiaOverlayContact result = Collections.max(candidates, GiaOverlayContact.getCapacityComparator());
		if (result.getCapacity() > capacity) return result;
		return CollectionHelpers.getRandomEntry(candidates);
	}
	
	public class PeriodicAdaptationTrigger extends AbstractOperation<GiaNode, Object> {

		boolean stopped = false;
		
		/**
		 * @param component
		 * @param callback
		 */
		protected PeriodicAdaptationTrigger() {
			super(giaOwner, Operations.getEmptyCallback());
		}
		
		/**
		 * 
		 */
		public void stop() {
			stopped=true;
		}

		@Override
		protected void execute() {
			if (!stopped) {
				GiaOverlayContact c = getContactToConnectTo();
				if (c != null) {
					GiaConnectionManager.this.new Connection(c, false);
				} else {
					//System.out.println(giaOwner.getOwnContact() + ": No contact in the host cache that can be used to connect to. Host cache: " + hostCache);
				}
				lastSchedAdaptIntvl  = getAdaptationInterval();
				this.scheduleWithDelay(lastSchedAdaptIntvl); 
			}
			else operationFinished(true);
		}

		@Override
		public Object getResult() {
			return null;
		}
		
	}

	/**
	 * Returns true if the connection manager holds a contact that is either connecting, pending or connected.
	 * @param to
	 * @return
	 */
	public boolean knowsContact(GiaOverlayContact c) {
		return contacts.containsKey(c);
	}
	
}




