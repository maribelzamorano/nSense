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

package de.tud.kom.p2psim.impl.overlay.ido.psense.operations;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseID;
import de.tud.kom.p2psim.impl.overlay.ido.psense.PSenseNode;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.PositionUpdateMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.messages.SensorRequestMsg;
import de.tud.kom.p2psim.impl.overlay.ido.psense.util.Configuration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is a operation for the join. The operation try to join to the
 * overlay. Additionally sets the status of the node during the join.
 * 
 * <p>
 * For the join, the operation used the bootstrap instance. It fetches for every
 * sensor node a node in the overlay over the bootstrap instance. Then take
 * every X ms a lookup for a receiving of a response. If a response is arrived,
 * then is the joinig operations finished.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class JoinOperation extends AbstractOperation<PSenseNode, Boolean> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(PSenseNode.class);

	private PSenseNode node;

	private PSenseBootstrapManager bootstrap;

	private boolean joinSuccessful;

	public JoinOperation(PSenseNode component,
			OperationCallback<Boolean> callback) {
		super(component, callback);
		node = component;
		bootstrap = node.getBootstrapManager();
		joinSuccessful = false;
	}

	@Override
	protected void execute() {

		if (node.getPeerStatus() == PeerStatus.TO_JOIN) {

			if (node.isAMessageArrived()) {
				if (log.isDebugEnabled())
					log.debug("A message is arrived. The node "
							+ node.getOverlayID()
							+ " is present in the overlay!");
				node.setPeerStatus(PeerStatus.PRESENT);
				bootstrap.registerNode(node);
				joinSuccessful = true;
			} else {
				if (Simulator.getCurrentTime() - node.getLastJoinAttempt() > Configuration.WAIT_BEFORE_RETRY_JOIN) {
					// do a rejoin
					node.setLastJoinAttempt(Simulator.getCurrentTime());
					boolean operationSuccessful = join();

					if (operationSuccessful) {
						node.setPeerStatus(PeerStatus.PRESENT);
						joinSuccessful = true;
					} else {
						joinSuccessful = false;
					}
				} else {
					// Do nothing, because waiting
					joinSuccessful = false;
				}
			}

		} else if (node.getPeerStatus() == PeerStatus.ABSENT) {

			node.setLastJoinAttempt(Simulator.getCurrentTime());
			boolean operationFinished = join();

			if (operationFinished) {
				node.setPeerStatus(PeerStatus.PRESENT);
				joinSuccessful = true;
			} else {
				node.setPeerStatus(PeerStatus.TO_JOIN);
				joinSuccessful = false;
			}

		} else {
			log.error("Wrong peer status or rather this operation has no handling for this peer status.");
		}
		operationFinished(true);
	}

	private boolean join() {
		if (!bootstrap.anyNodeAvailable()) {
			if (log.isInfoEnabled())
				log.info("Alone in the Overlay!");
			bootstrap.registerNode(node);
			return true;
		} else {

			List<TransInfo> toRequest;
			toRequest = bootstrap
					.getBootstrapInfo(Configuration.NUMBER_SECTORS);

			node.incSeqNr();

			for (int i = 0; i < toRequest.size()
					&& i < Configuration.NUMBER_SECTORS; i++) {
				SensorRequestMsg reqMsg = new SensorRequestMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), Configuration.VISION_RANGE_RADIUS,
						node.getPosition(), (byte) i);
				PositionUpdateMsg posMsg = new PositionUpdateMsg(
						node.getOverlayID(), Configuration.MAXIMAL_HOP,
						node.getSeqNr(), new Vector<PSenseID>(),
						Configuration.VISION_RANGE_RADIUS, node.getPosition());
				// Data amount to few for sorting out of needless
				// updateMessages
				// or that the bandwidth not reach
				node.getTransLayer().send(reqMsg, toRequest.get(i),
						node.getPort(), Configuration.TRANSPORT_PROTOCOL);
				node.getTransLayer().send(posMsg, toRequest.get(i),
						node.getPort(), Configuration.TRANSPORT_PROTOCOL);
			}
			return false;
		}
	}

	@Override
	public Boolean getResult() {
		return joinSuccessful;
	}

	public void churnDuringJoin() {
		log.error(node.getOverlayID()
				+ " could not complete join due to churn.");
		operationFinished(false);
	}
}
