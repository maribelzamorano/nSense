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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This operation is just used for debug. It shows all data of a peer.
 * 
 * @param master
 *            peer which should output its data
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class GiveNodeInfoOperation extends AbstractOperation<CanNode, Object>
		implements TransMessageCallback {

	private final static Logger log = SimLogger.getLogger(CanNode.class);

	CanNode master = getComponent();

	/**
	 * Shows all the information about the peer
	 * 
	 * @param node
	 * @param callback
	 */
	public GiveNodeInfoOperation(CanNode node,
			OperationCallback<Object> callback) {
		super(node, callback);
	}

	@Override
	public void execute() {
		if (master.getPeerStatus().equals(PeerStatus.PRESENT)) {
			master.getBootstrap().update(master);
			log.debug(Simulator.getSimulatedRealtime() + " Bootstrap: "
					+ master.getBootstrap().toString());
			log.debug("Own ID: "
					+ master.getLocalContact().getOverlayID().toString()
					+ " own VID "
					+ master.getLocalContact().getArea().getVid().toString()
					+ " own area "
					+ master.getLocalContact().getArea().toString()
					+ " is allive: " + master.getLocalContact().isAlive()
					+ " Neighbours: ");
			try {
				for (int i = 0; i < master.getNeighbours().size(); i++)
					log.debug(master.getNeighbours().get(i).getOverlayID()
							.toString()
							+ " "
							+ master.getNeighbours().get(i).getArea()
									.toString());
				log.debug("VID Neighbours "
						+ master.getVIDNeighbours()[0].getArea().getVid()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[0].getOverlayID()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[1].getArea().getVid()
								.toString()
						+ " "
						+ master.getVIDNeighbours()[1].getOverlayID()
								.toString());

			} catch (Exception e) {
				// just in case
			}
		}
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// TODO Auto-generated method stub

	}

}
