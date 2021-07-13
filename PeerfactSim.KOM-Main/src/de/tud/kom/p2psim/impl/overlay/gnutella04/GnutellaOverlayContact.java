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

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaOverlayContact implements OverlayContact<GnutellaOverlayID>, Comparable<GnutellaOverlayContact> {

	private GnutellaOverlayID overlayID;
	private TransInfo transInfo;
	
	private long timeActivated;
	private long lastRefresh;

	// TODO gescheites Ranking
	
	public GnutellaOverlayContact(GnutellaOverlayID overlayID, TransInfo transInfo) {
		this.overlayID = overlayID;
		this.transInfo = transInfo;
	}
	
	public GnutellaOverlayContact(OverlayContact<GnutellaOverlayID> contact) {
		this.overlayID = contact.getOverlayID();
		this.transInfo = contact.getTransInfo();
	}

	public GnutellaOverlayID getOverlayID() {
		return this.overlayID;
	}

	public TransInfo getTransInfo() {
		return this.transInfo;
	}
	
	public int hashCode() {
		return this.overlayID.hashCode();
	}

	public int compareTo(GnutellaOverlayContact contact) {
		return this.getOverlayID().compareTo(contact.getOverlayID());
	}

	public boolean equals(Object o) {
		if (o instanceof GnutellaOverlayContact) {
			GnutellaOverlayContact contact = (GnutellaOverlayContact) o;
			return this.overlayID.equals(contact.getOverlayID());
		}
		return false;
	}
	
	public String toString() {
		return this.overlayID.toString();
	}
		
	public void reset() {
		this.timeActivated = Simulator.getCurrentTime();
	}
	
	public double getRank() {
		return Double.MAX_VALUE / (Simulator.getCurrentTime() - this.timeActivated);
	}

	public long getLastRefresh() {
		return lastRefresh;
	}

	public void refresh() {
		this.lastRefresh = Simulator.getCurrentTime();
	}
	
}
