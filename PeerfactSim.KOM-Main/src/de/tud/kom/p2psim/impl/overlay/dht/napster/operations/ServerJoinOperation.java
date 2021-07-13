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

package de.tud.kom.p2psim.impl.overlay.dht.napster.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterServerNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ServerJoinOperation extends
		AbstractOperation<NapsterServerNode, Object> implements
		TransMessageCallback {

	private static final Logger log = SimLogger
			.getLogger(ServerJoinOperation.class);

	NapsterServerNode node;

	public ServerJoinOperation(NapsterServerNode node,
			OperationCallback callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	public void execute() {
		operationFinished(true);

		// This information is related to execution of the napster application
		log.warn("[" + this.getComponent().getHost() + "] Server connected @ "
				+ Simulator.getSimulatedRealtime());
	}

	@Override
	protected void operationTimeoutOccured() {
		operationFinished(false);
	}

	public void messageTimeoutOccured(int commId) {
		operationFinished(false);
	}

	public Object getResult() {
		return null;
	}

	public void receive(Message msg, TransInfo senderAddr, int commId) {
		log.warn("Received unexpected message: " + msg);
	}

}
