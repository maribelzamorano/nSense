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


package de.tud.kom.p2psim.impl.overlay.dht.pastry.operations;

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.MsgTransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryConstants;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryMessageHandler;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.TransmissionCallback.Failed;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.messages.JoinMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is the operation used to join the pastry overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinOperation extends AbstractPastryOperation<Object> implements
		Failed {

	final static Logger log = SimLogger.getLogger(JoinOperation.class);

	private final PastryNode node = getComponent();

	private final PastryMessageHandler msgHandler = node.getMsgHandler();

	private int numberOfTriedBootstrapNodes = 0;

	private LinkedHashSet<PastryContact> triedNodes = new LinkedHashSet<PastryContact>();

	/**
	 * @param component
	 *            the component owning this operation
	 * @param callback
	 *            the callback of the operation
	 */
	public JoinOperation(PastryNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		// TODO To be realized
	}

	@Override
	public void execute() {
		if (!node.getHost().getNetLayer().isOnline())
			return;

		scheduleOperationTimeout(PastryConstants.OP_JOIN_TIMEOUT);

		if (!PastryBootstrapManager.getInstance().anyNodeAvailable()) {
			/*
			 * Node is the first in the overlay
			 */

			node.setPeerStatus(PeerStatus.PRESENT);
			PastryBootstrapManager.getInstance().registerNode(node);
			operationFinished(true);
		} else {
			/*
			 * There are already other nodes in the overlay
			 */
			node.getMsgHandler().setUnfinishedJoinOp(this);

			tryNextAvailableBoostrap();
		}
	}

	@Override
	public Object getResult() {
		// There is no valuable result to be returned
		return null;
	}

	/**
	 * Tells the operation that the the join operation finished successfully.
	 * 
	 * This method is meant to be called by the message handler when the final
	 * reply to the join was received and all peers in the state tables have
	 * been notified of the peers existence.
	 */
	public void joinOperationFinished() {
		node.setPeerStatus(PeerStatus.PRESENT);
		operationFinished(true);
	}

	private void tryNextAvailableBoostrap() {

		if (numberOfTriedBootstrapNodes > PastryConstants.OP_JOIN_MAX_RETRIES) {
			node.setPeerStatus(PeerStatus.ABSENT);
			operationFinished(false);
			return;
		}

		List<PastryContact> availableBootstraps = PastryBootstrapManager
				.getInstance().getBoostrapNodesSortedByProximityDistance(
						node.getOverlayContact());

		availableBootstraps.removeAll(triedNodes);

		if (!availableBootstraps.isEmpty()) {
			PastryContact toTryBoostrap = availableBootstraps.get(0);
			triedNodes.add(toTryBoostrap);
			numberOfTriedBootstrapNodes++;

			JoinMsg msg = new JoinMsg(node.getOverlayContact());
			msgHandler.sendMsg(new MsgTransInfo<PastryContact>(msg,
					toTryBoostrap), this);
		} else {
			node.setPeerStatus(PeerStatus.ABSENT);
			operationFinished(false);
		}
	}

	@Override
	public void transmissionFailed(Message msg) {
		tryNextAvailableBoostrap();
	}
}
