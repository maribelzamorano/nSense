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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayRoutingTable;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaOverlayRoutingTable implements
		OverlayRoutingTable<GnutellaOverlayID, GnutellaOverlayContact> {

	private OverlayID overlayID;

	private Map<OverlayID, GnutellaOverlayContact> overlayIDs = new HashMap<OverlayID, GnutellaOverlayContact>();

	private Set<GnutellaOverlayContact> activeContacts = new HashSet<GnutellaOverlayContact>();

	private Set<GnutellaOverlayContact> inactiveContacts = new HashSet<GnutellaOverlayContact>();

	// number of connections
	private int numConn;

	private long refresh;

	private long contactTimeout;

	private long descriptorTimeout;

	// TODO low priority: Descriptoren ablegen und mit Timeout versehen
	private Map<BigInteger, Set<OverlayContact<GnutellaOverlayID>>> acceptPong = new HashMap<BigInteger, Set<OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, OverlayContact<GnutellaOverlayID>> routePong = new HashMap<BigInteger, OverlayContact<GnutellaOverlayID>>();

	private Map<BigInteger, Set<OverlayContact<GnutellaOverlayID>>> acceptQueryHit = new HashMap<BigInteger, Set<OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, OverlayContact<GnutellaOverlayID>> routeQueryHit = new HashMap<BigInteger, OverlayContact<GnutellaOverlayID>>();

	private Map<BigInteger, Map<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>> routePush = new HashMap<BigInteger, Map<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>>();

	private Map<BigInteger, BigInteger> descriptorRefreshTime = new HashMap<BigInteger, BigInteger>();

	public GnutellaOverlayRoutingTable(OverlayID overlayID) {
		this.overlayID = overlayID;
	}

	public void initiatedPing(GnutellaOverlayID overlayID2,
			BigInteger descriptor) {
		routePong.put(descriptor, null);
	}

	/**
	 * @param contact
	 * @param descriptor
	 * @return if new descriptor
	 */
	public boolean incomingPing(GnutellaOverlayID id, BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contact = getContact(id);
		if (routePong.containsKey(descriptor) || contact == null) {
			return false;
		}
		routePong.put(descriptor, contact);
		return true;
	}

	public void outgoingPing(OverlayContact<GnutellaOverlayID> contact,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!acceptPong.containsKey(descriptor)) {
			acceptPong.put(descriptor,
					new HashSet<OverlayContact<GnutellaOverlayID>>());
		}
		acceptPong.get(descriptor).add(contact);
	}

	public boolean incomingPong(GnutellaOverlayID idSender,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contactSender = getContact(idSender);
		// return contact to forward message to
		if (!acceptPong.containsKey(descriptor)
				|| !routePong.containsKey(descriptor)) {
			// do not route messages with wrong descriptor
			return false;
		}
		if (!acceptPong.get(descriptor).contains(contactSender)) {
			// do not reply messages without having sent a request
			return false;
		}
		return true;
	}

	public OverlayContact<GnutellaOverlayID> outgoingPong(BigInteger descriptor) {
		refreshDesriptor(descriptor);
		// return contact to forward message to
		return routePong.get(descriptor);
	}

	public void initiatedQuery(GnutellaOverlayID overlayID2,
			BigInteger descriptor) {
		routeQueryHit.put(descriptor, null);
	}

	public boolean incomingQuery(GnutellaOverlayID id, BigInteger descriptor) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contact = getContact(id);
		if (routeQueryHit.containsKey(descriptor) || contact == null) {
			return false;
		}
		routeQueryHit.put(descriptor, contact);
		return true;
	}

	public void outgoingQuery(OverlayContact<GnutellaOverlayID> contact,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!acceptQueryHit.containsKey(descriptor)) {
			acceptQueryHit.put(descriptor,
					new HashSet<OverlayContact<GnutellaOverlayID>>());
		}
		acceptQueryHit.get(descriptor).add(contact);
	}

	public boolean incomingQueryHit(GnutellaOverlayID idSender,
			BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> contactInitiator) {
		refreshDesriptor(descriptor);
		OverlayContact<GnutellaOverlayID> contactSender = this
				.getContact(idSender);
		if (!acceptQueryHit.containsKey(descriptor)
				|| !routeQueryHit.containsKey(descriptor)) {
			// do not route messages with wrong descriptor
			return false;
		}
		if (!acceptQueryHit.get(descriptor).contains(contactSender)) {
			// do not reply messages without having sent a request
			return false;
		}
		if (!routePush.containsKey(descriptor)) {
			routePush
					.put(
							descriptor,
							new HashMap<OverlayContact<GnutellaOverlayID>, OverlayContact<GnutellaOverlayID>>());
		}
		if (routePush.get(descriptor).containsKey(contactInitiator)) {
			// do not accept if a push route already exists
			return false;
		}
		routePush.get(descriptor).put(contactInitiator, contactSender);
		return true;
	}

	public OverlayContact<GnutellaOverlayID> outgoingQueryHit(
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		// return contact to forward message to
		return routeQueryHit.get(descriptor);
	}

	public OverlayContact<GnutellaOverlayID> outgoingPush(
			OverlayContact<GnutellaOverlayID> contactQueryHitInitiator,
			BigInteger descriptor) {
		refreshDesriptor(descriptor);
		if (!routePush.containsKey(descriptor)) {
			return null;
		}
		if (!routePush.get(descriptor).containsKey(contactQueryHitInitiator)) {
			return null;
		}
		return routePush.get(descriptor).get(contactQueryHitInitiator);
	}

	public void addInactiveContact(GnutellaOverlayContact c) {
		GnutellaOverlayContact contact = new GnutellaOverlayContact(c);
		if (!c.getOverlayID().equals(this.overlayID)
				&& !this.overlayIDs.containsKey(c.getOverlayID())) {
			this.overlayIDs.put(contact.getOverlayID(), contact);
			this.inactiveContacts.add(contact);
		}
	}

	public void addContact(GnutellaOverlayContact c) {
		GnutellaOverlayContact contact = new GnutellaOverlayContact(c);
		if (!this.activeContacts.contains(contact)) {
			inactiveContacts.remove(contact);
			if (this.activeContacts.size() == this.numConn) {
				// TODO sort active Contacts and disconnect worst contact
				List<GnutellaOverlayContact> sortedActiveContacts = new LinkedList<GnutellaOverlayContact>(
						activeContacts);
				Collections.sort(sortedActiveContacts,
						new GnutellaOverlayContactRankComparator());
				GnutellaOverlayContact inactiveContact = sortedActiveContacts
						.remove(0);
				activeContacts.remove(inactiveContact);
				inactiveContacts.add(inactiveContact);
			}
			if (!overlayIDs.containsKey(contact.getOverlayID())) {
				this.overlayIDs.put(contact.getOverlayID(), contact);
			}
			contact.reset();
			contact.refresh();
			this.activeContacts.add(contact);
		}
		overlayIDs.get(contact.getOverlayID()).refresh();
	}

	public List<GnutellaOverlayContact> allContacts() {
		return new LinkedList<GnutellaOverlayContact>(activeContacts);
	}

	public int numberOfActiveContacts() {
		return activeContacts.size();
	}

	public List<GnutellaOverlayContact> inactiveContacts() {
		return new LinkedList<GnutellaOverlayContact>(inactiveContacts);
	}

	public OverlayContact removeInactiveContact() {
		Iterator<GnutellaOverlayContact> iterator = inactiveContacts.iterator();
		if (iterator.hasNext()) {
			GnutellaOverlayContact contact = iterator.next();
			this.inactiveContacts.remove(contact);
			this.overlayIDs.remove(contact);
			return contact;
		}
		return null;
	}

	public GnutellaOverlayContact getContact(GnutellaOverlayID oid) {
		return this.overlayIDs.get(oid);
	}

	public boolean isActive(GnutellaOverlayID oid) {
		GnutellaOverlayContact contact = this.overlayIDs.get(oid);
		if (contact == null || !activeContacts.contains(contact)) {
			return false;
		}
		return true;
	}

	public void removeContact(GnutellaOverlayID oid) {
		throw new RuntimeException("The method removeContact() within "
				+ this.getClass().getSimpleName() + "is not implemented.");
	}

	public void clearContacts() {
		this.overlayIDs.clear();
		this.activeContacts.clear();
		this.inactiveContacts.clear();
	}

	public void setNumConn(int numConn) {
		this.numConn = numConn;
	}

	public int getNumConn() {
		return numConn;
	}

	public void setRefresh(long refresh) {
		this.refresh = refresh;
	}

	public void setContactTimeout(long contactTimeout) {
		this.contactTimeout = contactTimeout;
	}

	public void setDescriptorTimeout(long descriptorTimeout) {
		this.descriptorTimeout = descriptorTimeout;
	}

	public void refreshDesriptor(BigInteger descriptor) {
		if (descriptorRefreshTime.containsKey(descriptor)) {
			descriptorRefreshTime.remove(descriptor);
		}
		descriptorRefreshTime.put(descriptor, BigInteger.valueOf(Simulator
				.getCurrentTime()));
	}

	public List<BigInteger> getDeadContacts() {
		List<BigInteger> deadDescriptors = new LinkedList<BigInteger>();
		Iterator<BigInteger> iterator = descriptorRefreshTime.keySet()
				.iterator();
		while (iterator.hasNext()) {
			BigInteger descriptor = iterator.next();
			if (descriptorRefreshTime.get(descriptor).longValue()
					+ this.descriptorTimeout < Simulator.getCurrentTime()) {
				deadDescriptors.add(descriptor);
			}
		}
		for (BigInteger descriptor : deadDescriptors) {
			acceptPong.remove(descriptor);
			routePong.remove(descriptor);
			acceptQueryHit.remove(descriptor);
			routeQueryHit.remove(descriptor);
			routePush.remove(descriptor);
			descriptorRefreshTime.remove(descriptor);
		}
		return deadDescriptors;
	}

	public List<GnutellaOverlayContact> getRefreshContacts() {
		List<GnutellaOverlayContact> refreshContacts = new LinkedList<GnutellaOverlayContact>();
		List<GnutellaOverlayContact> timeoutContacts = new LinkedList<GnutellaOverlayContact>();
		Iterator<GnutellaOverlayContact> iterator = activeContacts.iterator();
		while (iterator.hasNext()) {
			GnutellaOverlayContact contact = iterator.next();
			// remove dead contacts
			if (contact.getLastRefresh() + this.contactTimeout < Simulator
					.getCurrentTime()) {
				timeoutContacts.add(contact);
			}
			// return contacts to be refreshed
			if (contact.getLastRefresh() + this.refresh < Simulator
					.getCurrentTime()) {
				refreshContacts.add(contact);
			}
		}
		// remove dead contacts
		for (GnutellaOverlayContact timeoutContact : timeoutContacts) {
			activeContacts.remove(timeoutContact);
		}
		return refreshContacts;
	}
}
