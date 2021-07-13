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

package de.tud.kom.p2psim.impl.service.dht.replication;

import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Contact Information of a Node that stores a DHTObject.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTContact {

	/**
	 * this Contacts TransInfo
	 */
	private TransInfo transInfo;

	/**
	 * time this Contact had its most recent interaction with this service
	 */
	private long lastAction;

	private long ttl;

	private boolean isOffline = false;

	/**
	 * Create a new Contact
	 * 
	 * @param transInfo
	 * @param ttl
	 *            Time to Live for this contact
	 */
	public ReplicationDHTContact(TransInfo transInfo, long ttl) {
		this.transInfo = transInfo;
		this.ttl = ttl;
		updateLastAction();
	}

	/**
	 * Set lastAction of this contact to "now"
	 */
	public void updateLastAction() {
		lastAction = Simulator.getCurrentTime();
		isOffline = false;
	}

	/**
	 * Mark this contact as offline
	 * 
	 * @param offline
	 */
	public void markAsOffline() {
		isOffline = true;
	}

	/**
	 * is this contact offline?
	 * 
	 * @return
	 */
	public boolean isOffline() {
		return isOffline || Simulator.getCurrentTime() > ttl + lastAction;
	}

	/**
	 * time this Contact had its last interaction with our service
	 * 
	 * @return
	 */
	public long getLastAction() {
		return lastAction;
	}

	/**
	 * the contacts TransInfo
	 * 
	 * @return
	 */
	public TransInfo getTransInfo() {
		return transInfo;
	}


	/**
	 * Compares two ReplicationDHTContacts, returns true if their TransInfo is
	 * equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReplicationDHTContact) {
			ReplicationDHTContact c = (ReplicationDHTContact) obj;
			return c.getTransInfo().equals(getTransInfo());
		}
		return false;
	}

	/**
	 * Clone a Contact, so that timestamps are deleted. The cloned instance and
	 * the original instance will return equals() == true
	 */
	@Override
	public ReplicationDHTContact clone() {
		return new ReplicationDHTContact(getTransInfo(), ttl);
	}

}
