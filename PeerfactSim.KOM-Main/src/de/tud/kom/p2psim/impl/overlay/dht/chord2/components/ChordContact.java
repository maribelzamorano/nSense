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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import java.io.Serializable;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * ChordContact encapsulates ChordId and Transport Address. This information is
 * used to contact between overlay nodes.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordContact implements OverlayContact<ChordID>,
		Comparable<ChordContact>, Serializable {

	private final ChordID id;

	private transient final TransInfo transInfo;

	private boolean isAlive;

	public ChordContact(ChordID id, TransInfo transInfo) {
		this.id = id;
		this.transInfo = transInfo;
	}

	@Override
	public ChordID getOverlayID() {
		return id;
	}

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	@Override
	public ChordContact clone() {
		ChordContact newContact = new ChordContact(this.id, this.transInfo);
		newContact.setAlive(this.isAlive);
		return newContact;
	}

	public int getDistance(ChordContact contact) {
		return this.id.getDistance(contact.getOverlayID());
	}

	@Override
	public int compareTo(ChordContact o) {
		return this.id.compareTo(o.getOverlayID());
	}

	public boolean equals(ChordContact o) {
		return (o == null) ? false : id.equals(o.getOverlayID());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChordContact) {
			return equals((ChordContact) o);
		}
		return super.equals(o);
	}

	/**
	 * @param a
	 * @param b
	 * @return if ChordContact stands in the interval (a,b) in the ring form.
	 * 
	 */
	public boolean between(ChordContact a, ChordContact b) {
		if(a == null || b == null )
			return false;
		return this.getOverlayID().between(a.getOverlayID(), b.getOverlayID());
	}

	@Override
	public String toString() {
		return "" + id;
	}
}
