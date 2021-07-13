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


package de.tud.kom.p2psim.impl.overlay.dht.can;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.overlay.JoinLeaveOverlayNode;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.evaluation.GetDataOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.GiveNodeInfoOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.JoinOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.LeaveOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.LookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.LookupPeriodicallyOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.StoreOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.operations.TakeoverOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is the base for every peer in the CAN it saves all information of the
 * peer. It is the central of the peers. Implements DHTNode.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanNode extends AbstractOverlayNode implements
		JoinLeaveOverlayNode {
	private final static Logger log = SimLogger.getLogger(CanNode.class);

	private CanOverlayContact localContact = null;

	private final TransLayer transLayer;

	private CanMessageHandler msgHandler;

	private OperationCallback callback;

	private final OperationListener opListener;

	List<CanOverlayContact> neighbours;

	private CanBootstrapManager bootstrap;

	private String joiningTime;

	private CanOverlayContact[] VIDNeighbours;

	// ///////////////////////////
	// overload extension
	private List<CanOverlayContact> overloadedContacts;

	private CanOverlayContact mainNode;

	// ////////////////////////////////
	// takeover leave
	private TakeoverOperation takeoverOperation;

	private List<List<CanOverlayContact>> neighboursOfNeighbours;

	private List<CanOverlayContact[]> vidNeighboursOfNeighbours;

	private List<CanOverlayContact> leavingNeighbours;

	private List<CanArea> leavingArea;

	private List<CanOverlayContact> leavingReplyContacts; // also used for
															// takeover

	private List<CanOverlayContact[]> leavingReplyVIDNeighbours;

	private List<Object[]> leavingHashs;

	private List<CanOverlayContact> takeoverReply;

	private CanOverlayContact missingNode; // node left without a message

	// //////////////////////////////
	// lookup store
	private List<Object[]> storedHashs;

	private final Map<Integer, AbstractOperation> lookupStore = new HashMap<Integer, AbstractOperation>();

	// /////////////////////////////
	// Evaluation
	public GetDataOperation getDataOperation;

	/**
	 * Initiates the CanNode. Is called when the peer is created.
	 * 
	 * @param transLayer
	 *            the TransLayer of the peer
	 * @param port
	 */
	public CanNode(TransLayer transLayer, short port) {
		super(new CanOverlayID(transLayer.getLocalTransInfo(port)), port);
		overloadedContacts = new LinkedList<CanOverlayContact>();

		neighbours = new LinkedList<CanOverlayContact>();
		neighboursOfNeighbours = new LinkedList<List<CanOverlayContact>>();
		vidNeighboursOfNeighbours = new LinkedList<CanOverlayContact[]>();

		this.transLayer = transLayer;
		localContact = new CanOverlayContact(new CanOverlayID(
				transLayer.getLocalTransInfo(port)),
				transLayer.getLocalTransInfo(port), getDataOperation);

		// create message handler
		msgHandler = new CanMessageHandler(this);
		// add message handler to the listeners list
		this.getTransLayer().addTransMsgListener(this.msgHandler,
				this.getPort());

		this.VIDNeighbours = new CanOverlayContact[2];

		// assign the operation listener to this
		opListener = new OperationListener(this);

		leavingNeighbours = new LinkedList<CanOverlayContact>();
		leavingArea = new LinkedList<CanArea>();
		leavingReplyContacts = new LinkedList<CanOverlayContact>();
		leavingReplyVIDNeighbours = new LinkedList<CanOverlayContact[]>();
		leavingHashs = new LinkedList<Object[]>();

		takeoverOperation = new TakeoverOperation(this, callback);
		takeoverReply = new LinkedList<CanOverlayContact>();

		storedHashs = new LinkedList<Object[]>();
		log.debug(Simulator.getSimulatedRealtime() + " New CanNode initiated.");

		getDataOperation = new GetDataOperation(this);
	}

	// //////////////////////////////////////////////////////////////
	// start Operations
	/**
	 * Is used to join the CAN
	 */
	@Override
	public int join(OperationCallback callback) {
		// get the first active node in bootstrap, to join in the overlay
		log.warn(Simulator.getSimulatedRealtime() + " Node is joining.");
		this.callback = callback;
		JoinOperation join = new JoinOperation(this, callback);
		join.scheduleImmediately();
		return join.getOperationID();
	}

	/**
	 * Is used to leave the CAN
	 */
	@Override
	public int leave(OperationCallback callback) {
		LeaveOperation leave = new LeaveOperation(this, callback);
		leave.scheduleImmediately();
		return leave.getOperationID();
	}

	/**
	 * Calls the GiveNodeInfoOperation which shows all the data of the peer
	 * 
	 * @param callback
	 * @return
	 */
	public int giveNodeInfo(OperationCallback callback) {
		GiveNodeInfoOperation giveNeighbour = new GiveNodeInfoOperation(this,
				callback);
		giveNeighbour.scheduleImmediately();
		return giveNeighbour.getOperationID();
	}

	/**
	 * Sets the peer absent. Is used for debug.
	 * 
	 * @param callback
	 * @return
	 */
	public int setAbsent(OperationCallback callback) {
		log.debug("set node absent: "
				+ this.getLocalContact().getOverlayID().toString() + " "
				+ this.getLocalContact().getArea().toString() + " "
				+ this.getLocalContact().getArea().getVid().toString());
		this.setPeerStatus(PeerStatus.ABSENT);
		this.setAlive(false);
		return 0;
	}

	/**
	 * Stores a hash in the CAN
	 * 
	 * @param obj
	 *            String which should be saved
	 * @param callback
	 * @return
	 */
	public int store(String obj, OperationCallback callback) {
		DataID key = new DataID(obj);
		StoreOperation storeOperation = new StoreOperation(this, key, callback);
		storeOperation.scheduleImmediately();
		return 0;
	}

	/**
	 * Sends a lookupMsg.
	 * 
	 * @param data
	 *            String which should be found.
	 * @param callback
	 * @return
	 */
	public int valueLookup(String data, OperationCallback<DHTObject> callback) {
		DataID id = new DataID(data);
		LookupOperation lookupOperation = new LookupOperation(this, id,
				callback);
		lookupOperation.scheduleImmediately();
		return lookupOperation.getOperationID();
	}

	/**
	 * Starts a Operation which starts a periodical lookup.
	 * 
	 * @param callback
	 * @return
	 */
	public int lookupPeriodically(OperationCallback<DHTObject> callback) {
		LookupPeriodicallyOperation lookup = new LookupPeriodicallyOperation(
				this, callback);
		lookup.scheduleImmediately();
		return lookup.getOperationID();
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			if (this.getPeerStatus().equals(PeerStatus.PRESENT)) {
				this.setPeerStatus(PeerStatus.ABSENT);
				this.storedHashs = new LinkedList<Object[]>();
				this.stopTakeoverOperation();
				this.setAlive(false);
			}
		} else if (ce.isOnline()) {
			if (!this.getPeerStatus().equals(PeerStatus.PRESENT)) {
				neighbours = new LinkedList<CanOverlayContact>();
				neighboursOfNeighbours = new LinkedList<List<CanOverlayContact>>();
				vidNeighboursOfNeighbours = new LinkedList<CanOverlayContact[]>();

				localContact = new CanOverlayContact(new CanOverlayID(
						transLayer.getLocalTransInfo(this.getPort())),
						transLayer.getLocalTransInfo(this.getPort()),
						getDataOperation);

				this.VIDNeighbours = new CanOverlayContact[2];

				leavingNeighbours = new LinkedList<CanOverlayContact>();
				leavingArea = new LinkedList<CanArea>();
				leavingReplyContacts = new LinkedList<CanOverlayContact>();
				leavingReplyVIDNeighbours = new LinkedList<CanOverlayContact[]>();
				leavingHashs = new LinkedList<Object[]>();

				takeoverOperation = new TakeoverOperation(this, callback);
				takeoverReply = new LinkedList<CanOverlayContact>();

				storedHashs = new LinkedList<Object[]>();

				this.setPeerStatus(PeerStatus.TO_JOIN);
				this.join(callback);
			}
		}

	}

	// /////////////////////////////////////////////////////
	// basic methods
	public CanOverlayID getCanOverlayID() {
		return getLocalContact().getOverlayID();
	}

	/**
	 * gives the CanOverlayContact of the actual peer
	 * 
	 * @return CanOverlayContact
	 */
	public CanOverlayContact getLocalContact() {
		return localContact.clone();
	}

	public void setAlive(boolean live) {
		localContact.setAlive(live);
	}

	public void setNeighbours(List<CanOverlayContact> neighbours) {
		this.neighbours = neighbours;
		takeoverOperation.updateNode(this);
	}

	public List<CanOverlayContact> getNeighbours() {
		return this.neighbours;
	}

	/**
	 * removes a certain neighbour
	 * 
	 * @param remove
	 *            neighbour to remove
	 */
	public void removeNeighbour(CanOverlayContact remove) {
		for (int i = 0; i < neighbours.size(); i++) {
			if ((neighbours.get(i)).getOverlayID().toString()
					.equals(remove.getOverlayID().toString())) {
				neighbours.remove(i);
			}
		}
		removeNeighboursOfNeighbours(remove);
		removeVidNeighboursOfNeighbours(remove);
	}

	/**
	 * checks if the neighbour is in the list
	 * 
	 * @param compare
	 *            CanOverlayContact to check
	 * @return true if neighbour is in the list
	 */
	public boolean neighboursContain(CanOverlayContact compare) {
		for (int i = 0; i < neighbours.size(); i++) {
			if ((neighbours.get(i)).getOverlayID().equals(
					compare.getOverlayID()))
				return true;
		}
		return false;
	}

	public CanBootstrapManager getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(CanBootstrapManager bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public TransLayer getTransLayer() {
		return this.transLayer;
	}

	// //////////////////////////////////////////////////////////////
	// neighbours of neighbours methods
	/**
	 * checks if the neighbour is in the list
	 * 
	 * @param compare
	 *            CanOverlayID to check
	 * @return true if neighbour is in the list
	 */
	public boolean neighboursContain(CanOverlayID compare) {
		for (int i = 0; i < neighbours.size(); i++) {
			if ((neighbours.get(i)).getOverlayID().equals(compare))
				return true;
		}
		return false;
	}

	public List<CanOverlayContact[]> getVidNeighboursOfNeighbours() {
		return this.vidNeighboursOfNeighbours;
	}

	public void setVidNeighboursOfNeighbours(
			List<CanOverlayContact[]> vidNeighboursOfNeighbours) {
		this.vidNeighboursOfNeighbours = vidNeighboursOfNeighbours;
	}

	public void addToVidNeighboursOfNeighbours(CanOverlayContact neighbour,
			CanOverlayContact[] vidNeighboursOfNeighbours) {
		CanOverlayContact[] vidToSave = { neighbour,
				vidNeighboursOfNeighbours[0], vidNeighboursOfNeighbours[1] };
		this.vidNeighboursOfNeighbours.add(vidToSave);
	}

	/**
	 * remove the VID neighbours of a peer
	 * 
	 * @param neighbour
	 */
	public void removeVidNeighboursOfNeighbours(CanOverlayContact neighbour) {
		for (int i = 0; i < vidNeighboursOfNeighbours.size(); i++) {
			if (vidNeighboursOfNeighbours.get(i)[0].getArea().getVid()
					.toString().equals(neighbour.getArea().getVid().toString())
					|| vidNeighboursOfNeighbours.get(i)[0].getOverlayID()
							.toString()
							.equals(neighbour.getOverlayID().toString())) {
				vidNeighboursOfNeighbours.remove(i);
				i = -1;
			}
		}
	}

	/**
	 * removes a list of neighbour in the VID neighbours of neighbours list.
	 * 
	 * @param neighbourList
	 */
	public void removeVidNeighboursOfNeighbours(
			List<CanOverlayContact> neighbourList) {
		try {
			for (int i = 0; i < vidNeighboursOfNeighbours.size(); i++) {
				boolean contains = false;
				CanOverlayContact[] toTry = vidNeighboursOfNeighbours.get(i);
				for (int j = 0; j < neighbourList.size(); j++) {
					if (toTry[0]
							.getOverlayID()
							.toString()
							.equals(neighbourList.get(j).clone().getOverlayID()
									.toString()))
						contains = true;
				}
				if (!contains) {
					vidNeighboursOfNeighbours.remove(i);
					i = -1;
				}
			}
		} catch (Exception e) {
			// in case the list is null
		}
	}

	/**
	 * updates the VID neighbours of a peer.
	 * 
	 * @param neighbour
	 * @param neighboursOfNeighbours
	 */
	public void updateVidNeighboursOfNeighbours(CanOverlayContact neighbour,
			CanOverlayContact[] neighboursOfNeighbours) {
		try {
			removeVidNeighboursOfNeighbours(neighbour);
		} catch (Exception e) {
			// in case the list is null
		}
		addToVidNeighboursOfNeighbours(neighbour, neighboursOfNeighbours);
	}

	/**
	 * Gives the VID neighbours of a certain node.
	 * 
	 * @param neighbour
	 * @return
	 */
	public CanOverlayContact[] getVidNeighboursOfCertainNeighbour(
			CanOverlayContact neighbour) {
		CanOverlayContact[] output = new CanOverlayContact[2];
		for (int i = 0; i < vidNeighboursOfNeighbours.size(); i++) {
			if (vidNeighboursOfNeighbours.get(i)[0].getArea().getVid()
					.toString().equals(neighbour.getArea().getVid().toString())) {
				output[0] = vidNeighboursOfNeighbours.get(i)[1];
				output[1] = vidNeighboursOfNeighbours.get(i)[2];
			}
		}
		return output;
	}

	public List<List<CanOverlayContact>> getNeighboursOfNeighbours() {
		return this.neighboursOfNeighbours;
	}

	public void setNeighboursOfNeighbours(
			List<List<CanOverlayContact>> neighboursOfNeighbours) {
		this.neighboursOfNeighbours = neighboursOfNeighbours;
	}

	/**
	 * adds a peer to the neighbour of neighbours list
	 * 
	 * @param neighbour
	 * @param neighboursOfNeighbours
	 */
	public void addToNeighboursOfNeighbours(CanOverlayContact neighbour,
			List<CanOverlayContact> neighboursOfNeighbours) {
		try {
			List<CanOverlayContact> listToSave = new LinkedList<CanOverlayContact>();
			listToSave.add(neighbour);
			listToSave.addAll(neighboursOfNeighbours);
			this.neighboursOfNeighbours.add(listToSave);
		} catch (Exception e) {
			// in case the list is null
		}
	}

	/**
	 * removes a peer in the neighbour of neighbours list
	 * 
	 * @param neighbour
	 */
	public void removeNeighboursOfNeighbours(CanOverlayContact neighbour) {
		for (int i = 0; i < neighboursOfNeighbours.size(); i++) {
			if (neighboursOfNeighbours.get(i).get(0).getOverlayID().toString()
					.equals(neighbour.getOverlayID().toString())) {
				neighboursOfNeighbours.remove(i);
				i = -1;
			}
		}
	}

	/**
	 * removes a list of peers from the neighbours of neighbour list
	 * 
	 * @param neighbourList
	 */
	public void removeNeighboursOfNeighbours(
			List<CanOverlayContact> neighbourList) {
		try {
			for (int i = 0; i < neighboursOfNeighbours.size(); i++) {
				boolean contains = false;
				for (int j = 0; j < neighbourList.size(); j++) {
					List<CanOverlayContact> toTry = neighboursOfNeighbours
							.get(i);
					if (toTry
							.get(0)
							.getOverlayID()
							.toString()
							.equals(neighbourList.get(j).getOverlayID()
									.toString()))
						contains = true;
				}
				if (!contains) {
					vidNeighboursOfNeighbours.remove(i);
					i = -1;
				}
			}
		} catch (Exception e) {
			// in case the list is empty
		}
	}

	/**
	 * updates a peer in the neighbours of neighbour list
	 * 
	 * @param neighbour
	 * @param neighboursOfNeighbours
	 */
	public void updateNeighboursOfNeighbours(CanOverlayContact neighbour,
			List<CanOverlayContact> neighboursOfNeighbours) {
		try {
			removeNeighboursOfNeighbours(neighbour);
		} catch (Exception e) {
			// in case the list is empty
		}
		addToNeighboursOfNeighbours(neighbour, neighboursOfNeighbours);
	}

	/**
	 * gets the neighbous of a certain peer.
	 * 
	 * @param neighbour
	 * @return
	 */
	public List<CanOverlayContact> getNeighboursOfCertainNeighbour(
			CanOverlayContact neighbour) {
		List<CanOverlayContact> outputList = new LinkedList<CanOverlayContact>();
		for (int i = 0; i < neighboursOfNeighbours.size(); i++) {
			if (neighboursOfNeighbours.get(i).get(0).getOverlayID().toString()
					.equals(neighbour.getOverlayID().toString()))
				outputList = neighboursOfNeighbours.get(i);
		}
		try {
			outputList.remove(0);
		} catch (Exception e) {
			// in case the list is empty
		}
		return outputList;
	}

	public void showNeighboursOfNeighbour() {
		try {
			for (int x = 0; x < this.neighbours.size(); x++) {
				log.debug("own neighbours: "
						+ this.neighbours.get(x).getOverlayID().toString());
			}
		} catch (Exception e) {
			// in case the list is empty
		}

		for (int x = 0; x < this.getNeighboursOfNeighbours().size(); x++) {
			List<CanOverlayContact> toShow = this.getNeighboursOfNeighbours()
					.get(x);
			log.debug("neighbours of: "
					+ toShow.get(0).getOverlayID().toString());
			for (int y = 1; y < toShow.size(); y++)
				log.debug(toShow.get(y).getOverlayID().toString());
		}
	}

	// ////////////////////////////////////////
	// for overload extension
	public List<CanOverlayContact> getOverloadedContacts() {
		return overloadedContacts;
	}

	@SuppressWarnings("unchecked")
	public void setOverloadedContacts(List overloadedContacts) {
		this.overloadedContacts = overloadedContacts;
	}

	public int getOverloadNumber() {
		return CanConfig.overloadNumber;
	}

	public CanOverlayContact getMainNode() {
		return mainNode;
	}

	public void setMainNode(CanOverlayContact mainNode) {
		this.mainNode = mainNode;
	}

	public void setVIDNeigbours(CanOverlayContact[] vIDNeighbours) {
		this.VIDNeighbours = new CanOverlayContact[2];
		this.VIDNeighbours[0] = vIDNeighbours[0].clone();
		this.VIDNeighbours[1] = vIDNeighbours[1].clone();
	}

	/**
	 * sets both VID neighbours to null
	 */
	public void emptyVIDNeigbours() {
		this.VIDNeighbours = new CanOverlayContact[2];
	}

	public CanOverlayContact[] getVIDNeighbours() {
		return VIDNeighbours;
	}

	public void setArea(CanArea area) {
		this.localContact.setArea(area);
	}

	public void setVID(CanVID vid) {
		this.localContact.getArea().setVid(vid);
	}

	// ////////////////////////////////////////////
	// is used for leave and takeover to store the data of the other nodes
	// with the same parents

	/**
	 * adds the neighbours of a peer
	 */
	public void addLeavingNeighbours(List<CanOverlayContact> leavingNeighbours) {
		this.leavingNeighbours.addAll(leavingNeighbours);
	}

	/**
	 * adds a contact
	 * 
	 * @return
	 */
	public List<CanOverlayContact> getLeavingNeighbours() {
		return leavingNeighbours;
	}

	/**
	 * sets the list to null
	 */
	public void emptyLeavingNeighbours() {
		this.leavingNeighbours = new LinkedList<CanOverlayContact>();
	}

	/**
	 * adds a area
	 * 
	 * @param leavingArea
	 */
	public void addLeavingArea(CanArea leavingArea) {
		this.leavingArea.add(leavingArea);
	}

	public List<CanArea> getLeavingArea() {
		return leavingArea;
	}

	/**
	 * sets the list to null
	 */
	public void emptyLeavingArea() {
		this.leavingArea = new LinkedList<CanArea>();
	}

	public void addLeavingReplyContacts(CanOverlayContact leavingReplyContacts) {
		this.leavingReplyContacts.add(leavingReplyContacts);
	}

	public List<CanOverlayContact> getLeavingReplyContacts() {
		return leavingReplyContacts;
	}

	/**
	 * sets the list to null
	 */
	public void emptyLeavingReplyContacts() {
		this.leavingReplyContacts = new LinkedList<CanOverlayContact>();
	}

	public List<CanOverlayContact[]> getLeavingReplyVIDNeighbours() {
		return this.leavingReplyVIDNeighbours;
	}

	public void addLeavingReplyVIDNeighbours(CanOverlayContact[] vidNeighbour) {
		this.leavingReplyVIDNeighbours.add(vidNeighbour);
	}

	/**
	 * sets the list to null
	 */
	public void emptyLeavingReplyVIDNeighbours() {
		this.leavingReplyVIDNeighbours = new LinkedList<CanOverlayContact[]>();
	}

	public List<Object[]> getLeavingHash() {
		return this.leavingHashs;
	}

	public void addLeavingHash(List<Object[]> newLeavingHash) {
		if (newLeavingHash != null)
			this.leavingHashs.addAll(newLeavingHash);
	}

	/**
	 * sets the list to null
	 */
	public void emptyLeavingHashs() {
		this.leavingHashs = new LinkedList<Object[]>();
	}

	// /////////////////////////////////////////////////////////////
	// the TakeoverOperation handles the ping messages

	/**
	 * starts the TakeoverOperation
	 */
	public void startTakeoverOperation() {
		takeoverOperation = new TakeoverOperation(this, callback);
		takeoverOperation.scheduleWithDelay(CanConfig.waitTimeBetweenPing);
	}

	public TakeoverOperation getTakeoverOperation() {
		return takeoverOperation;
	}

	/**
	 * stops the TakeoverOperation
	 */
	public void stopTakeoverOperation() {
		takeoverOperation.stopOperation();
	}

	/**
	 * resumes the TakeoverOperation with delay
	 */
	public void resumeTakeoverOperation() {
		takeoverOperation.updateNode(this);
		takeoverOperation.resumeOperation();
	}

	/**
	 * resumes the TakoverOperation directly
	 */
	public void resumeDirectTakeoverOperation() {
		takeoverOperation.updateNode(this);
		takeoverOperation.resumeDirectOperation();
	}

	/**
	 * saves a missing peer and deletes it from the neighbour list
	 * 
	 * @param missingNode
	 */
	public void setMissingNode(CanOverlayContact missingNode) {
		this.missingNode = missingNode;
		if (missingNode != null) {
			List<CanOverlayContact> newNeighbours = getNeighbours();
			for (int i = 0; i < newNeighbours.size(); i++) {
				if (newNeighbours.get(i).getOverlayID().toString()
						.equals(missingNode.getOverlayID().toString()))
					newNeighbours.remove(i);
			}
			setNeighbours(newNeighbours);
		}
	}

	public CanOverlayContact getMissingNode() {
		return missingNode;
	}

	// /////////////////////////////////////////////////////////////////////
	// store and lookup

	/**
	 * Gives the next peer for the routing of lookup and store messages.
	 * 
	 * @param id
	 *            DataID the has value which should be routed to
	 * @return
	 */
	public CanOverlayContact routingNext(DataID id) { // Data ID
		CanOverlayContact next = null;

		if (id.includedInArea(this.localContact.getArea()))
			return this.localContact;

		for (int i = 0; i < neighbours.size(); i++) {
			if (id.includedInArea(neighbours.get(i).getArea())) {
				return neighbours.get(i);
			}
		}
		CanArea areaActualNode = this.getLocalContact().getArea();

		if (id.sameXValue(areaActualNode)) { // gleiche Höhe
			if (id.getYValue().intValue() > areaActualNode.getArea()[3]) { // area
																			// gr��er
																			// als
																			// aktuelle
				for (int i = 0; i < neighbours.size(); i++) {
					next = neighbours.get(i);
					if (id.getYValue().intValue() >= next.getArea().getArea()[3]
							&& next.isAlive()
							&& areaActualNode.getArea()[3] <= next.getArea()
									.getArea()[2])
						return next;
				}
			} else if (id.getYValue().intValue() <= areaActualNode.getArea()[2]) { // area
				// kleiner
				// als
				// aktuelle
				for (int i = 0; i < neighbours.size(); i++) {
					next = neighbours.get(i);
					if (id.getYValue().intValue() <= next.getArea().getArea()[3]
							&& next.isAlive()
							&& areaActualNode.getArea()[2] >= next.getArea()
									.getArea()[3])
						return next;
				}
			}
		} else if (id.getXValue().intValue() >= areaActualNode.getArea()[1]) { // area
																				// gr��er
																				// als
																				// aktuelle
			for (int i = 0; i < neighbours.size(); i++) {
				next = neighbours.get(i);
				if (id.getXValue().intValue() > next.getArea().getArea()[0]
						&& next.isAlive()
						&& areaActualNode.getArea()[1] <= next.getArea()
								.getArea()[0])
					return next;
			}
		} else if (id.getXValue().intValue() <= areaActualNode.getArea()[0]) { // area
																				// kleiner
																				// als
																				// aktuelle
			for (int i = 0; i < neighbours.size(); i++) {
				next = neighbours.get(i);

				if (id.getXValue().intValue() <= next.getArea().getArea()[1]
						&& next.isAlive()
						&& areaActualNode.getArea()[0] >= next.getArea()
								.getArea()[1])
					return next;
			}
		} else if (id.getYValue().intValue() >= areaActualNode.getArea()[3]) { // area
			// größer als aktuelle

			for (int i = 0; i < neighbours.size(); i++) {
				next = neighbours.get(i);
				if (id.getYValue().intValue() > next.getArea().getArea()[3]
						&& next.isAlive()
						&& areaActualNode.getArea()[3] <= next.getArea()
								.getArea()[2])
					return next;
			}
		} else
			return neighbours.get(Simulator.getRandom().nextInt(
					neighbours.size()));

		return next;
	}

	/**
	 * adds a hash and the conected CanOveralayContact
	 * 
	 * @param id
	 *            hash value
	 * @param contact
	 *            CanOverlayContact
	 */
	public void addStoredHashs(DataID id, CanOverlayContact contact) {
		Object[] store = { id, contact };
		this.storedHashs.add(store);
	}

	/**
	 * deletes a certain hash
	 * 
	 * @param id
	 *            hash value to delete
	 */
	public void deleteStoredHashs(DataID id) {
		for (int i = 0; i < storedHashs.size(); i++) {
			if (((DataID) storedHashs.get(i)[0]).toString().equals(
					id.toString())) {
				this.storedHashs.remove(i);
				i = -1;
			}

		}
	}

	/**
	 * deletes all the hash values from the contact
	 * 
	 * @param contact
	 */
	public void deleteStoredHashs(CanOverlayContact contact) {
		for (int i = 0; i < storedHashs.size(); i++) {
			if (((CanOverlayContact) storedHashs.get(i)[1]).toString().equals(
					contact.toString())) {
				this.storedHashs.remove(i);
				i = -1;
			}
		}
	}

	/**
	 * gives the CanOverlayContact to a hash, if it is saved in the list.
	 * 
	 * @param id
	 *            searched hash value
	 * @return
	 */
	public CanOverlayContact getStoredHashToID(DataID id) {
		for (int i = 0; i < storedHashs.size(); i++) {
			if (((DataID) storedHashs.get(i)[0]).toString().equals(
					id.toString()))
				return ((CanOverlayContact) storedHashs.get(i)[1]);
		}
		return null;
	}

	public List<Object[]> getStoredHashs() {
		return this.storedHashs;
	}

	public void setStoredHashs(List<Object[]> newHashs) {
		try {
			this.storedHashs = new LinkedList<Object[]>();
			this.storedHashs = newHashs;
		} catch (Exception e) {
			this.storedHashs = new LinkedList<Object[]>();
		}
		for (int i = 0; i < this.storedHashs.size(); i++) {
			if ((CanOverlayContact) this.storedHashs.get(i)[1] != null) {
				for (int j = 0; j < this.storedHashs.size(); j++) {
					if ((CanOverlayContact) this.storedHashs.get(j)[1] != null) {
						if (i != j
								&& ((DataID) this.storedHashs.get(i)[0])
										.getId()
										.toString()
										.equals(((DataID) this.storedHashs
												.get(j)[0]).getId().toString())
								&& ((CanOverlayContact) this.storedHashs.get(i)[1])
										.getOverlayID()
										.toString()
										.equals(((CanOverlayContact) this.storedHashs
												.get(j)[1]).getOverlayID()
												.toString())) {
							this.storedHashs.remove(i);
							i = -1;
							break;
						}
					}
				}
			}
		}
		for (int x = 0; x < newHashs.size(); x++)
			log.debug("hash " + ((DataID) newHashs.get(x)[0]).toString());
	}

	/**
	 * gives a map of all lookup and store Operations.
	 * 
	 * @return
	 */
	public Map<Integer, AbstractOperation> getLookupStore() {
		return this.lookupStore;
	}

	/**
	 * registers a new Lookup or store operation
	 * 
	 * @param id
	 * @param op
	 */
	public void registerLookupStore(Integer id, AbstractOperation op) {
		lookupStore.put(id, op);
	}

	/**
	 * removes a lookup or store Operation from the map.
	 * 
	 * @param cmdID
	 */
	public void lookupStoreFinished(Integer cmdID) {
		lookupStore.remove(cmdID);
	}

	// ///////////////////////////////////////////////////////////
	/**
	 * sets the time the peer joins
	 * 
	 * @param time
	 */
	public void setJoiningTime(String time) {
		this.joiningTime = time;
	}

	public String getJoiningTime() {
		return this.joiningTime;
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return new INeighborDeterminator() {
			
			@Override
			public Collection<OverlayContact> getNeighbors() {
				List<? extends OverlayContact> res = neighbours;
				return Collections.unmodifiableList(res);
			}
		};
	}
}
