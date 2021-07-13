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


package de.tud.kom.p2psim.impl.overlay.dht.can.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.StoreMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.StoreReplyMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation tries to store a hash value in the CAN. It sends a store
 * message with its hash value and its contact to the area, which is responsible
 * for the hash value. The operation is saved in the CanNode and if the
 * storeReplyMsg arrives it is removed. The hash values are refreshed every
 * CanConfig.waitTimeToRefreshHash.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreOperation extends AbstractOperation<CanNode, Object>
		implements OperationCallback {

	private final static Logger log = SimLogger.getLogger(CanNode.class);

	private CanNode master;

	private DataID id;

	private StoreMsg storeMsg;

	private StoreReplyMsg storeReplyMsg;

	private boolean succ;

	/**
	 * stores a hash value
	 * 
	 * @param component
	 *            CanNode which whants to save
	 * @param key
	 *            the hash value
	 * @param callback
	 */
	public StoreOperation(CanNode component, DataID key,
			OperationCallback<Object> callback) {
		super(component, callback);
		master = getComponent();
		this.id = key;
		succ = false;
	}

	@Override
	protected void execute() {
		if (!succ) {
			if (id.includedInArea(master.getLocalContact().getArea())) {
				master.addStoredHashs(id, master.getLocalContact());
				calledOperationSucceeded(this);
			} else {

				storeMsg = new StoreMsg(
						master.getLocalContact().getOverlayID(), master
								.routingNext(id).getOverlayID(), master
								.getLocalContact().clone(), id,
						this.getOperationID());
				master.getTransLayer().send(storeMsg,
						master.routingNext(id).getTransInfo(),
						master.getPort(), TransProtocol.UDP);
				master.registerLookupStore(getOperationID(), this);
				this.scheduleWithDelay(CanConfig.waitTimeToStore);
			}
		} else
			calledOperationSucceeded(this);
	}

	@Override
	public Object getResult() {
		return storeReplyMsg.getSaved();
	}

	/**
	 * Is used when the storeReplyMsg arrives
	 * 
	 * @param reply
	 *            StoreReplyMsg
	 */
	public void found(StoreReplyMsg reply) {
		master.lookupStoreFinished(getOperationID());
		succ = true;

	}

	@Override
	public void calledOperationFailed(Operation op) {
		log.warn(Simulator.getSimulatedRealtime() + " Couldn't store data!!"
				+ " process ID " + this.getOperationID());
		this.operationFinished(false);
		this.scheduleWithDelay(CanConfig.waitTimeToRefreshHash);
	}

	@Override
	public void calledOperationSucceeded(Operation op) {
		log.debug("Store succeded");
		this.scheduleWithDelay(CanConfig.waitTimeToRefreshHash);
		operationFinished(true);
	}
}
