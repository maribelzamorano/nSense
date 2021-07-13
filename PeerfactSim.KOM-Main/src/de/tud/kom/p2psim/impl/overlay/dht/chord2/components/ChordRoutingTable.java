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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.CheckPredecessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.CheckSuccessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.MessageTimer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyOfflineMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The extension functionalities will be implemented in this class
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordRoutingTable extends ChordRoutingTableBase implements
		Serializable {

	private static Logger log = SimLogger.getLogger(ChordRoutingTable.class);

	/**
	 * number of stored successor and predecessor
	 */
	private final int num_Neighbours = ChordConfiguration.STORED_NEIGHBOURS;

	/**
	 * successor and predecessor, which will be updated in next Stabilize phase
	 */
	private transient ChordContact nextUpdatePredecessor, nextUpdateSuccessor;

	public ChordRoutingTable(ChordNode _masterNode, ChordContact predecessor,
			ChordContact successor, Set<ChordContact> succFingerTable) {
		super(_masterNode, predecessor, successor, succFingerTable);

	}

	// it should be protected
	@Override
	public void receiveOfflineEvent(ChordContact offlineNode) {
		log.info("receive offline event node = " + masterNode
				+ " offline node = " + offlineNode + "at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		log.info("succ " + Arrays.toString(successorList.toArray()));
		log.info("pred " + Arrays.toString(predecessorList.toArray()));
		boolean isDirectSucc = getSuccessor().equals(offlineNode);
		boolean isDirectPred = getPredecessor().equals(offlineNode);

		// check successor and next update successor
		int index = successorList.indexOf(offlineNode);
		if (index >= 0) {
			if (offlineNode.equals(nextUpdateSuccessor)) {
				int nextIndex = (index + 1) % successorList.size();
				nextUpdateSuccessor = successorList.get(nextIndex);
			}
			successorList.remove(offlineNode);
		}

		// check predecessor and next update predecessor
		index = predecessorList.indexOf(offlineNode);
		if (index >= 0) {
			if (offlineNode.equals(nextUpdatePredecessor)) {
				int nextIndex = (index + 1) % predecessorList.size();
				nextUpdatePredecessor = predecessorList.get(nextIndex);
			}
			predecessorList.remove(offlineNode);
		}

		// check finger table
		updateFingerTable(offlineNode);

		// check is successorList and predecessorList empty?
		if (successorList.size() == 0) {
			successorList.add(finger[0]);
		}
		if (predecessorList.size() == 0) {
			predecessorList.add(finger[bitLength - 1]);
		}

		// notify next direct successor and predecessor
		// update next successor
		if (isDirectSucc) {
			notifyOfflineEvent(getSuccessor(), offlineNode);
		}
		// update next predecessor
		if (isDirectPred) {
			notifyOfflineEvent(getPredecessor(), offlineNode);
		}
	}

	private void updateFingerTable(ChordContact offlineNode) {
		ChordContact nextSucc = masterNode.getLocalChordContact();
		for (int index = bitLength - 1; index >= 0; index--) {
			if (offlineNode.equals(finger[index])) {
				finger[index] = nextSucc;
			} else {
				nextSucc = finger[index];
			}
		}
	}

	/**
	 * This method is called to deliver new successor of specified successor
	 * 
	 * @param successor
	 * @param succOfSuccessor
	 *            null if successor was off-line
	 */
	public void updateDistantSuccessor(ChordContact successor,
			ChordContact succOfSuccessor) {

		if(!isActive())
			return;
		
		if (succOfSuccessor == null) {
			log.info("Inform offline Distant Successor null");
			// successor offline
			receiveOfflineEvent(successor);
		} else {
			if (!successorList.contains(successor)) {
				nextUpdateSuccessor = successorList.getFirst();
			} else {
				int index = successorList.indexOf(successor);

				// check next successors
				while ((index + 1 < successorList.size())
						&& successorList.get(index + 1).between(successor,
								succOfSuccessor)) {
					successorList.remove(index + 1);
				}
				if (index + 1 == successorList.size()
						|| !successorList.get(index + 1)
								.equals(succOfSuccessor)) {
					successorList.add(index + 1, succOfSuccessor);
				}

				// calculate next update
				if (index + 1 == num_Neighbours) {
					nextUpdateSuccessor = successorList.getFirst();
				} else {
					nextUpdateSuccessor = successorList.get(index + 1);
				}
			}

			if (successorList.size() > num_Neighbours) {
				successorList = new LinkedList<ChordContact>(successorList
						.subList(0, num_Neighbours));
			}

		}
		
		new CheckSuccessorOperation(masterNode)
				.scheduleWithDelay(ChordConfiguration.UPDATE_NEIGHBOURS_INTERVAL);
	}

	/**
	 * This method is call to deliver new predecessor of a specified predecessor
	 * 
	 * @param predecessor
	 * @param predOfPredecessor
	 *            null if predecessor was off-line
	 */
	public void updateDistantPredecessor(ChordContact predecessor,
			ChordContact predOfPredecessor) {

		if(!isActive())
			return;
		
		new CheckPredecessorOperation(masterNode)
				.scheduleWithDelay(ChordConfiguration.UPDATE_NEIGHBOURS_INTERVAL);

		if (predOfPredecessor == null) {
			// predecessor offline
			log.info("Inform offline Distant Predecessor offline");
			receiveOfflineEvent(predecessor);
		} else {
			if (!predecessorList.contains(predecessor)) {
				nextUpdatePredecessor = predecessorList.getFirst();
			} else {
				int index = predecessorList.indexOf(predecessor);

				// check next successors
				while ((index + 1 < predecessorList.size())
						&& predecessorList.get(index + 1).between(
								predOfPredecessor, predecessor)) {
					predecessorList.remove(index + 1);
				}
				if (index + 1 == predecessorList.size()
						|| !predecessorList.get(index + 1).equals(
								predOfPredecessor)) {
					predecessorList.add(index + 1, predOfPredecessor);
				}

				// calculate next update
				if (index + 1 == num_Neighbours) {
					nextUpdatePredecessor = predecessorList.getFirst();
				} else {
					nextUpdatePredecessor = predecessorList.get(index + 1);
				}
			}

			if (predecessorList.size() > num_Neighbours) {
				predecessorList = new LinkedList<ChordContact>(predecessorList
						.subList(0, num_Neighbours));
			}

		}

	}

	/**
	 * notify neighbors to leaving event
	 * 
	 * @param notifier
	 * @param offliner
	 */
	protected void notifyOfflineEvent(ChordContact notifier,
			ChordContact offliner) {
		
		if(!isActive())
			return;
		
		NotifyOfflineMsg notifyOfflineMsg = new NotifyOfflineMsg(masterNode.getLocalChordContact(), notifier, offliner);
		MessageTimer messageTimer = new MessageTimer(masterNode,
				notifyOfflineMsg, notifier);

		if (!masterNode.getHost().getNetLayer().isOffline()
				&& masterNode.getOverlayID().compareTo(notifier.getOverlayID()) != 0)
			masterNode.getTransLayer().sendAndWait(notifyOfflineMsg,
					notifier.getTransInfo(), masterNode.getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL, messageTimer,
					ChordConfiguration.MESSAGE_TIMEOUT);
	}

	// Getters and Setters

	public ChordContact getDistantPredecessor(int index) {
		if (index >= predecessorList.size()) {
			return null;
		}
		return predecessorList.get(index);
	}

	public List<ChordContact> getAllDistantPredecessor() {
		return Collections.unmodifiableList(predecessorList);
	}

	public ChordContact getDistantSuccessor(int index) {
		if (index >= successorList.size()) {
			return null;
		}
		return successorList.get(index);
	}

	public List<ChordContact> getAllDistantSuccessor() {
		return Collections.unmodifiableList(successorList);
	}

	public ChordContact getNextUpdatePredecessor() {
		if (predecessorList.size() == 0) {
			log.error("all predcessors left node = " + masterNode);
		}
		if (!predecessorList.contains(nextUpdatePredecessor)) {
			nextUpdatePredecessor = predecessorList.get(0);
		}
		return nextUpdatePredecessor;
	}

	public ChordContact getNextUpdateSuccessor() {
		if (predecessorList.size() == 0) {
			log.error("all successors left node = " + masterNode);
		}
		if (!successorList.contains(nextUpdateSuccessor)) {
			nextUpdateSuccessor = successorList.get(0);
		}
		return nextUpdateSuccessor;
	}

}
