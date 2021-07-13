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
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanArea;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanVID;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * Operation is used to build up a new CAN. If the first node joins the CAN it
 * creates a CAN.
 * 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CreateCanOperation extends AbstractOperation<CanNode, Object> {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	/**
	 * creates a CAN with the given peer
	 * 
	 * @param node
	 *            first peer in CAN
	 * @param callback
	 *            operation listener
	 */
	public CreateCanOperation(CanNode node, OperationCallback<Object> callback) {
		super(node, callback);
	}

	protected void execute() {
		CanNode master = getComponent();
		master.setAlive(true);
		master.setPeerStatus(PeerStatus.PRESENT);

		master.setNeighbours(null);
		CanArea area = new CanArea(0, CanConfig.CanSize, 0, CanConfig.CanSize);
		master.setArea(area);
		master.getLocalContact().getArea().setVid(new CanVID("0"));
		CanOverlayContact[] vidNeighbours = { master.getLocalContact().clone(),
				master.getLocalContact().clone() };
		master.setVIDNeigbours(vidNeighbours);
		master.setPeerStatus(PeerStatus.PRESENT);
		master.startTakeoverOperation();

		master.getBootstrap().registerNode(master);
		operationFinished(true);

		log.debug(Simulator.getSimulatedRealtime() + "Created Can");
		this.isSuccessful();
	}

	@Override
	public Object getResult() {
		return this;
	}

}
