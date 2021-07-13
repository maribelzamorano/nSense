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

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LookupReplyMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * 
 * This operation starts a lookup for a certain hash value. The operation id is
 * saved and if the lookup reply arrives the operation is removed.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupOperation extends AbstractOperation<CanNode, Object>
		implements OperationCallback {

	private CanNode master;

	private DataID id;

	private LookupMsg lookupMsg;

	private LookupReplyMsg lookupReplyMsg;

	private boolean succ;

	private int allreadyTried;

	private long startTime;

	/**
	 * starts a lookup. Sends a hash and gets a CanOverlayContact
	 * 
	 * @param component
	 *            node which needs a contact to a hash value
	 * @param id
	 *            Hash value
	 * @param callback
	 */
	public LookupOperation(CanNode component, DataID id,
			OperationCallback callback) {
		super(component);
		this.id = id;
		this.master = getComponent();
		succ = false;
		allreadyTried = 0;
	}

	public void execute() {
		if (master.getPeerStatus() == PeerStatus.PRESENT) {
			if (succ == false && allreadyTried < CanConfig.numberLookups) {
				master.getDataOperation.addStartedLookup();

				if (id.includedInArea(master.getLocalContact().getArea())) {
					if (master.getStoredHashToID(id) != null)
						calledOperationSucceeded(this);
					else
						calledOperationFailed(this);
				} else {
					try {
						startTime = Simulator.getCurrentTime();
						lookupMsg = new LookupMsg(master.getLocalContact()
								.getOverlayID(), master.routingNext(id)
								.getOverlayID(), master.getLocalContact()
								.clone(), id, this.getOperationID());

						master.getTransLayer().send(lookupMsg,
								master.routingNext(id).getTransInfo(),
								master.getPort(), TransProtocol.UDP);
						master.registerLookupStore(getOperationID(), this);
						this.scheduleWithDelay(CanConfig.waitTimeToStore);
					} catch (Exception e) {
						// just in case
					}
				}
				allreadyTried++;
			} else if (succ == true)
				calledOperationSucceeded(this);
			else if (succ == false && allreadyTried >= CanConfig.numberLookups)
				calledOperationFailed(this);
		}
	}

	/**
	 * Is used to tell the operation that a hash value was found.
	 * 
	 * @param reply
	 *            reply message with the CanOveralyContact to the hash value;
	 */
	public void found(LookupReplyMsg reply) {
		if (master.getPeerStatus() == PeerStatus.PRESENT) {
			if (reply.getResult() != null)
				succ = true;
			master.getDataOperation.addTimeForHops(Simulator.getCurrentTime()
					- startTime);
			operationFinished(true);
		}
	}

	@Override
	public Object getResult() {
		return lookupReplyMsg.getResult();
	}

	@Override
	public void calledOperationFailed(Operation op) {
		master.lookupStoreFinished(getOperationID());
		this.operationFinished(false);
	}

	@Override
	public void calledOperationSucceeded(Operation op) {
		master.lookupStoreFinished(getOperationID());
		this.operationFinished(true);
	}

}
