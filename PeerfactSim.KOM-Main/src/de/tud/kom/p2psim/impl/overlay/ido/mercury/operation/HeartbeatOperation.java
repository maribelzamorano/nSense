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

package de.tud.kom.p2psim.impl.overlay.ido.mercury.operation;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.MercuryIDOConfiguration;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.MercuryIDONode;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This Operation execute a heartbeat. It disseminate the actually position to
 * the overlay, if in a defined time interval is no dissemination of the
 * position occurred.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class HeartbeatOperation extends
		AbstractOperation<MercuryIDONode, Object> {
	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public HeartbeatOperation(MercuryIDONode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.node = component;
	}

	@Override
	protected void execute() {
		// FIXED >= leads to duplicate publications if Interval = n *
		// moveInterval
		if (Simulator.getCurrentTime() - node.getLastHeartbeat() > MercuryIDOConfiguration.INTERVAL_BETWEEN_HEARTBEATS) {
			node.disseminatePosition(node.getPosition());
			node.setLastHeartbeat(Simulator.getCurrentTime());
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stop this operation.
	 */
	public void stop() {
		operationFinished(false);
	}

}
