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


package de.tud.kom.p2psim.impl.overlay.dht.pastry.operations;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.MsgTransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryConstants;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryMessageHandler;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.messages.LookupMsg;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * @author Fabio Zöllner, improved by Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupOperation extends
		AbstractPastryOperation<List<PastryContact>> {

	private static Logger log = SimLogger.getLogger(LookupOperation.class);

	private PastryNode node;

	private PastryID targetId;

	private Integer lookupId;

	private Integer redoCounter = 0;

	private PastryContact responsibleContact = null;

	private PastryMessageHandler msgHandler;

	// TODO check, if this can be improved
	public LookupOperation(PastryNode component, PastryID target,
			OperationCallback<List<PastryContact>> callback) {

		super(component, callback);
		this.targetId = target;
		lookupId = this.getOperationID();
		this.node = component;
		msgHandler = node.getMsgHandler();
	}

	public LookupOperation(PastryNode component, PastryID target,
			OperationCallback<List<PastryContact>> callback, int lookupId) {

		this(component, target, callback);
		this.lookupId = lookupId;
	}

	@Override
	protected void execute() {
		if (!node.isPresent()) {
			operationFinished(false);
			return;
		}

		if (node.isResponsibleFor(targetId.getCorrespondingKey())) {
			this.responsibleContact = node.getOverlayContact();
			operationFinished(true);
		} else {

			// Schedule a timeout to redo the lookup after some time
			Simulator.scheduleEvent("retryLookup" + lookupId,
					Simulator.getCurrentTime() + PastryConstants.OP_TIMEOUT,
					this, SimulationEvent.Type.OPERATION_EXECUTE);

			// Do the lookup
			node.registerOperation(this);
			PastryContact receiver = node.getNextHop(targetId);
			LookupMsg msg = new LookupMsg(node.getOverlayContact(), receiver,
					targetId, lookupId, 0);
			msgHandler.sendMsg(new MsgTransInfo<PastryContact>(msg, receiver));
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getData().equals("retryLookup" + lookupId)) {
			if (!isFinished()) {
				if (redoCounter < PastryConstants.OP_MAX_RETRIES) {
					log.info("lookup redo id = " + this.getOperationID()
							+ " times = " + redoCounter);
					redoCounter++;
					this.execute();
				} else {
					log.debug("look up aborted id = " + lookupId
							+ " redotime = " + redoCounter);

					node.unregisterOperation(lookupId);
					operationFinished(false);
				}
			}
		} else {
			super.eventOccurred(se);
		}
	}

	public void deliverResult(PastryContact responsibleContact,
			PastryID target, int lookupId, int hops) {
		this.responsibleContact = responsibleContact;

		if (!isFinished()) {
			node.unregisterOperation(this.lookupId);
			operationFinished(true);
		}
	}

	public int getLookupId() {
		return this.lookupId;
	}

	public PastryID getTarget() {
		return this.targetId;
	}

	@Override
	public List<PastryContact> getResult() {
		LinkedList<PastryContact> l = new LinkedList<PastryContact>();
		if (responsibleContact != null)
			l.add(responsibleContact);
		return l;
	}
}
