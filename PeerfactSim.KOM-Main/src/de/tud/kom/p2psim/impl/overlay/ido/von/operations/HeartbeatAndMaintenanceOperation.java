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

package de.tud.kom.p2psim.impl.overlay.ido.von.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonConfiguration;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonID;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNode;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNodeInfo;
import de.tud.kom.p2psim.impl.overlay.ido.von.messages.MoveMsg;
import de.tud.kom.p2psim.impl.overlay.ido.von.voronoi.Voronoi;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This operation is primarily concerned with the mechanisms for doing heart
 * beats if necessary. Before it does so, it checks the local voronoi diagram
 * for stale neighbors and removes them if necessary.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HeartbeatAndMaintenanceOperation extends
		AbstractOperation<VonNode, Object> {

	public HeartbeatAndMaintenanceOperation(VonNode component,
			OperationCallback callback) {
		super(component, callback);
	}

	@Override
	protected void execute() {
		VonNode node = getComponent();

		if (node.getPeerStatus() != PeerStatus.PRESENT) {
			operationFinished(false);
			return;
		}

		Voronoi v = node.getLocalVoronoi();

		/*
		 * Remove stale contacts from voronoi
		 */
		long removeBefore = Simulator.getCurrentTime()
				- VonConfiguration.STALE_NEIGHBOR_INTERVAL;
		v.removeStaleContacts(removeBefore);

		/*
		 * Do heart beating if necessary
		 */
		if (node.getLastHeartbeatTime()
				+ VonConfiguration.INTERVAL_BETWEEN_HEARTBEATS <= Simulator
				.getCurrentTime()) {

			// Retrieve all neighbors
			VonNodeInfo[] neighbors = node.getLocalVoronoi().getVonNeighbors(
					node.getVonID(), node.getAOI());

			for (VonNodeInfo toInform : neighbors) {
				VonID toInformId = toInform.getContact().getOverlayID();

				MoveMsg mMsg;

				if (v.isBoundaryNeighborOf(node.getVonID(), toInformId,
						node.getAOI())) {
					mMsg = new MoveMsg(node.getVonID(), toInformId, true,
							node.getPosition(), node.getAOI(),
							Simulator.getCurrentTime());

				} else {
					mMsg = new MoveMsg(node.getVonID(), toInformId, false,
							node.getPosition(), node.getAOI(),
							Simulator.getCurrentTime());
				}

				node.getTransLayer().send(mMsg,
						toInform.getContact().getTransInfo(), node.getPort(),
						VonConfiguration.TRANSPORT_PROTOCOL);
			}

			node.setLastHeartbeatTime(Simulator.getCurrentTime());

			operationFinished(true);
		} else {
			operationFinished(true);
		}

	}

	@Override
	public Object getResult() {
		// There is no result
		return null;
	}

}
