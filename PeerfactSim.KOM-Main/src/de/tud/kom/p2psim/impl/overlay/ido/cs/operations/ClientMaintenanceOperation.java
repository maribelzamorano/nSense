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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The maintenance operation for a client. If the Server not send a message in a
 * specific interval, then join the node new.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ClientMaintenanceOperation extends
		AbstractOperation<ClientNode, Object> {
	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger
			.getLogger(ClientMaintenanceOperation.class);

	private ClientNode node;

	public ClientMaintenanceOperation(ClientNode node,
			OperationCallback<Object> operationCallback) {
		super(node, operationCallback);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (Simulator.getCurrentTime() - node.getLastUpdate() > CSConfiguration.TIME_OUT_SERVER) {
			node.join();
			// to stop the reply of this operation
			if (log.isInfoEnabled())
				log.info(node.getOverlayID()
						+ " do a rejoin, because Server doesn't send updates in a specific interval");
			this.operationFinished(false);
		}
		this.operationFinished(true);
	}

	@Override
	public Object getResult() {
		// nothing
		return null;
	}

	public void stop() {
		this.operationFinished(false);
	}

}
