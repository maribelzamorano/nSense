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

package de.tud.kom.p2psim.impl.service.dht.replication.operations;

import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTConfig;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTObject;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTService;
import de.tud.kom.p2psim.impl.service.dht.replication.messages.NewRootMessage;

/**
 * This operation will select a new root for given DHTEntry.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class NewRootOperation extends ReplicationDHTAbstractOperation {

	private ReplicationDHTObject object;

	private List<TransInfo> contacts;

	private int actIndex = 0;

	private boolean cancel = false;

	public NewRootOperation(ReplicationDHTService component,
			OperationCallback<Object> callback, ReplicationDHTConfig config,
			ReplicationDHTObject object) {
		super(component, callback, config);
		this.object = object;
	}

	@Override
	protected void execute() {
		if (cancel) {
			operationFinished(true);
			return;
		}
		// System.err.println(Simulator.getFormattedTime(Simulator
		// .getCurrentTime())
		// + ": NewRootOperation started: "
		// + toString());
		contacts = object.getReplications();
		nextContact();
	}

	/**
	 * Cancel this operation!
	 */
	public void cancel() {
		cancel = true;
	}

	protected void nextContact() {
		if (actIndex >= contacts.size()) {
			operationFinished(true);
			return;
		}
		TransInfo receiver = contacts.get(actIndex);
		NewRootMessage msg = new NewRootMessage(object.getKey());
		sendMessage(msg, receiver, getConfig().getNumberOfReplicationTries());
	}

	@Override
	protected void sendMessageFailed() {
		actIndex++;
		nextContact();
	}

	@Override
	protected void sendMessageSucceeded() {
		// succeeded, new root will issue a ReplicationOperation.
		operationFinished(true);
	}

	/**
	 * this Operation has no result
	 */
	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public String toString() {
		return object.toString();
	}

}
