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
import java.util.Set;
import java.util.Vector;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.dht.DHTEntry;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTConfig;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTContact;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTObject;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTService;
import de.tud.kom.p2psim.impl.service.dht.replication.messages.PingMessage;

/**
 * Periodically executed. Checks all roots and if a root does not respond, try
 * next contact on the list. First contact that responds will be elected as new
 * root, announces itself to all holders of the file. This is initiated by the
 * service, as it works on a per-file-basis
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class CheckRootOperation extends ReplicationDHTAbstractOperation {

	private List<TransInfo> roots;

	private int actRoot;

	public CheckRootOperation(ReplicationDHTService component,
			OperationCallback<Object> callback, ReplicationDHTConfig config) {
		super(component, callback, config);
		roots = new Vector<TransInfo>();
	}

	@Override
	protected void execute() {
		Set<DHTEntry> entries = getComponent()
				.getDHTEntries();

		// Contact all roots
		for (DHTEntry entry : entries) {
			ReplicationDHTObject object = (ReplicationDHTObject) entry;
			if (object.getRoot() == null) {
				// own Contact is root
				continue;
			}
			if (!roots.contains(object.getRoot())) {
				ReplicationDHTContact contact = getComponent().getContact(
						object.getRoot());
				if (contact != null) {
					//if (contact.getLastAction() + getConfig().getContactTTL() < Simulator
					// .getCurrentTime()) {
						roots.add(object.getRoot());
					//} else {
					//	continue;
					//}
				} else {
					// This should not happen...
					System.err
							.println("DHTService: CheckRootOperation: contact is null");
				}
			}
		}

		checkNextRoot();
	}

	private void checkNextRoot() {
		if (actRoot >= roots.size()) {
			operationFinished(true);
			return;
		}
		PingMessage msg = new PingMessage();
		sendMessage(msg, roots.get(actRoot), getConfig().getNumberOfPingTries());
	}

	@Override
	protected void sendMessageFailed() {
		getComponent().contactDidNotRespond(roots.get(actRoot));
		actRoot++;
		checkNextRoot();
	}

	@Override
	protected void sendMessageSucceeded() {
		getComponent().contactDidRespond(roots.get(actRoot));
		actRoot++;
		checkNextRoot();
	}

	/**
	 * This operation has no result
	 */
	@Override
	public Object getResult() {
		return null;
	}

}
