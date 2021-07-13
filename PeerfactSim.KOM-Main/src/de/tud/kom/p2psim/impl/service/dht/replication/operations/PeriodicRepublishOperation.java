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
import java.util.Vector;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.dht.DHTEntry;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTConfig;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTObject;
import de.tud.kom.p2psim.impl.service.dht.replication.ReplicationDHTService;

/**
 * This operation is periodically executed and republishes all Objects with a
 * certain threshold of dead contacts this node is a root of.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PeriodicRepublishOperation extends ReplicationDHTAbstractOperation {

	public PeriodicRepublishOperation(ReplicationDHTService component,
			OperationCallback<Object> callback, ReplicationDHTConfig config) {
		super(component, callback, config);
	}

	@Override
	protected void execute() {
		// iterate over all objects this node is root of
		for (DHTEntry entry : getComponent().getDHTEntries()) {
			ReplicationDHTObject obj = (ReplicationDHTObject) entry;
			if (obj.getRoot() == null) {
				// this Node is root
				List<TransInfo> deadContacts = new Vector<TransInfo>();
				for (TransInfo replication : obj.getReplications()) {
					// contact did not reply to ping, so it is considered
					// offline
					if (getComponent().getContact(replication).isOffline()) {
						deadContacts.add(replication);
					}
				}
				if (deadContacts.size() >= getConfig().getNumberOfReplicates()
						- getConfig().getMinimumNumberOfReplicates()) {
					// Republish this object
					ReplicationOperation op = new ReplicationOperation(
							getComponent(),
							Operations.getEmptyCallback(), getConfig(), obj,
							getComponent().getNeighborsContacts());
					op.scheduleImmediately();
					System.err.println("REPUBLISH: " + obj.toString());
				}
			}
		}

	}

	/**
	 * no result.
	 */
	@Override
	public Object getResult() {
		return null;
	}

}
