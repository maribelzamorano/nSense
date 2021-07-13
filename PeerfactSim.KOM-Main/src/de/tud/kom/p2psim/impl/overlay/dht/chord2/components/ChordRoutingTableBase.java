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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.CheckPredecessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.CheckSuccessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.MessageTimer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.UpdateDirectSuccessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyPredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifySuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.UpdateFingerPointOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.MathHelper;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.RoutingTableContructor;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This class provide the basic functionalities of Chord routing table.
 * RoutingTable provide the functionality to lookup and forward message more
 * efficiently. In Chord Overlay, a node respond for interval from next direct
 * predecessor Id (exclusive) to its id (inclusive)
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class ChordRoutingTableBase implements Serializable,
		INeighborDeterminator {

	private static Logger log = SimLogger
			.getLogger(ChordRoutingTableBase.class);

	private boolean isActive = true;

	/**
	 * number of finger table entry
	 */
	protected final int bitLength = ChordID.KEY_BIT_LENGTH;

	// from index 1 to bitLength
	protected ChordContact[] finger;

	/**
	 * list of direct predecessors
	 */
	protected LinkedList<ChordContact> predecessorList = null;

	/**
	 * list of direct successors
	 */
	protected LinkedList<ChordContact> successorList = null;

	protected transient ChordNode masterNode;

	/**
	 * index of finger table, which will be updated in next phase
	 */
	private int nextUpdateFingerPoint = 1;

	public ChordRoutingTableBase(ChordNode masterNode,
			ChordContact predecessor, ChordContact successor,
			Set<ChordContact> succFingerTable) {

		this.masterNode = masterNode;
		successorList = new LinkedList<ChordContact>();
		predecessorList = new LinkedList<ChordContact>();
		successorList.add(successor);
		predecessorList.add(predecessor);

		Set<ChordContact> nodeList;
		if (succFingerTable == null) {
			nodeList = new HashSet<ChordContact>();
		} else {
			nodeList = new HashSet<ChordContact>(succFingerTable);
		}
		nodeList.add(masterNode.getLocalChordContact());
		nodeList.add(successor);
		nodeList.add(predecessor);
		finger = RoutingTableContructor.getFingerTable(
				masterNode.getLocalChordContact(), nodeList);

		// periodically check successor and finger table
		// update successor and predecessor immediately!!!
		new UpdateDirectSuccessorOperation(masterNode,
				masterNode.getOperationListener()).scheduleImmediately();
		new UpdateFingerPointOperation(masterNode,
				masterNode.getOperationListener()).scheduleImmediately();
		new CheckSuccessorOperation(masterNode).scheduleImmediately();
		new CheckPredecessorOperation(masterNode).scheduleImmediately();
	}

	/**
	 * Returns the closest finger preceding id n.closest_preceding_finger(id)
	 * for i= m-1 downto 0 if(finger[i].node in (n,id) return finger[i].node
	 * return n
	 * 
	 * @param id
	 * @return the point in finger table, which is closest by the input id
	 * 
	 */
	public ChordContact getClosestPrecedingFinger(ChordID id) {

		for (int index = bitLength - 1; index >= 0; index--) {
			if (finger[index].getOverlayID().between(masterNode.getOverlayID(),
					id)) {
				return finger[index];
			}
		}
		return masterNode.getLocalChordContact();
	}

	/**
	 * Returns the num closest preceding fingers
	 * 
	 * @param id
	 * @param num
	 * @return
	 */
	public List<ChordContact> getClosestPrecedingFingers(ChordID id, int num) {

		List<ChordContact> closestPrecedingFingers = new LinkedList<ChordContact>();

		for (int index = bitLength - 1; index >= 0; index--) {
			if (finger[index].getOverlayID().between(masterNode.getOverlayID(),
					id)) {
				closestPrecedingFingers.add(finger[index]);
				if (closestPrecedingFingers.size() >= num)
					break;
			}
		}
		if (closestPrecedingFingers.isEmpty())
			closestPrecedingFingers.add(masterNode.getLocalChordContact());
		return closestPrecedingFingers;
	}

	/**
	 * notify of changing next direct predecessor
	 * 
	 * @param newPredecessor
	 */
	public void updatePredecessor(ChordContact newPredecessor) {
		log.debug("node id = " + masterNode.getOverlayID()
				+ " old predecessor = " + getPredecessor()
				+ " new predecessor = " + newPredecessor);
		if (newPredecessor.equals(masterNode.getLocalChordContact())) {
			return;
		}
		// this case occurs only if masterNode is the first Node in ring
		if (getPredecessor().equals(masterNode.getLocalChordContact())) {
			setPredecessor(newPredecessor);

		} else if (newPredecessor.between(getPredecessor(),
				masterNode.getLocalChordContact())) {
			setPredecessor(newPredecessor);
		}
	}

	/**
	 * notify of changing next direct successor
	 * 
	 * @param newSuccessor
	 */
	public void updateSuccessor(ChordContact newSuccessor) {
		log.debug("node id = " + masterNode.getOverlayID()
				+ " old successor = " + getSuccessor() + " new successor = "
				+ newSuccessor);
		if (newSuccessor.equals(masterNode.getLocalChordContact())) {
			return;
		}
		// this case occurs only if masterNode is the first Node in ring
		if (getSuccessor().equals(masterNode.getLocalChordContact())) {
			setSuccessor(newSuccessor);

		} else if (newSuccessor.between(masterNode.getLocalChordContact(),
				getSuccessor())) {
			setSuccessor(newSuccessor);
		}

	}

	/**
	 * This method is called to deliver current updated predecessor of next
	 * direct successor
	 * 
	 * @param successor
	 * @param predOfSucc
	 *            : null if successor was off-line
	 */
	public void updatePredecessorOfSuccessor(ChordContact successor,
			ChordContact predOfSucc) {

		log.debug("Node " + masterNode + " Predecessor Of Successor "
				+ predOfSucc);
		if (!successor.equals(getSuccessor())) {
			log.info("check pred of out-of-date succ node = " + masterNode);
			return;
		}
		if (predOfSucc != null) {
			// the node is predecessor of it's successor => nothing to do
			if (predOfSucc.equals(masterNode.getLocalChordContact())) {
				return;
			}

			// successor consider that it is alone
			else if (predOfSucc.equals(getSuccessor())) {
				// notify successor that this node is its new predecessor
				NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
						masterNode.getLocalChordContact(), getSuccessor(),
						masterNode.getLocalChordContact().clone());
				// masterNode.getTransLayer().send(msg,
				// getSuccessor().getTransInfo(), masterNode.getPort(),
				// ChordConfiguration.TRANSPORT_PROTOCOL);
				sendAndWait(msg, getSuccessor());
			}

			// predecessor of successor is really next neighbor in Chord ring
			else if (predOfSucc.between(masterNode.getLocalChordContact(),
					getSuccessor())) {
				updateSuccessor(predOfSucc);
			}

			// inform successor about myself
			else {
				// notify successor that this node is its new predecessor
				NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
						masterNode.getLocalChordContact(), getSuccessor(),
						masterNode.getLocalChordContact().clone());
				// masterNode.getTransLayer().send(msg,
				// getSuccessor().getTransInfo(), masterNode.getPort(),
				// ChordConfiguration.TRANSPORT_PROTOCOL);
				sendAndWait(msg, getSuccessor());
			}
		} else {
			log.info("inform offline direct succ failed");
			ChordContact oldSucc = getSuccessor();
			receiveOfflineEvent(oldSucc);
		}

	}

	/**
	 * replace finger point by new value
	 * 
	 * @param entryIndex
	 * @param newClosestSucc
	 */
	public void setFingerPoint(int entryIndex, ChordContact newClosestSucc) {
		log.debug("update finger node = " + masterNode + " entryindex = "
				+ entryIndex + " point " + getPointAddress(entryIndex)
				+ finger[entryIndex] + " " + newClosestSucc);

		if (newClosestSucc == null) {
			log.info("update finger failed node = " + masterNode + " point "
					+ getPointAddress(entryIndex));
			return;
		}
		finger[entryIndex] = newClosestSucc;

		// check for next points
		while (newClosestSucc.getOverlayID().between(
				getPointAddress(nextUpdateFingerPoint),
				finger[nextUpdateFingerPoint].getOverlayID())
				|| newClosestSucc.equals(finger[nextUpdateFingerPoint])) {

			log.trace("next point has the same successor index = "
					+ nextUpdateFingerPoint + " point "
					+ getPointAddress(nextUpdateFingerPoint) + " old value "
					+ finger[nextUpdateFingerPoint] + " new value "
					+ newClosestSucc);
			finger[nextUpdateFingerPoint] = newClosestSucc;
			incNextUpdateFingerPoint();
			// prevent endless loop cause the node is alone
			if (nextUpdateFingerPoint == entryIndex) {
				log.info("updated whole finger table node = " + masterNode);
				break;
			}
		}
		log.trace("break check next point" + " index = "
				+ nextUpdateFingerPoint + " point "
				+ getPointAddress(nextUpdateFingerPoint)
				+ finger[nextUpdateFingerPoint] + " " + newClosestSucc);
	}

	public boolean responsibleFor(ChordID key) {
		ChordContact predecessor = getPredecessor();

		if (predecessor != null) {
			if ((key.between(predecessor.getOverlayID(),
					masterNode.getOverlayID()))
					|| (key.equals(masterNode.getOverlayID()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * refresh finger table when receive joining event of new node
	 * 
	 * @param newNode
	 */
	public void refreshFingerTable(ChordContact newNode) {
		log.debug("refreshFingerTable new node = " + newNode);
		// update finger table
		for (int index = 0; index < bitLength; index++) {

			if (newNode.getOverlayID().between(getPointAddress(index),
					finger[index].getOverlayID())) {
				log.trace("node " + masterNode + " replace index "
						+ getPointAddress(index) + " old succ "
						+ finger[index].getOverlayID() + " by "
						+ newNode.getOverlayID());
				finger[index] = newNode;
			} else {
				log.trace("node " + masterNode + " index "
						+ getPointAddress(index) + " value "
						+ finger[index].getOverlayID());
			}
		}
	}

	/**
	 * @param index
	 * @return start address of i-th finger point
	 */
	public ChordID getPointAddress(int index) {
		BigInteger point = MathHelper.getFingerStartValue(masterNode
				.getOverlayID().getValue(), index);
		return new ChordID(point);
	}

	/**
	 * refresh finger table when receive leaving event of node
	 * 
	 * @param offlineNode
	 */
	protected abstract void receiveOfflineEvent(ChordContact offlineNode);

	public ChordContact[] copyFingerTable() {
		return finger.clone();
	}

	public ChordContact getFingerEntry(int index) {
		return finger[index];
	}

	public ChordNode getMasterNode() {
		return masterNode;
	}

	protected void setPredecessor(ChordContact newPredecessor) {
		log.debug("node " + masterNode + " old pred " + getPredecessor()
				+ " new pred " + newPredecessor);
		ChordContact oldPredecessor = predecessorList.get(0);

		predecessorList.set(0, newPredecessor);

		// refreshFingerTable(newPredecessor); --> Not needed in this case

		if (oldPredecessor.compareTo((masterNode.getLocalChordContact())) != 0) {
			NotifySuccessorMsg msg = new NotifySuccessorMsg(
					masterNode.getLocalChordContact(), oldPredecessor,
					newPredecessor);
			sendAndWait(msg, oldPredecessor);
		} else {
			updatePredecessor(newPredecessor);
		}
	}

	protected void setSuccessor(ChordContact newSuccessor) {
		log.debug("node " + masterNode + " old succ " + getSuccessor()
				+ " new succ " + newSuccessor);
		ChordContact oldSuccessor = successorList.get(0);

		successorList.set(0, newSuccessor);

		refreshFingerTable(newSuccessor);

		if (oldSuccessor.compareTo((masterNode.getLocalChordContact())) != 0) {
			NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
					masterNode.getLocalChordContact(), oldSuccessor,
					newSuccessor);
			sendAndWait(msg, oldSuccessor);
		} else {
			updateSuccessor(newSuccessor);
		}
	}

	private void sendAndWait(Message msg, ChordContact receiver) {
		if (!isActive)
			return;

		MessageTimer messageTimer = new MessageTimer(masterNode, msg, receiver);
		masterNode.getTransLayer().sendAndWait(msg, receiver.getTransInfo(),
				masterNode.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
				messageTimer, ChordConfiguration.MESSAGE_TIMEOUT);
	}

	public ChordContact getPredecessor() {
		return predecessorList.getFirst();
	}

	public List<ChordContact> getPredecessors() {
		return predecessorList;
	}

	public ChordContact getSuccessor() {
		return successorList.getFirst();
	}

	public List<ChordContact> getSuccessors() {
		return successorList;
	}

	public int getNextUpdateFingerPoint() {
		return nextUpdateFingerPoint;
	}

	public void incNextUpdateFingerPoint() {
		nextUpdateFingerPoint++;
		nextUpdateFingerPoint = nextUpdateFingerPoint % bitLength;
	}

	public boolean isNodeItsOwnSuccessor() {
		return getSuccessor().getOverlayID().compareTo(
				masterNode.getOverlayID()) == 0;
	}

	public boolean isNodeItsOwnSPredecessor() {
		return getPredecessor().getOverlayID().compareTo(
				masterNode.getOverlayID()) == 0;
	}

	public void setInactive() {
		isActive = false;
	}

	public boolean isActive() {
		return isActive;
	}

	@Override
	public Collection<OverlayContact> getNeighbors() {
		List<OverlayContact> contacts = new ArrayList<OverlayContact>(
				predecessorList.size() + successorList.size());
		contacts.addAll(successorList);
		contacts.addAll(predecessorList);
		for (int i = 0; i < finger.length; i++) {
			if (!contacts.contains(finger[i])) {
				contacts.add(finger[i]);
			}
		}
		return contacts;
	}
}
