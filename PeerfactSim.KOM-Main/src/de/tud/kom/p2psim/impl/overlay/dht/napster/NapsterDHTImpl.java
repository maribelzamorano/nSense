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


package de.tud.kom.p2psim.impl.overlay.dht.napster;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.napster.NapsterDHT;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterDHTImpl implements NapsterDHT {

	private static Logger log = SimLogger.getLogger(NapsterDHTImpl.class);

	private HashMap<BigInteger, NapsterOverlayContact> dhtTable;

	public NapsterDHTImpl() {
		dhtTable = new HashMap<BigInteger, NapsterOverlayContact>();
	}

	public void addContact(NapsterOverlayContact contact) {
		if (dhtTable.containsKey(contact.getOverlayID().getID())) {
			log.debug("Cannot add contact " + contact.toString()
					+ ", because Entry with overlayID "
					+ contact.getOverlayID().getID()
					+ " as key, already exitsts");
		} else {
			BigInteger key = contact.getOverlayID().getID();
			dhtTable.put(key, contact);
			log.debug("Size of the DHT after adding " + key + " is "
					+ dhtTable.size());
		}
	}

	public List<NapsterOverlayContact> allContacts() {
		// Not needed
		return null;
	}

	public void clearContacts() {
		dhtTable.clear();
		log.debug("Cleared the DHT");
	}

	public boolean containsOverlayID(NapsterOverlayID oid) {
		if (dhtTable.containsKey(oid.getID())) {
			return true;
		} else {
			return false;
		}
	}

	public NapsterOverlayContact getPredecessor(NapsterOverlayID oid) {
		if (dhtTable.size() > 0) {
			if (dhtTable.containsKey(oid.getID())) {
				// Creating a sorted list out of a HashMap
				Set<BigInteger> keySet = dhtTable.keySet();
				List<BigInteger> list = Collections.list(Collections
						.enumeration(keySet));
				Collections.sort(list);

				// Search the Predecessor of the node with the NapsterOverlayID
				// oid
				int iter = 0;
				boolean flag = true;
				while (flag) {
					if (oid.getID().compareTo((list.get(iter))) == 0) {
						flag = false;
					} else {
						iter++;
					}
				}
				if (iter == 0) {
					iter = list.size();
				}
				NapsterOverlayContact contact = dhtTable
						.get(list.get(iter - 1));
				log.debug("Looked up node " + contact.toString() + " for key "
						+ oid.getID());
				return contact;

			} else {
				log.warn("No NapsterClient with OverlayID " + oid.getID()
						+ " is registered");
				return null;
			}

		} else {
			log
					.error("all nodes left the napster-overlay. This request is an orphan");
			return null;
		}
	}

	public NapsterOverlayContact getContact(NapsterOverlayID oid) {
		if (dhtTable.containsKey(oid.getID())) {
			NapsterOverlayContact contact = dhtTable.get(oid);
			log.debug("Returned OverlayContact " + contact.toString()
					+ " from the DHT");
			return contact;
		} else {
			log.debug("No entry with OverlayID " + oid.getID() + " was found");
			return null;
		}
	}

	// only for an workaround
	public void removeContact(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		removeContact(new NapsterOverlayID(ip));
	}

	public void removeContact(NapsterOverlayID oid) {
		NapsterOverlayContact contact = dhtTable.remove(oid.getID());
		if (contact != null) {
			log.debug("Size of the DHT after deleting is " + dhtTable.size());
		} else {
			log.warn("No entry with OverlayID " + oid.getID()
					+ " could be removed");
		}
	}

	public NapsterOverlayContact nodeLookup(NapsterOverlayID key) {
		// Creating a sorted list out of a HashMap
		Set<BigInteger> keySet = dhtTable.keySet();
		List<BigInteger> list = Collections.list(Collections
				.enumeration(keySet));
		Collections.sort(list);

		// Search the NapsterOverlayContact of the node, which is responsible
		// for the given key
		if (dhtTable.size() > 0) {
			int iter = 0;
			boolean flag = true;
			while (flag) {
				if (key.getID().compareTo(list.get(iter)) < 1) {
					flag = false;
				} else {
					iter++;
					if (iter == list.size()) {
						flag = false;
						iter = 0;
					}
				}
			}
			NapsterOverlayContact contact = dhtTable.get(list.get(iter));
			log.debug("Looked up node " + contact.toString() + " for key "
					+ key.getID());
			return contact;
		} else {
			log
					.error("all nodes left the napster-overlay. This request is an orphant");
			return null;
		}
	}

}
