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

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.PingMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.PongMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.TakeoverMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.TakeoverReorganizeMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation sends a PingMsg to a neighbour and awaits a response. If no
 * response arrives it is tried CanConfig.numberPings times. If their is still
 * no response, it tries to finds which peer has the most common parents with
 * the missing node. This peer gets a takeover message which starts the
 * TakeoverRabuild Operation. If the actual node is the has the most common vid
 * parents, it starts the TakeoverRebuildOperation itself. So it send a
 * takeoverReorganizeMsg to every node which has the same number of vid parents.
 * If missing node and actual node are leafs of the tree, the actual node just
 * takes the area and adds it to its own.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverReplyOperation extends AbstractOperation<CanNode, Object> {

	private final static Logger log = SimLogger.getLogger(CanNode.class);

	private CanNode node;

	private CanOverlayContact neighbour;

	private boolean succ, delete;

	private int allreadyTried;

	private TakeoverOperation takeoverOperation;

	private boolean otherAnswer;

	/**
	 * starts a TakeoverReplyOperation in this node for one of its neighbours
	 * 
	 * @param component
	 *            CanNode which should check its neighbours
	 * @param neighbour
	 *            neighbour to check
	 * @param takeoverOperation
	 *            this operation called the TekeoverReply Operation
	 */
	public TakeoverReplyOperation(CanNode component,
			CanOverlayContact neighbour, TakeoverOperation takeoverOperation) {
		super(component);

		this.node = getComponent();
		this.neighbour = neighbour;
		this.takeoverOperation = takeoverOperation;
		this.allreadyTried = 0;
		this.succ = false;
		this.delete = false;
		otherAnswer = false;
	}

	@Override
	public void execute() {
		if (node.getPeerStatus() == PeerStatus.PRESENT && delete == false
				&& node.neighboursContain(neighbour)) {

			if (allreadyTried < CanConfig.numberPings && succ == false) {
				PingMsg ping = new PingMsg(node.getLocalContact()
						.getOverlayID(), neighbour.getOverlayID(), node
						.getLocalContact().clone());
				ping.setOperationId(this.getOperationID());
				node.getTransLayer().send(ping, neighbour.getTransInfo(),
						node.getPort(), TransProtocol.UDP);
				allreadyTried++;
				this.operationFinished(true);
				this.scheduleWithDelay(CanConfig.timeout);
			} else if (allreadyTried >= CanConfig.numberPings && succ == false
					&& otherAnswer == false) {
				this.operationFinished(true);
				this.scheduleWithDelay(CanConfig.numberPings
						* CanConfig.timeout + 2 * CanConfig.timeout);
			} else if (allreadyTried >= CanConfig.numberPings && succ == false) {
				log.warn(Simulator.getSimulatedRealtime()
						+ " node left, own id: "
						+ node.getLocalContact().getOverlayID().toString()
						+ " " + node.getLocalContact().getArea().toString()
						+ " missing node: "
						+ neighbour.getOverlayID().toString() + " "
						+ neighbour.getArea().toString() + " "
						+ this.getOperationID());
				for (CanOverlayContact neighbour : node.getNeighbours()) {
					log.warn("own neighbours: "
							+ neighbour.getOverlayID().toString());
				}

				CanOverlayContact closestVidNeighbour = null;
				if (neighbour
						.getArea()
						.getVid()
						.getVIDList()
						.get(neighbour.getArea().getVid().getVIDList().size() - 1)
						.equals("0"))
					closestVidNeighbour = node
							.getVidNeighboursOfCertainNeighbour(neighbour)[1];
				else
					closestVidNeighbour = node
							.getVidNeighboursOfCertainNeighbour(neighbour)[0];

				if (closestVidNeighbour != null) { // ////////////normaly used
													// without the case, not
													// well tested
					log.debug("closest vid neighbour: "
							+ closestVidNeighbour.getOverlayID().toString()
							+ " in node "
							+ node.getLocalContact().getOverlayID().toString()
							+ " "
							+ neighbour.getArea().getVid().toString()
							+ " "
							+ node.getLocalContact().getArea().getVid()
									.toString()
							+ " "
							+ neighbour
									.getArea()
									.getVid()
									.closestNeighbour(
											node.getLocalContact().getArea()
													.getVid()));

					// controll if one of the neighbours is the direct neighbour
					if (neighbour
							.getArea()
							.getVid()
							.closestNeighbour(
									node.getLocalContact().getArea().getVid())) {
						log.debug("direct Vid neighbour is missing "
								+ " neighbour "
								+ neighbour.getArea().getVid().toString()
								+ " own "
								+ node.getLocalContact().getArea().getVid()
										.toString()
								+ " "
								+ neighbour
										.getArea()
										.getVid()
										.closestNeighbour(
												node.getLocalContact()
														.getArea().getVid()));
						if (node.getMissingNode() == null) {
							node.setMissingNode(neighbour);
							TakeoverRebuildOperation takeoverRebuildOperation = new TakeoverRebuildOperation(
									node);
							takeoverRebuildOperation
									.scheduleWithDelay(CanConfig.waitForTakeover);
							this.operationFinished(true);
						}
					}
					// own node is closest, but not brother
					else if (closestVidNeighbour
							.getOverlayID()
							.toString()
							.equals(node.getLocalContact().getOverlayID()
									.toString())
							&& (node.getVIDNeighbours()[0]
									.getOverlayID()
									.toString()
									.equals(neighbour.getOverlayID().toString()) || node
									.getVIDNeighbours()[1]
									.getOverlayID()
									.toString()
									.equals(neighbour.getOverlayID().toString()))) {

						if (node.getMissingNode() == null) {
							node.setMissingNode(neighbour);

							CanOverlayContact n = null;
							if (node.getVIDNeighbours()[0].getArea().getVid()
									.equals(neighbour.getArea().getVid()))
								n = node.getVIDNeighbours()[1];
							else
								n = node.getVIDNeighbours()[0];
							TakeoverReorganizeMsg leave = new TakeoverReorganizeMsg(
									(CanOverlayID) node.getOverlayID(),
									n.getOverlayID(), node.getLocalContact()
											.clone(), neighbour);
							node.getTransLayer().send(leave, n.getTransInfo(),
									node.getPort(), TransProtocol.TCP);

							TakeoverRebuildOperation takeoverRebuildOperation = new TakeoverRebuildOperation(
									node);
							takeoverRebuildOperation
									.scheduleWithDelay(CanConfig.waitForTakeover);
							this.operationFinished(true);

						} else
							log.debug("node is allready set");
					} else { // send takeoverMsg
						CanOverlayContact receiver = null;
						if (closestVidNeighbour.getArea().getVid()
								.numberCommon(neighbour.getArea().getVid()) >= node
								.getLocalContact().getArea().getVid()
								.numberCommon(neighbour.getArea().getVid()))
							receiver = closestVidNeighbour;
						else if (neighbour
								.getArea()
								.getVid()
								.getVIDList()
								.get(neighbour.getArea().getVid().getVIDList()
										.size() - 1).toString().equals("0"))
							receiver = node.getVIDNeighbours()[0];
						else
							receiver = node.getVIDNeighbours()[1];

						log.debug("send takeovermsg from "
								+ node.getLocalContact().getOverlayID()
										.toString() + " to "
								+ receiver.getOverlayID().toString());
						List<CanOverlayContact> neighboursOfMissing = node
								.getNeighboursOfCertainNeighbour(neighbour);
						CanOverlayContact[] vidNeighboursOfMissing = node
								.getVidNeighboursOfCertainNeighbour(neighbour);
						TakeoverMsg takeoverMsg = new TakeoverMsg(node
								.getLocalContact().getOverlayID(),
								receiver.getOverlayID(), neighbour,
								neighboursOfMissing, vidNeighboursOfMissing);
						node.getTransLayer().send(takeoverMsg,
								receiver.getTransInfo(), node.getPort(),
								TransProtocol.TCP);

						log.debug("missing node detected and just removed, in node: "
								+ node.getLocalContact().getOverlayID()
										.toString()
								+ " missing node: "
								+ neighbour.getOverlayID().toString());
						node.removeNeighbour(neighbour);
						this.takeoverOperation.takeoverReplyFinished(this
								.getOperationID());

					}
				} else {
					log.debug("missing node detected and just removed, in node: "
							+ node.getLocalContact().getOverlayID().toString()
							+ " missing node: "
							+ neighbour.getOverlayID().toString());
					node.removeNeighbour(neighbour);
					this.takeoverOperation.takeoverReplyFinished(this
							.getOperationID());
				}
			} else {
				this.takeoverOperation.takeoverReplyFinished(this
						.getOperationID());
				this.operationFinished(true);

			}
		}
	}

	public Object getResult() {
		return null;
	}

	public void found(PongMsg msg) {
		succ = true;
	}

	public void deleteOperation() {
		delete = true;
	}

	public boolean getSucc() {
		return succ;
	}

	public void anotherAnswer() {
		otherAnswer = true;
	}

	public void updateNode(CanNode node) {
		this.node = node;
	}
}
