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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LeaveMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.AbstractChordOperation;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class represents a leave event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeaveOperation extends AbstractChordOperation<Object> {

	final static Logger log = SimLogger.getLogger(LeaveOperation.class);

	private final ChordNode leaveNode;

	public LeaveOperation(ChordNode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		leaveNode = getComponent();
	}

	@Override
	protected void execute() {

		if (leaveNode.isPresent()) {

			log.debug("node leave node = " + leaveNode);
			ChordRoutingTable routingTable = leaveNode.getChordRoutingTable();

			// inform successor
			ChordContact successor = routingTable.getSuccessor();
			if (successor != null) {
				LeaveMessage leaveMessage = new LeaveMessage(
						leaveNode.getLocalChordContact(), successor);
				leaveNode.getTransLayer().send(leaveMessage,
						successor.getTransInfo(), leaveNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL);

			}

			// inform predecessor
			ChordContact predecessor = routingTable.getPredecessor();
			if (predecessor != null) {
				LeaveMessage leaveMessage = new LeaveMessage(
						leaveNode.getLocalChordContact(), predecessor);
				leaveNode.getTransLayer().send(leaveMessage,
						predecessor.getTransInfo(), leaveNode.getPort(),
						ChordConfiguration.TRANSPORT_PROTOCOL);

			}
		} else
			operationFinished(false);

		leaveNode.leaveOperationFinished();
	}

	@Override
	public Object getResult() {

		return this.isSuccessful();
	}

}
