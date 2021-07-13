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


package de.tud.kom.p2psim.impl.overlay.dht.can.operations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.PongMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation is started after a peer joins, it is responsible to ask all
 * neighbour every CanConfig.waitTimeBetweenPing*3 intervals if they are still
 * alive. It starts a TakeoverReply Operation for every neighbour.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverOperation extends AbstractOperation<CanNode, Object> {
	private final static Logger log = SimLogger.getLogger(CanNode.class);

	private CanNode node;

	private boolean stopOperation;

	private TakeoverReplyOperation takeoverReplyOperation;

	private int noAnswer;

	private OperationCallback<DHTObject> callback;

	private Map<Integer, AbstractOperation> takeover;

	/**
	 * starts the takeoverOperation, which pings the neighbours
	 * 
	 * @param component
	 *            CanNode
	 * @param callback
	 */
	public TakeoverOperation(CanNode component,
			OperationCallback<DHTObject> callback) {
		super(component);

		takeover = new HashMap<Integer, AbstractOperation>();
		stopOperation = false;
		node = this.getComponent();
		noAnswer = 0;
		this.callback = callback;
	}

	public void execute() {
		if (stopOperation == false) {
			if (node.getPeerStatus() == PeerStatus.PRESENT) {
				if (node.getNeighbours() != null) {
					for (int i = 0; i < node.getNeighbours().size(); i++) {
						takeoverReplyOperation = new TakeoverReplyOperation(
								node, node.getNeighbours().get(i), this);
						takeoverReplyOperation.scheduleImmediately();
						this.takeover.put(
								takeoverReplyOperation.getOperationID(),
								takeoverReplyOperation);
					}
				}
				this.operationFinished(true);
				this.scheduleWithDelay(CanConfig.waitTimeBetweenPing
						* CanConfig.numberPings);
			}
		}

	}

	public Object getResult() {
		return null;
	}

	/**
	 * update the CanNode, to be sure that every neighbour is still valid.
	 * 
	 * @param component
	 *            CanNode
	 */
	public void updateNode(CanNode component) {
		node = component;
		for (int i = 0; i < takeover.size(); i++) {
			Object[] cmdID2 = takeover.keySet().toArray();
			TakeoverReplyOperation takeoverReply = (TakeoverReplyOperation) takeover
					.get(cmdID2[i]);
			if (takeoverReply != null)
				takeoverReply.updateNode(node);
		}
	}

	public void stopOperation() {
		stopOperation = true;
		takeoverReplyFinishAll();
	}

	/**
	 * resumes the operation after CanConfig.waitTimeBetweenPing
	 */
	public void resumeOperation() {
		stopOperation = false;
		this.scheduleWithDelay(CanConfig.waitTimeBetweenPing);
	}

	/**
	 * resumes directly
	 */
	public void resumeDirectOperation() {
		stopOperation = false;
		this.scheduleImmediately();
	}

	/**
	 * A neighbour has answerd with a pongMsg. This finishes the
	 * takoverReplyOperation for this neighbour
	 * 
	 * @param pong
	 *            PongMsg
	 */
	public void found(PongMsg pong) {

		TakeoverReplyOperation takeoverReply = (TakeoverReplyOperation) takeover
				.get(pong.getOperationID());
		if (takeoverReply != null)
			takeoverReply.found(pong);
	}

	/**
	 * the finished TakeoverReplyOperation is removed
	 * 
	 * @param cmdID
	 *            operationID of the operation
	 */
	public void takeoverReplyFinished(Integer cmdID) {
		TakeoverReplyOperation takeoverReply = (TakeoverReplyOperation) takeover
				.get(cmdID);
		if (takeoverReply != null)
			takeoverReply.deleteOperation();
		takeover.remove(cmdID);
		noAnswer = 0;

		for (int i = 0; i < takeover.size(); i++) {
			Object[] cmdID2 = takeover.keySet().toArray();
			TakeoverReplyOperation takeoverReplySetAnswer = (TakeoverReplyOperation) takeover
					.get(cmdID2[i]);
			if (takeoverReplySetAnswer != null)
				takeoverReplySetAnswer.anotherAnswer();
		}
	}

	/**
	 * This method finishes all TakeoverRaplyOperations which are in progress
	 */
	public void takeoverReplyFinishAll() {
		for (int i = 0; i < takeover.size(); i++) {
			Object[] cmdID = takeover.keySet().toArray();
			TakeoverReplyOperation takeoverReply = (TakeoverReplyOperation) takeover
					.get(cmdID[i]);

			if (takeoverReply.getSucc() == false)
				takeoverReply.deleteOperation();

		}

		takeover = new HashMap<Integer, AbstractOperation>();
	}

	/**
	 * If non of the neighbours answer the peer asumes it is kicked and rejoins.
	 * (Not sure if it works.
	 */
	public void noAnswer() {
		noAnswer++;
		if (noAnswer >= node.getNeighbours().size() - 1) {
			stopOperation();
			node.join(callback);
			node.setStoredHashs(new LinkedList<Object[]>());
		}

	}

}
