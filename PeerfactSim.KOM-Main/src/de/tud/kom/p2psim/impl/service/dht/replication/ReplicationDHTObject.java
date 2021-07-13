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

import java.util.List;

import de.tud.kom.p2psim.api.overlay.dht.DHTKey;
import de.tud.kom.p2psim.api.overlay.dht.DHTValue;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.service.dht.SimpleDHTEntry;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class encapsulates a DHTObject stored in ReplicationDHTService
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTObject extends SimpleDHTEntry {

	/**
	 * timestamp when this item was republished
	 */
	private long timeOfLastRepublication;

	/**
	 * List of all Replications
	 */
	private List<TransInfo> replications;

	/**
	 * Root TransInfo
	 */
	private TransInfo root;

	/**
	 * Create a new Entry in this DHT
	 * 
	 * @param key
	 *            The DHTKey
	 * @param object
	 *            The DHTObject
	 * @param root
	 *            Root TransInfo, null if own Node is root
	 * @param replications
	 *            List of TransInfos this Object is also stored on
	 */
	public ReplicationDHTObject(DHTKey key, DHTValue object, TransInfo root,
			List<TransInfo> replications) {
		super(key, object);
		this.root = root;
		this.replications = replications;
		this.timeOfLastRepublication = Simulator.getCurrentTime();
	}

	/**
	 * get a List of all Replications TransInfos
	 * 
	 * @return
	 */
	public List<TransInfo> getReplications() {
		return replications;
	}
	
	public TransInfo getRoot() {
		return root;
	}

	public void updateTimestamp() {
		timeOfLastRepublication = Simulator.getCurrentTime();
	}

	public long getTimestamp() {
		return timeOfLastRepublication;
	}

	@Override
	public String toString() {
		return getKey().toString() + getValue().toString();
	}

}
