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

package de.tud.kom.p2psim.impl.overlay.ido.cs.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Sends a heartbeat message, if for a configurable time isn't send a message to
 * the server.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ClientHeartbeatOperation extends
		AbstractOperation<ClientNode, Object> {

	/**
	 * The client node, which started this operation.
	 */
	private ClientNode node;

	public ClientHeartbeatOperation(ClientNode node,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (Simulator.getCurrentTime() - node.getLastHeartbeat() >= CSConfiguration.INTERVAL_BETWEEN_HEARTBEATS) {
			node.disseminatePosition();
			node.setLastHeartbeat(Simulator.getCurrentTime());
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// Nothing to get back
		return null;
	}

	/**
	 * Stops the operation.
	 */
	public void stop() {
		this.operationFinished(false);
	}

}
