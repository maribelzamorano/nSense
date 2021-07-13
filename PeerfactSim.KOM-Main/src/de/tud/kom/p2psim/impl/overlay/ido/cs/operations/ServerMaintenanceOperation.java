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
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientID;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ServerNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ServerStorage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * The maintenance operation of the server. If a client has not send a message
 * in a specific interval, then will be removed this client from the storage.
 * Additionally it will be assumed that the client is offline.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerMaintenanceOperation extends
		AbstractOperation<ServerNode, Object> {

	private ServerNode node;

	public ServerMaintenanceOperation(ServerNode node,
			OperationCallback<Object> operationCallback) {
		super(node, operationCallback);
		this.node = node;
	}

	/**
	 * Iterate over all entries in the storage and delete expired clients.
	 */
	@Override
	protected void execute() {
		ServerStorage storage = node.getStorage();

		for (ClientID id : storage.getAllStoredClientIDs()) {
			long lastUpdate = storage.getLastUpdate(id);
			if (Simulator.getCurrentTime() - lastUpdate >= CSConfiguration.TIME_OUT_CLIENT) {
				storage.removeClient(id);
				node.stopDissemination(id);
			}
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// nothing
		return null;
	}

}
