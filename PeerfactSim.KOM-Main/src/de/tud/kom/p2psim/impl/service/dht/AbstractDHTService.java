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

package de.tud.kom.p2psim.impl.service.dht;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.SupportOperations;
import de.tud.kom.p2psim.api.overlay.dht.DHTEntry;
import de.tud.kom.p2psim.api.overlay.dht.DHTKey;
import de.tud.kom.p2psim.api.overlay.dht.DHTListener;
import de.tud.kom.p2psim.api.overlay.dht.DHTListenerSupported;
import de.tud.kom.p2psim.api.overlay.dht.DHTValue;

/**
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
abstract public class AbstractDHTService implements Component,
		SupportOperations,
		DHTListener {

	private Host host;

	private DHTListenerSupported node;

	private Set<DHTEntry> storedEntries;


	/**
	 * Create a service and register as Listener at the DHTNode
	 * 
	 * @param node
	 */
	public AbstractDHTService(DHTListenerSupported node) {
		storedEntries = new HashSet<DHTEntry>();
		if (node != null) {
			this.node = node;
			node.registerDHTListener(this);
		}
	}

	@Override
	public void addDHTEntry(DHTKey key, DHTValue value) {
		addDHTEntry(new SimpleDHTEntry(key, value));
	}

	protected void addDHTEntry(DHTEntry entry) {
		if (!storedEntries.add(entry)) {
			// update an entry, as the value might have changed
			// add will not be executed if Set already contained the object
			removeDHTEntry(entry.getKey());
			storedEntries.add(entry);
		}
	}

	@Override
	public void removeDHTEntry(DHTKey key) {
		Iterator<DHTEntry> it = storedEntries.iterator();
		while (it.hasNext()) {
			DHTEntry entry = it.next();
			if (entry.getKey().equals(key)) {
				it.remove();
			}
		}
	}

	@Override
	public DHTEntry getDHTEntry(DHTKey key) {
		Iterator<DHTEntry> it = storedEntries.iterator();
		while (it.hasNext()) {
			DHTEntry entry = it.next();
			if (entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public DHTValue getDHTValue(DHTKey key) {
		DHTEntry entry = getDHTEntry(key);
		if (entry == null)
			return null;
		return entry.getValue();
	}

	@Override
	public Set<DHTEntry> getDHTEntries() {
		return storedEntries;
	}

	@Override
	public int getNumberOfDHTEntries() {
		return storedEntries.size();
	}


	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public Host getHost() {
		return host;
	}

	protected DHTListenerSupported getNode() {
		return node;
	}

}
