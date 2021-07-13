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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation.Reason;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.KademliaOperation;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.PeriodicOperation;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.setup.KademliaSetup;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetNode;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Handles the online/offline state and the logic related to state changes for
 * Kademlia's Nodes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class StateHandler<T extends KademliaOverlayID> implements ConnectivityListener {

	private final static Logger log = SimLogger.getLogger(StateHandler.class);

	/**
	 * The Node that this StateHandler belongs to.
	 */
	protected final Node<T> myNode;

	/**
	 * The status ({@see BaseOverlayNode#PeerStatus}) of this peer.
	 */
	private PeerStatus peerStatus;

	/**
	 * Whether the periodic data lookup (for analysis/measurements) has been
	 * enabled. It is assumed that once this flag has been set to true, it does
	 * not become false again.
	 */
	private boolean lookupEnabled = false;

	/**
	 * Configuration values ("constants").
	 */
	protected final ComponentsConfig config;

	/**
	 * Constructs a new StateHandler. Its initial state is ABSENT.
	 * 
	 * @param itsNode
	 *            the Node for which the state is handled.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public StateHandler(final Node<T> itsNode, final ComponentsConfig conf) {
		config = conf;
		peerStatus = PeerStatus.ABSENT;
		myNode = itsNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void connectivityChanged(final ConnectivityEvent ce) {
		// handle changes in network connectivity
		if (ce.isOnline()) {
			doConnect();
		} else if (ce.isOffline()) {
			doDisconnect();
		}
	}

	/**
	 * Performs actions that follow a connect.
	 */
	protected final void doConnect() {
		if (peerStatus != PeerStatus.ABSENT) {
			return;
		}
		peerStatus = PeerStatus.TO_JOIN;

		// initially build the routing table
		final KademliaOperation<T> buildRT = myNode.getOperationFactory()
				.getBuildRoutingTableOperation(Operations.EMPTY_CALLBACK);
		buildRT.scheduleImmediately();
		// after RT has been built, continues in routingTableBuilt()

		// log.debug("Connect triggered at second " + getSimSec() + ", node="
		// + myNode); // TODO uncomment
	}

	/**
	 * Called if the "buildRT" operation from doConnect() has completed. That
	 * is, the routing table is assumed to be in a stable state.
	 */
	protected final void routingTableBuilt() {
		final PeriodicOperation<T> periodicRefresh, periodicRepublish;

		/*
		 * The routing table has been built: TO_JOIN -> PRESENT. (Set status
		 * only if currently TO_JOIN as the Node might have gone offline in the
		 * meantime...)
		 */
		if (peerStatus != PeerStatus.TO_JOIN) {
			return;
		}
		peerStatus = PeerStatus.PRESENT;

		/*
		 * Refresh/Republish intervals are ideally given for each item
		 * (data/bucket) individually. For performance reasons, these are
		 * executed together in one operation. To make sure that no
		 * refresh/republish events are "missed", schedule periodic operations
		 * with one quarter of the "individual" interval. (In order for this to
		 * work, timeouts have to be at least 1.25 times the interval.)
		 * 
		 * Example (republish interval and data timeout 1 hour): Republish at
		 * minute 0, new item arrives at minute 5, next republish at minute 60
		 * does not include that item because its last republish is not yet one
		 * hour ago, at minute 65 the new item needs to be republished but the
		 * next republish operation does not start before minute 120 - by then,
		 * the data item has already expired. ==> Check more often if items have
		 * to be republished.
		 */

		// schedule periodical bucket refresh (begins in 0.25*REFRESH_INTERVAL)
		periodicRefresh = new PeriodicOperation<T>(
				(long) (0.25 * config.getRefreshInterval()), myNode,
				Operations.EMPTY_CALLBACK, Reason.MAINTENANCE, config) {

			@Override
			protected KademliaOperation<List<KademliaOverlayContact<T>>> createPeriodicOperation() {
				return myNode.getOperationFactory().getRefreshOperation(false,
						Operations.EMPTY_CALLBACK);
			}

		};
		periodicRefresh.scheduleWithDelay((long) (0.25 * config
				.getRefreshInterval()));

		// schedule periodical republish (starting in 0.25*REPUBLISH_INTERVAL)
		periodicRepublish = new PeriodicOperation<T>(
				(long) (0.25 * config.getRepublishInterval()), myNode,
				Operations.EMPTY_CALLBACK, Reason.MAINTENANCE, config) {

			@Override
			protected KademliaOperation<T> createPeriodicOperation() {
				return myNode.getOperationFactory().getRepublishOperation(
						Operations.EMPTY_CALLBACK);
			}

		};
		periodicRepublish.scheduleWithDelay((long) (0.25 * config
				.getRepublishInterval()));

		// for measurement purposes, periodic lookups can be initiated
		beginPeriodicLookups();

		// log.debug("Connect finished at second " + getSimSec() + ", node="
		// + myNode); // TODO uncomment

		// TEST if this is the right place for starting SkyNet
		SkyNetNode node = ((SkyNetNode) myNode.getHost().getOverlay(
				AbstractSkyNetNode.class));
		if (node != null) {
			node.startSkyNetNode(Simulator.getCurrentTime());
		}
	}

	/**
	 * Performs actions that follow a disconnect.
	 */
	protected final void doDisconnect() {
		peerStatus = PeerStatus.ABSENT;
		myNode.getOperationFactory().abortAllOperations();
		// log.debug("Disconnect finished at second " + getSimSec() + ", node="
		// + myNode); // TODO uncomment
	}

	/**
	 * @return the status of the peer.
	 */
	protected final PeerStatus getPeerStatus() {
		return peerStatus;
	}

	/**
	 * Enables periodic lookups for measurement purposes. These look up random
	 * keys and are scheduled in a constant interval.
	 */
	protected final void enablePeriodicLookup() {
		lookupEnabled = true;
		beginPeriodicLookups();

		// log.debug("Periodic lookups enabled at second " + getSimSec()
		// + ", node=" + myNode); // TODO uncomment
	}

	/**
	 * Schedules periodic data item lookups if this node is online and the
	 * "lookupEnabled" flag has been set before. These lookups begin immediately
	 * and are executed in intervals of
	 * {@link WorkloadConfig#PERIODIC_LOOKUP_INTERVAL}.
	 */
	private final void beginPeriodicLookups() {
		final PeriodicOperation<T> periodicLookup;

		if (!lookupEnabled || peerStatus != PeerStatus.PRESENT) {
			return;
		}

		periodicLookup = new PeriodicOperation<T>(
				config.getPeriodicLookupInterval(), myNode,
				Operations.EMPTY_CALLBACK, Reason.USER_INITIATED, config) {

			@Override
			protected KademliaOperation<DHTObject> createPeriodicOperation() {
				final KademliaOverlayKey rndKey = KademliaSetup
						.getWorkloadGenerator().getRandomKeyForLookup();
				return myNode.getOperationFactory().getDataLookupOperation(
						rndKey, Operations.EMPTY_CALLBACK);
			}

		};
		periodicLookup.scheduleImmediately();
	}

	/**
	 * Does a single lookup. Added by Leo Nobach
	 * 
	 * @param key
	 */
	protected final void doSingleLookup(KademliaOverlayKey key,
			OperationCallback<DHTObject> callback) {
		KademliaOperation<DHTObject> op = myNode.getOperationFactory()
				.getDataLookupOperation(key, callback);
		op.scheduleImmediately();
	}

	private static final double getSimSec() {
		return Simulator.getCurrentTime() / (double) Simulator.SECOND_UNIT;
	}

}
