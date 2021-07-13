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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.operations;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This operation is called to find the current responder for a specified finger
 * points
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateFingerPointOperation extends
		AbstractChordOperation<ChordContact> {

	final static Logger log = SimLogger
			.getLogger(UpdateFingerPointOperation.class);

	protected ChordNode masterNode;

	protected int updateIndex;

	protected ChordRoutingTable routingTable;

	protected ChordContact responder;

	protected ChordID target;

	protected long beginTime;

	public UpdateFingerPointOperation(ChordNode component) {
		super(component);
		masterNode = component;
	}

	public UpdateFingerPointOperation(ChordNode component,
			OperationCallback<ChordContact> callback) {
		super(component, callback);
		masterNode = component;
	}

	@Override
	protected void execute() {
		beginTime = Simulator.getCurrentTime();

		if (masterNode.isPresent()) {
			scheduleOperationTimeout(ChordConfiguration.OPERATION_TIMEOUT);

			routingTable = masterNode.getChordRoutingTable();
			updateIndex = routingTable.getNextUpdateFingerPoint();
			target = routingTable.getPointAddress(updateIndex);
			log.debug(masterNode.getTransInfo().getNetId()
					+ " executes updateFingerPointOp = " + updateIndex
					+ " target = " + target);
			OperationCallback<List<ChordContact>> callback = new UpdateFingerPointCallback();
			log.debug("update finger node = " + masterNode + " update index "
					+ updateIndex + " point " + target);

			// new OperationTimer(callback, ChordConfiguration.LOOKUP_TIMEOUT);

			masterNode.overlayNodeLookup(target, callback);
		} else {
			operationFinished(false);
		}
	}

	protected class UpdateFingerPointCallback implements
			OperationCallback<List<ChordContact>> {

		@Override
		public void calledOperationFailed(Operation<List<ChordContact>> op) {
			if (responder == null) {
				log.warn(masterNode.getTransInfo().getNetId()
						+ " cannot update finger node index = " + updateIndex
						+ " target = " + target);

			}
			// CHECK if this is the right place instead of being in the if-block
			operationFinished(false);
		}

		@Override
		public void calledOperationSucceeded(Operation<List<ChordContact>> op) {
			routingTable.incNextUpdateFingerPoint();
			List<ChordContact> responders = op.getResult();
			if (!responders.isEmpty()) {
				responder = responders.get(0);
				routingTable.setFingerPoint(updateIndex, responder);
				operationFinished(true);
			}
		}

	}

	@Override
	public ChordContact getResult() {
		return responder;
	}

	public long getBeginTime() {
		return beginTime;
	}

}
