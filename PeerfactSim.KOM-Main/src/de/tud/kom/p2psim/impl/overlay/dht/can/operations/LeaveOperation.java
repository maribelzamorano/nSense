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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LeaveMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This Operation is called, if a peer wants to leave the overlay. Therefore
 * this operation sends a LeaveMsg to the VID neighbour which has the most
 * common numbers, that means it has a common parent with the leaving peer. If
 * the next parent has just this both children the leave message stops there. If
 * the parent has more children a LeaveReorganizeMsg is created and sent to ever
 * children. The will all response with a LeaveReorganizeReplyMsg which is sent
 * to the leaving peer. The leaving peer collects the data and after a certain
 * time it calls the LeaveReorgnizeOperation which takes all these data a
 * generates the new structure (area, VID, neighbours, VID neighbours).
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LeaveOperation extends AbstractOperation<CanNode, Object> {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	CanNode master = getComponent();

	/**
	 * peer wants to leave
	 * 
	 * @param component
	 *            leaving node
	 * @param callback
	 *            operation listener
	 */
	public LeaveOperation(CanNode component, OperationCallback<Object> callback) {
		super(component, callback);
	}

	protected void execute() {
		log.debug("node wants to leave: "
				+ master.getLocalContact().getOverlayID().toString() + " "
				+ master.getLocalContact().getArea().toString());
		CanOverlayContact n;

		if (master.getPeerStatus().equals(PeerStatus.ABSENT)) {
			System.err.println("peer is allready offline");
			operationFinished(false);
			return;
		}
		log.debug("vid nl: "
				+ master.getVIDNeighbours()[0].getArea().getVid()
				+ " own vid: "
				+ master.getLocalContact().getArea().getVid()
				+ " vid nr: "
				+ master.getVIDNeighbours()[1].getArea().getVid()
				+ " "
				+ master.getVIDNeighbours()[0]
						.getArea()
						.getVid()
						.numberCommon(
								master.getLocalContact().getArea().getVid())
				+ " "
				+ master.getVIDNeighbours()[1]
						.getArea()
						.getVid()
						.numberCommon(
								master.getLocalContact().getArea().getVid()));
		if (master.getVIDNeighbours()[0].getArea().getVid()
				.numberCommon(master.getLocalContact().getArea().getVid()) > master
				.getVIDNeighbours()[1].getArea().getVid()
				.numberCommon(master.getLocalContact().getArea().getVid()))
			n = master.getVIDNeighbours()[0];
		else
			n = master.getVIDNeighbours()[1];
		log.debug(n.getOverlayID().toString() + " "
				+ n.getArea().getVid().toString());
		master.getLocalContact().setAlive(false);
		LeaveMsg leave = new LeaveMsg(
				(CanOverlayID) this.master.getOverlayID(), n.getOverlayID(),
				master.getLocalContact().clone(), master.getNeighbours(),
				master.getLocalContact().getArea(), master.getVIDNeighbours());
		this.master.getTransLayer().send(leave, n.getTransInfo(),
				master.getPort(), TransProtocol.TCP);

		// after a certain time is the leaveReorganize operation called which
		// handles the
		// most of the leave.

		LeaveReorganizeOperation leaveReorganize = new LeaveReorganizeOperation(
				master);
		leaveReorganize.scheduleWithDelay(CanConfig.waitTimeAfterLeave);

	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
