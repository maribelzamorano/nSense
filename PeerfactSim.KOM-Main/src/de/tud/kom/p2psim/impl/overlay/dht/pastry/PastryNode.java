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


package de.tud.kom.p2psim.impl.overlay.dht.pastry;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.overlay.dht.DHTListener;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.dht.KBRForwardInformationImpl;
import de.tud.kom.p2psim.impl.overlay.dht.KBRLookupProvider;
import de.tud.kom.p2psim.impl.overlay.dht.KBRMsgHandler;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.nodestate.LeafSet;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.nodestate.NeighborhoodSet;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.nodestate.PastryRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.nodestate.StateHelpers;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.operations.AbstractPastryOperation;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.operations.JoinOperation;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.operations.LookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.operations.StoreOperation;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.operations.ValueLookupOperation;
import de.tud.kom.p2psim.impl.service.dht.simple.SimpleDHTService;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to represent nodes of the pastry overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryNode extends AbstractOverlayNode<PastryID> implements
		DHTNode<PastryContact>, KBR<PastryID, PastryContact> {

	private static Logger log = SimLogger.getLogger(PastryNode.class);

	/**
	 * The transport layer of the node
	 */
	private TransLayer transLayer;

	/**
	 * The contact of the node
	 */
	private final PastryContact contact;

	/**
	 * The message handler of this node
	 */
	private PastryMessageHandler msgHandler;

	/**
	 * The routing table of the node
	 */
	private PastryRoutingTable routingTable;

	/**
	 * The leaf set of the node
	 */
	private LeafSet leafSet;

	/**
	 * The neighborhood set of the node
	 */
	private NeighborhoodSet neighborhoodSet;

	/**
	 * Tells whether this node is in a state where it should be present in the
	 * overlay. This means, it either is present or absent due to churn. This
	 * flag is needed to decide if a node should initiate a automatic join after
	 * a churn event. This way, absent nodes stay absent after a churn event.
	 */
	private boolean wantsToBePresent = false;

	/**
	 * Holds pending operations
	 */
	private final Map<Integer, AbstractPastryOperation<?>> registeredOperations = new HashMap<Integer, AbstractPastryOperation<?>>();

	/**
	 * A map that contains all stored DHTObjects of this node, wrapped in an
	 * instance of DHTListener. This allows for later addition of other
	 * DHT-Related services.
	 */
	private DHTListener dht = new SimpleDHTService();
	
	private KBRLookupProvider<PastryID, PastryContact> kbrProvider;

	private KBRListener kbrListener;

	// private final Map<PastryKey, DHTObject> storedObjects;

	protected PastryNode(TransLayer translayer) {
		super(new PastryID(
				translayer.getLocalTransInfo(PastryConstants.PASTRY_PORT)),
				PastryConstants.PASTRY_PORT);

		this.transLayer = translayer;

		contact = new PastryContact(getOverlayID(), getTransLayer()
				.getLocalTransInfo(PastryConstants.PASTRY_PORT));


		// Register the message handler at the transport layer
		msgHandler = new PastryMessageHandler(this);
		getTransLayer().addTransMsgListener(msgHandler, getPort());

		// Initialize node state
		leafSet = new LeafSet(this);
		neighborhoodSet = new NeighborhoodSet(this);
		routingTable = new PastryRoutingTable(this);
		// storedObjects = new HashMap<PastryKey, DHTObject>();

		// The initial peer status is "ABSENT"
		setPeerStatus(PeerStatus.ABSENT);
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {

			if (this.wantsToBePresent) {
				// Do a rejoin
				join(new OperationCallback<Object>() {

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						// TODO What to do after a successful rejoin
					}

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// TODO Initialize a rejoin after a random wait!?
					}
				});
			}
		} else if (ce.isOffline()) {
			// Reset the node's state
			reset();
		}
	}

	/**
	 * Resets the node to a unconnected state. This resets all state information
	 * of the node to the initial state.
	 */
	private void reset() {
		// Remove node as bootstrap node
		PastryBootstrapManager.getInstance().unregisterNode(this);

		// Reset the peers state
		routingTable.reset();
		leafSet.reset();
		neighborhoodSet.reset();
		// storedObjects.clear();
		dht.getDHTEntries().clear();

		// Reset the status
		setPeerStatus(PeerStatus.ABSENT);
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	public boolean absentCausedByChurn() {
		return (getPeerStatus() == PeerStatus.ABSENT) && wantsToBePresent;
	}

	public PastryContact getNextHop(PastryID id) {
		return getNextHop(id, new LinkedList<PastryContact>());
	}

	public PastryContact getNextHop(PastryID id,
			LinkedList<PastryContact> exclude) {

		PastryContact nextHop = null;

		if (leafSet.isInRange(id)) {
			// Use leaf set
			nextHop = leafSet.getNumericallyClosestContact(id, exclude);
		} else {
			// Use routing table
			nextHop = routingTable
					.getContactWithLongerCommonPrefix(id, exclude);

			if (nextHop == null) {
				// Use overall numerically closest node

				PastryContact closestNode;

				LinkedList<PastryContact> closest = new LinkedList<PastryContact>();
				closestNode = leafSet.getNumericallyClosestContact(id, exclude);
				if (closestNode != null)
					closest.add(closestNode);
				closestNode = routingTable.getNumericallyClosest(id, exclude);
				if (closestNode != null)
					closest.add(closestNode);
				closestNode = neighborhoodSet.getNumericallyClosestContact(id,
						exclude);
				if (closestNode != null)
					closest.add(closestNode);

				nextHop = StateHelpers.getClosestContact(id, closest);
			}
		}
		return nextHop;
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		if (getPeerStatus() != PeerStatus.ABSENT)
			return -1;

		setPeerStatus(PeerStatus.TO_JOIN);
		this.wantsToBePresent = true;

		JoinOperation op = new JoinOperation(this, callback);
		op.scheduleImmediately();

		return op.getOperationID();
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		if (getPeerStatus() == PeerStatus.ABSENT)
			return -1;

		setPeerStatus(PeerStatus.ABSENT);
		this.wantsToBePresent = false;

		/*
		 * Actually, Pastry does not define any leave-Mechanism. It instead
		 * relies on the "lazy-repair" provided by its RoutingTable
		 */

		return 0;
	}

	@Override
	public int store(OverlayKey key, DHTObject obj,
			OperationCallback<Set<PastryContact>> callback) {
		// Do not store the value if the node is absent
		if (getPeerStatus() != PeerStatus.ABSENT)
			return -1;

		StoreOperation op = new StoreOperation(this,
				((PastryKey) key).getCorrespondingId(), obj, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public void registerDHTListener(DHTListener listener) {
		// currently only one DHT-Listener is used
		this.dht = listener;
	}

	@Override
	public int valueLookup(OverlayKey key, OperationCallback<DHTObject> callback) {
		// Do not look the value up if the node is absent
		if (getPeerStatus() != PeerStatus.ABSENT)
			return -1;

		ValueLookupOperation op = new ValueLookupOperation(this,
				((PastryKey) key).getCorrespondingId(), callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int nodeLookup(OverlayKey key,
			OperationCallback<List<PastryContact>> callback,
			boolean returnSingleNode) {

		// Do not lookup if the node is absent
		if (getPeerStatus() != PeerStatus.ABSENT)
			return -1;

		if (!(key instanceof PastryKey))
			return -1;

		// If the key is a pastry key, schedule a new lookup
		LookupOperation op = new LookupOperation(this,
				((PastryKey) key).getCorrespondingId(), callback);
		op.scheduleImmediately();

		return op.getOperationID();
	}

	/**
	 * @return the nodes overlay contact
	 */
	public PastryContact getOverlayContact() {
		return contact;
	}

	public PastryMessageHandler getMsgHandler() {
		return msgHandler;
	}

	public boolean wantsToBePresent() {
		return wantsToBePresent;
	}

	public boolean isResponsibleFor(PastryKey key) {
		PastryContact nearestInLeafSet = leafSet
				.getNumericallyClosestContact(key.getCorrespondingId());

		if (nearestInLeafSet.equals(getOverlayContact()))
			return true;
		return false;
	}

	public void addContact(PastryContact newContact) {
		LinkedList<PastryContact> l = new LinkedList<PastryContact>();
		l.add(newContact);
		addContacts(l);
	}

	public void addContacts(Collection<PastryContact> newContacts) {
		leafSet.putAll(newContacts);
		neighborhoodSet.putAll(newContacts);
		routingTable.insertAll(newContacts);
	}

	public Collection<PastryContact> getLeafSetNodes() {
		LinkedHashSet<PastryContact> neighbors = new LinkedHashSet<PastryContact>();

		for (PastryContact c : leafSet) {
			neighbors.add(c);
		}
		return neighbors;
	}

	public Collection<PastryContact> getNeighborSetNodes() {
		LinkedHashSet<PastryContact> neighbors = new LinkedHashSet<PastryContact>();

		for (PastryContact c : neighborhoodSet) {
			neighbors.add(c);
		}
		return neighbors;
	}

	public Collection<PastryContact> getRoutingTableNodes() {
		LinkedHashSet<PastryContact> neighbors = new LinkedHashSet<PastryContact>();

		for (PastryContact c : routingTable) {
			neighbors.add(c);
		}
		return neighbors;
	}

	public Collection<PastryContact> getAllNeighbors() {
		LinkedHashSet<PastryContact> neighbors = new LinkedHashSet<PastryContact>();

		neighbors.addAll(getLeafSetNodes());
		neighbors.addAll(getNeighborSetNodes());
		neighbors.addAll(getRoutingTableNodes());
		neighbors.add(getOverlayContact());

		return neighbors;
	}

	public int getNumOfAllNeighbors() {
		return getAllNeighbors().size();
	}

	/**
	 * Remove contact from node state (leaf set, neighbor set, routing table).
	 * This may cause the triggering of the search for replacements, if needed.
	 * 
	 * @param c
	 *            the contact to be removed
	 */
	public void removeNeighbor(PastryContact c) {
		leafSet.removeAndSubstitute(c);
		neighborhoodSet.removeAndSubstitute(c);
		routingTable.remove(c);
	}

	public LeafSet getLeafSet() {
		return leafSet;
	}

	public DHTListener getDHT() {
		return dht;
	}

	/**
	 * Registers a currently running operation for later retrieval through its
	 * operation id.
	 * 
	 * @param operation
	 *            The operation to be registered
	 */
	public void registerOperation(AbstractPastryOperation<?> operation) {
		this.registeredOperations.put(operation.getOperationID(), operation);
	}

	/**
	 * Unregisters a registered operation.
	 * 
	 * @param operationId
	 *            The operation id of the registered operation
	 * @return The registered operation
	 */
	public AbstractPastryOperation<?> unregisterOperation(Integer operationId) {
		return this.registeredOperations.remove(operationId);
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return new INeighborDeterminator() {

			@Override
			public Collection<OverlayContact> getNeighbors() {
				return Collections
						.unmodifiableList(new LinkedList<OverlayContact>(
								getAllNeighbors()));
			}
		};
	}

	/**
	 * get Pastry's routing Table
	 * 
	 * @return
	 */
	public PastryRoutingTable getPastryRoutingTable() {
		return routingTable;
	}

	/*
	 * KBR-Interface
	 */


	@Override
	public void route(OverlayKey key, Message msg, PastryContact hint) {
		if (getPastryRoutingTable() == null
				|| (key != null && !(key instanceof PastryKey)))
			return;

		PastryContact nextHop = null;
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
		nextHop = (PastryContact) info.getNextHopAgent();

		if (nextHop != null) {
			/*
			 * IMPORTANT: KBR-Communication does not use PastryMessageHandler,
			 * as it would use SendAndWait without ever noticing a reply, as
			 * ForwardMessage does not implement PastryBaseMessage.
			 */
			ForwardMsg fm = new ForwardMsg(getOverlayID(),
					nextHop.getOverlayID(), key, msg);
			getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
					TransProtocol.UDP);
		}
	}

	@Override
	public List<PastryContact> local_lookup(OverlayKey key, int num) {
		List<PastryContact> closest = new Vector<PastryContact>();
		for (int i = 0; i < num; i++) {
			PastryContact nextContact = getPastryRoutingTable()
					.getNumericallyClosest(new PastryID((PastryKey) key),
							closest);
			if (nextContact == null)
				return closest;
			closest.add(nextContact);
		}
		return closest;
	}

	@Override
	public List<PastryContact> replicaSet(OverlayKey key, int maxRank) {
		return new Vector<PastryContact>(getLeafSetNodes());
	}

	@Override
	public List<PastryContact> neighborSet(int num) {
		List<PastryContact> contacts = new Vector<PastryContact>();
		int in = 0;
		for (PastryContact contact : getAllNeighbors()) {
			contacts.add(contact);
			in++;
			if (in >= num)
				return contacts;
		}
		return contacts;
	}

	@Override
	public PastryID[] range(PastryContact contact, int rank) {
		/*
		 * what exactly is rank supposed to mean? Is this Method needed at all?
		 * It seems not to be used by any of the KBR-Nodes...
		 * 
		 * FIXME: Check meaning of rank with KBR-Paper!
		 */
		throw new UnsupportedOperationException(
				"range() is not yet implemented in Pastry. Check, if it is needed at all?");
	}

	@Override
	public boolean isRootOf(OverlayKey key) {
		return isResponsibleFor((PastryKey) key);
	}

	@Override
	public void setKBRListener(KBRListener listener) {
		this.kbrListener = listener;
		KBRMsgHandler<PastryID, PastryContact> msgHandler = new KBRMsgHandler<PastryID, PastryContact>(
				this, this, kbrListener);
		kbrProvider = msgHandler.getLookupProvider();
	}

	@Override
	public OverlayKey getNewOverlayKey(int rank) {
		return new PastryKey(new BigInteger(((Integer) rank).toString()));
	}

	@Override
	public OverlayKey getRandomOverlayKey() {
		return getNewOverlayKey(Simulator.getRandom().nextInt());
	}

	@Override
	public OverlayContact<PastryID> getLocalOverlayContact() {
		return new PastryContact(getOverlayID(), getTransLayer()
				.getLocalTransInfo(getPort()));
	}

	@Override
	public PastryContact getOverlayContact(OverlayID id, TransInfo transinfo) {
		return new PastryContact((PastryID) id, transinfo);
	}

	@Override
	public void hadContactTo(OverlayContact<PastryID> contact) {
		addContact((PastryContact) contact);
	}

	@Override
	public KBRLookupProvider<PastryID, PastryContact> getKbrLookupProvider() {
		return kbrProvider;
	}

}
