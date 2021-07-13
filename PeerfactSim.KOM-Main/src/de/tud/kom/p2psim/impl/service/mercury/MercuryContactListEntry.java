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

package de.tud.kom.p2psim.impl.service.mercury;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Class provides additional Information for each stored MercuryContact, for
 * example timestamp of last action. This information is not part of a
 * transmitted Mercury Contact.
 * 
 * TODO Bjoern: set lifetimeRange and maxTimeForRangeUpdate via config.xml
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryContactListEntry {

	private MercuryContact contact;

	private long lifetimeRange = 20 * Simulator.SECOND_UNIT;

	private long rangeValidUntil;

	private long maxTimeForRangeUpdate = 7 * Simulator.SECOND_UNIT;

	private boolean updateStarted = false;


	public MercuryContactListEntry(MercuryContact contact) {
		this.contact = contact;
		this.rangeValidUntil = Simulator.getCurrentTime() + 4
				* Simulator.SECOND_UNIT;
	}

	/**
	 * Copy, in order to prevent unwanted updates due to OOP
	 * 
	 * @return
	 */
	public MercuryContact getContact() {
		return new MercuryContact(contact.getAttribute(),
				contact.getTransInfo(),
				contact.getRange());
	}

	/**
	 * udpate MercuryContact for this Entry
	 * 
	 * @param contact
	 */
	public void setContact(MercuryContact contact) {
		this.contact = contact;
		rangeValidUntil = Simulator.getCurrentTime() + lifetimeRange;
		updateStarted = false;
	}

	/**
	 * This Contacts' range info is up-to-date and can be used
	 * 
	 * @return
	 */
	public boolean hasValidRange() {
		return Simulator.getCurrentTime() < rangeValidUntil;
	}

	/**
	 * This contact should be updated using a SendRange-Message
	 * 
	 * @return
	 */
	public boolean shouldUpdate() {
		return !updateStarted && !hasValidRange();
	}

	/**
	 * An update for this contacts' range has been initialized. Waiting for the
	 * reply.
	 */
	public void updateStarted() {
		updateStarted = true;
	}

	/**
	 * A reply did not arrive within the specified time, so this contact is
	 * assumed to be dead
	 * 
	 * @return
	 */
	public boolean isDead() {
		return Simulator.getCurrentTime() > rangeValidUntil
				+ maxTimeForRangeUpdate;
	}

	@Override
	public String toString() {
		return contact.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MercuryContactListEntry) {
			MercuryContactListEntry o = (MercuryContactListEntry) obj;
			return o.getContact().equals(this.getContact());
		}
		return false;
	}

}
