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

package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.overlay.dht.DHTEntry;
import de.tud.kom.p2psim.api.overlay.dht.DHTListener;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.dht.KBRForwardInformationImpl;
import de.tud.kom.p2psim.impl.overlay.dht.KBRLookupProvider;
import de.tud.kom.p2psim.impl.overlay.dht.KBRMsgHandler;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.PeerStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.operations.LookupSchedule;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.JoinOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.LeaveOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.AbstractChordOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.ChordOperationListener;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.LookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.StoreOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.ValueLookupOperation;
import de.tud.kom.p2psim.impl.service.dht.simple.SimpleDHTService;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetNode;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordNode extends AbstractOverlayNode<ChordID> implements
		DHTNode<ChordContact>, KBR<ChordID, ChordContact> {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordMessageHandler messageHandler;

	private final TransLayer transLayer;

	private ChordRoutingTable routingTable;

	/**
	 * Flag to prevent automatical rejoins after churn related on-line events,
	 * if the initial node join was not initiated yet (using the join method) or
	 * the node has intentionally left the overlay (using the leave method).
	 */
	private boolean rejoinOnOnlineEvent = false;

	/**
	 * handle operation time out, deliver operation results
	 */
	private ChordOperationListener operationListener;

	/**
	 * Contain executing operations
	 */
	private final Map<Integer, AbstractChordOperation<?>> lookupOperationList = new HashMap<Integer, AbstractChordOperation<?>>();

	/**
	 * The Id of the next lookup
	 */
	private int nextLookupId = 0;

	/**
	 * A map containing all stored DHTObjects of this node
	 */
	private DHTListener dht = new SimpleDHTService();

	private ChordBootstrapManager bootstrap;

	/**
	 * @param transLayer
	 * @param port
	 * @param bootstrap
	 */
	public ChordNode(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap) {

		super(new ChordID(transLayer.getLocalTransInfo(port)), port);
		this.transLayer = transLayer;
		operationListener = new ChordOperationListener(this);

		// create message handler
		messageHandler = new ChordMessageHandler(this);
		// add message handler to the listeners list
		this.getTransLayer().addTransMsgListener(this.messageHandler,
				this.getPort());

		this.bootstrap = bootstrap;

	}

	private JoinOperation joinOperation = null;

	/**
	 * Join the overlay with a delay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	public int joinWithDelay(OperationCallback callback, long delay) {
		setPeerStatus(PeerStatus.TO_JOIN);
		// Node intentionally joined --> Do rejoins after churn on-line events
		this.rejoinOnOnlineEvent = true;

		log.debug("Node initiated join " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		joinOperation = new JoinOperation(this, callback);

		if (delay > 0)
			joinOperation.scheduleWithDelay(delay);
		else
			joinOperation.scheduleImmediately();

		return joinOperation.getOperationID();
	}

	/**
	 * Immediately join the overlay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	@Override
	public int join(OperationCallback callback) {
		setPeerStatus(PeerStatus.TO_JOIN);
		return joinWithDelay(callback, 0l);
	}

	/**
	 * Leave the overlay
	 * 
	 * @param callback
	 * @return the Id of the LeaveOperation
	 */
	@Override
	public int leave(OperationCallback callback) {
		// Node intentionally left --> Do not rejoin after churn on-line events
		this.rejoinOnOnlineEvent = false;
		bootstrap.unregisterNode(this);

		// Check if the node is present in the overlay
		if (getPeerStatus() != PeerStatus.PRESENT) {
			log.debug("Node initiated leave but was not present " + this
					+ " at Time[s] " + Simulator.getCurrentTime()
					/ Simulator.SECOND_UNIT);
			return -1;
		} else {
			log.debug("Node initiated leave " + this + " at Time[s] "
					+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		}

		LeaveOperation leave = new LeaveOperation(this, callback);
		leave.scheduleImmediately();
		return leave.getOperationID();
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {

		log.debug("Connectivity changed " + this + " to online="
				+ ce.isOnline());
		if (ce.isOnline()) {
			if (getPeerStatus().equals(PeerStatus.ABSENT)
					&& this.rejoinOnOnlineEvent) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " received online event ");
				setPeerStatus(PeerStatus.TO_JOIN);
				join(Operations.EMPTY_CALLBACK);
			}
		} else if (ce.isOffline()) {
			if (getPeerStatus().equals(PeerStatus.PRESENT)
					|| getPeerStatus().equals(PeerStatus.TO_JOIN)) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " is affected by churn ");

				setPeerStatus(PeerStatus.ABSENT);

				// Mark the old listener as inactive and create an new one
				operationListener.setInactive();
				operationListener = new ChordOperationListener(this);

				// Mark the old routing table as inactive
				if (routingTable != null)
					routingTable.setInactive();
				routingTable = null;

				// Unregister node at bootstrap manager
				this.getBootstrapManager().unregisterNode(this);

				// Inform statistic class about leaving peer
				PeerStore.getInstance().registerNodeLeave(this,
						Simulator.getCurrentTime());

				// delete the stored data items
				// TODO check this remove operation as it throws a
				// ConcurrentModificationException
				Set<DHTEntry> entries = dht.getDHTEntries();
				Iterator<DHTEntry> it = entries.iterator();
				// while (it.hasNext()) {
				// DHTEntry dhtEntry = (DHTEntry) it.next();
				// it.remove();
				// dht.removeDHTEntry(dhtEntry.getKey());
				// }

				// Reset the SkyNet node
				OverlayNode olNode = getHost().getOverlay(SkyNetNode.class);
				if (olNode != null && olNode instanceof SkyNetNode)
					((SkyNetNode) olNode).resetSkyNetNode(Simulator
							.getCurrentTime());
			}
		}
	}

	/**
	 * Find node that is responsible for the given key
	 * 
	 * @param key
	 *            the key to look up
	 * @param callback
	 * @return the Id of the LookupOperation, -1 if the node is not present in
	 *         the overlay
	 */
	public int overlayNodeLookup(ChordID key,
			OperationCallback<List<ChordContact>> callback) {
		if (!isPresent())
			return -1;

		log.debug("Start look up from node = " + this + " key = " + key);

		int lookupId = getNextLookupId();
		LookupOperation op = new LookupOperation(this, key, callback, lookupId);
		registerLookupOperation(lookupId, op);
		op.scheduleImmediately();

		return lookupId;
	}

	/**
	 * 
	 * This method is called when join operation is finished. As results of join
	 * operation are
	 * 
	 * @param successor
	 *            : next direct successor in ring form
	 * @param predecessor
	 *            : : next direct predecessor in ring form
	 * @param succFingerTable
	 *            : FingerTable of successor. The FingerTable size is so big to
	 *            put in a message, thus a list of different ChordContact
	 *            represent the FingerTable instance. The FingerTable can be
	 *            reconstructed by using utility class RoutingTableContructor
	 * 
	 */
	public void joinOperationFinished(ChordContact successor,
			ChordContact predecessor, Set<ChordContact> succFingerTable) {

		log.debug(Simulator.getSimulatedRealtime() + " Peer "
				+ getHost().getNetLayer().getNetID() + " joined ");
		PeerStore.getInstance().registerNewJoin(this,
				Simulator.getCurrentTime());
		// create Routing table
		routingTable = new ChordRoutingTable(this, predecessor, successor,
				succFingerTable);

		if (getChordRoutingTable().isNodeItsOwnSuccessor()
				&& getChordRoutingTable().isNodeItsOwnSPredecessor()
				&& this.getBootstrapManager().getNumOfAvailableNodes() > 1) {

			joinWithDelay(
					Operations.EMPTY_CALLBACK,
					Simulator
							.getRandom()
							.nextInt(
									(int) ChordConfiguration.MAX_WAIT_BEFORE_JOIN_RETRY));

			log.error(getOverlayID()
					+ " joined but has itself as successor although there are other alive peers in the system. Will do a rejoin again.");

		} else {

			// Start SkyNet after the joining was successful
			SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
					AbstractSkyNetNode.class));
			if (node != null) {
				node.startSkyNetNode(Simulator.getCurrentTime());
			}
		}
	}

	/**
	 * This method is called when leave operation is finished
	 */
	public void leaveOperationFinished() {

		// Stop SkyNet
		SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
				AbstractSkyNetNode.class));
		if (node != null) {
			node.resetSkyNetNode(Simulator.getCurrentTime());
		}

		log.info(" node leave " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		setPeerStatus(PeerStatus.ABSENT);

		// Mark the old listener as inactive and create an new one
		operationListener.setInactive();
		operationListener = new ChordOperationListener(this);

		// Mark the old routing table as inactive
		if (routingTable != null)
			routingTable.setInactive();

		PeerStore.getInstance().registerNodeLeave(this,
				Simulator.getCurrentTime());
	}

	// Getters and Setters

	public boolean isPresent() {
		return getPeerStatus() == PeerStatus.PRESENT;
	}

	public ChordRoutingTable getChordRoutingTable() {
		return routingTable;
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	public ChordContact getLocalChordContact() {
		return new ChordContact(getOverlayID(),
				transLayer.getLocalTransInfo(this.getPort()));
	}

	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(getPort());
	}

	public JoinOperation getJoinOperation() {
		return joinOperation;
	}

	public ChordOperationListener getOperationListener() {
		return operationListener;
	}

	protected void registerLookupOperation(int lookupId,
			AbstractChordOperation op) {
		lookupOperationList.put(lookupId, op);
	}

	public AbstractChordOperation<?> removeLookupOperation(int lookupId) {
		return lookupOperationList.remove(lookupId);
	}

	public AbstractChordOperation<?> getLookupOperation(int lookupId) {
		return lookupOperationList.get(lookupId);
	}

	public ChordMessageHandler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public String toString() {
		return "Node " + getOverlayID() + " " + getPeerStatus();
	}

	private int getNextLookupId() {
		nextLookupId++;
		return nextLookupId;
	}

	// test methods

	/**
	 * This method is called to periodically start dummy lookup request
	 */
	public void startLookup() {
		new LookupSchedule(this).scheduleWithDelay(Simulator.getRandom()
				.nextInt((int) ChordConfiguration.TIME_BETWEEN_RANDOM_LOOKUPS));
	}

	public boolean isOnline() {
		return getHost().getNetLayer().isOnline();
	}

	public boolean absentCausedByChurn() {
		return (getPeerStatus() == PeerStatus.ABSENT) && rejoinOnOnlineEvent;
	}

	/*
	 * DHTNode methods
	 */

	@Override
	public int store(OverlayKey key, DHTObject obj,
			OperationCallback<Set<ChordContact>> callback) {
		if (!(key instanceof ChordKey))
			return -1;

		StoreOperation op = new StoreOperation(this,
				((ChordKey) key).getCorrespondingID(), obj, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int valueLookup(OverlayKey key, OperationCallback<DHTObject> callback) {
		if (!(key instanceof ChordKey))
			return -1;

		ValueLookupOperation op = new ValueLookupOperation(this,
				((ChordKey) key).getCorrespondingID(), callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int nodeLookup(OverlayKey key,
			OperationCallback<List<ChordContact>> callback,
			boolean returnSingleNode) {
		if (!(key instanceof ChordKey))
			return -1;

		return overlayNodeLookup(((ChordKey) key).getCorrespondingID(),
				callback);
	}

	public DHTListener getDHT() {
		return this.dht;
	}

	/*
	 * KBR methods
	 */

	private KBRListener kbrListener;

	private KBRLookupProvider<ChordID, ChordContact> kbrLookupProvider;

	@Override
	public void route(OverlayKey key, Message msg, ChordContact hint) {
		if (getChordRoutingTable() == null
				|| (key != null && !(key instanceof ChordKey)))
			return;

		ChordContact nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			nextHop = local_lookup(key, 1).get(0);

			// Inform the monitors about an initiated query
			Simulator.getMonitor().queryStarted(getLocalOverlayContact(), msg);
		} else {
			log.error("KBR route problem: Both key and hint are null! No idea where to route the message.");
			return;
		}

		KBRForwardInformation info = new KBRForwardInformationImpl(key, msg,
				nextHop);
		kbrListener.forward(info);
		key = info.getKey();
		msg = info.getMessage();
		nextHop = (ChordContact) info.getNextHopAgent();

		if (nextHop != null) { // see kbrListener-Interface, stop Message if
								// nextHop = null
			ForwardMsg fm = new ForwardMsg(getOverlayID(),
					nextHop.getOverlayID(), key, msg);
			getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
					TransProtocol.UDP);
		}
	}

	@Override
	public List<ChordContact> local_lookup(OverlayKey key, int num) {
		if (getChordRoutingTable() == null || !(key instanceof ChordKey))
			return new LinkedList<ChordContact>();

		List<ChordContact> nodes = getChordRoutingTable()
				.getClosestPrecedingFingers(
						((ChordKey) key).getCorrespondingID(), num);

		if (nodes.isEmpty())
			nodes.add(getChordRoutingTable().getSuccessor());
		else if (nodes.get(0).equals(getLocalChordContact()))
			nodes.set(0, getChordRoutingTable().getSuccessor());

		return nodes;
	}

	@Override
	public List<ChordContact> replicaSet(OverlayKey key, int maxRank) {
		List<ChordContact> succs = getChordRoutingTable().getSuccessors();

		if (maxRank < succs.size())
			return succs.subList(0, maxRank);
		return succs;
	}

	@Override
	public List<ChordContact> neighborSet(int num) {
		List<ChordContact> neighbors = new LinkedList<ChordContact>();
		List<ChordContact> preds = getChordRoutingTable().getPredecessors();
		List<ChordContact> succs = getChordRoutingTable().getSuccessors();

		for (int i = 0; i < num; i++) {
			if (i < preds.size())
				neighbors.add(preds.get(i));
			if (i < succs.size() && neighbors.size() < num)
				neighbors.add(succs.get(i));
			if (neighbors.size() >= num
					|| i >= Math.max(preds.size(), succs.size()))
				break;
		}
		return neighbors;
	}

	@Override
	public ChordID[] range(ChordContact contact, int rank) {
		/*
		 * FIXME: Look up concrete meaning of rank in KBR-Paper!
		 */

		ChordID[] range = new ChordID[2];

		range[0] = ChordIDFactory.getInstance().getChordID(
				getChordRoutingTable().getPredecessor().getOverlayID()
						.getValue().add(BigInteger.ONE));

		range[1] = getOverlayID();

		return range;
	}

	@Override
	public boolean isRootOf(OverlayKey key) {
		if (getChordRoutingTable() == null || !(key instanceof ChordKey))
			return false;

		return getChordRoutingTable().responsibleFor(
				((ChordKey) key).getCorrespondingID());
	}

	@Override
	public void setKBRListener(KBRListener listener) {
		this.kbrListener = listener;
		KBRMsgHandler<ChordID, ChordContact> msgHandler = new KBRMsgHandler<ChordID, ChordContact>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public OverlayKey getNewOverlayKey(int rank) {
		return ChordIDFactory.getInstance().getChordID(String.valueOf(rank))
				.getCorespondingKey();
	}

	@Override
	public OverlayKey getRandomOverlayKey() {
		return ChordIDFactory.getInstance().createRandomChordID()
				.getCorespondingKey();
	}

	@Override
	public OverlayContact<ChordID> getLocalOverlayContact() {
		return getLocalChordContact();
	}

	@Override
	public ChordContact getOverlayContact(OverlayID id, TransInfo transInfo) {
		if (!(id instanceof ChordID))
			return null;

		return new ChordContact((ChordID) id, transInfo);
	}

	@Override
	public void hadContactTo(OverlayContact<ChordID> contact) {
		// Nothing to do here.
	}

	@Override
	public KBRLookupProvider<ChordID, ChordContact> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return new INeighborDeterminator() {

			@Override
			public Collection<OverlayContact> getNeighbors() {
				if (routingTable == null)
					return Collections.emptySet();
				return routingTable.getNeighbors();
			}
		};
	}

	public ChordBootstrapManager getBootstrapManager() {
		return bootstrap;
	}

	@Override
	public void registerDHTListener(DHTListener listener) {
		dht = listener;
	}

}
