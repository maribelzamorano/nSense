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

import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.MsgTransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryConstants;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryID;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.messages.ConfirmStoreMsg;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.messages.StoreMsg;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements the store operation of Pastry. The operation is
 * triggered from the {@link PastryNode} and stores the data item at the
 * responsible node(s), while returning the ID(s) through the provided
 * {@link OperationCallback}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 02/04/2011
 * 
 */
public class StoreOperation extends AbstractPastryOperation<Set<PastryContact>> {

	private static Logger log = SimLogger.getLogger(StoreOperation.class);

	private PastryID key;

	private DHTObject value;

	private Set<PastryContact> responsibleNodes;

	private int redoCounter = 0;

	public StoreOperation(PastryNode component, PastryID key, DHTObject value,
			OperationCallback<Set<PastryContact>> callback) {
		super(component, callback);
		this.key = key;
		this.value = value;
	}

	@Override
	protected void execute() {
		if (!getComponent().isPresent()) {
			operationFinished(false);
			return;
		}

		// Schedule a timeout to redo the lookup after some time
		scheduleOperationTimeout(PastryConstants.OP_TIMEOUT);

		// Do the lookup
		getComponent().registerOperation(this);
		PastryContact receiver = getComponent().getNextHop(key);
		StoreMsg msg = new StoreMsg(getComponent().getOverlayContact(),
				receiver, key, value, getOperationID());
		getComponent().getMsgHandler().sendMsg(
				new MsgTransInfo<PastryContact>(msg, receiver));

	}

	public void deliverResult(ConfirmStoreMsg csMsg) {
		if (!isFinished()) {
			responsibleNodes = csMsg.getStoringPeers();
			log.info(Simulator.getSimulatedRealtime() + ": IPAddress= "
					+ getComponent().getHost().getNetLayer().getNetID()
					+ ": Received map of storing peers for key " + key);
			operationFinished(true);
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (!isFinished()
				&& se.getType() == SimulationEvent.Type.TIMEOUT_EXPIRED) {
			if (redoCounter < PastryConstants.OP_MAX_RETRIES) {
				log.debug(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": Retry of StoreOperation with ID "
						+ getOperationID() + " for the " + (redoCounter + 1)
						+ ". time");
				if (getComponent().unregisterOperation(getOperationID()) == null) {
					log.error(Simulator.getSimulatedRealtime()
							+ ": IPAddress= "
							+ getComponent().getHost().getNetLayer().getNetID()
							+ ": Trying to unregister operation"
							+ getOperationID() + ", which was not stored");
				}
				redoCounter++;
				execute();
			} else {
				log.warn(Simulator.getSimulatedRealtime() + ": IPAddress= "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ ": StoreOperation with ID " + getOperationID()
						+ " failed!");
				operationTimeoutOccured();
			}
		} else if (se.getType() == SimulationEvent.Type.OPERATION_EXECUTE
				&& se.getData() == this) {
			Simulator.getMonitor().operationInitiated(this);
			execute();
		}
	}

	@Override
	public Set<PastryContact> getResult() {
		return responsibleNodes;
	}

}
