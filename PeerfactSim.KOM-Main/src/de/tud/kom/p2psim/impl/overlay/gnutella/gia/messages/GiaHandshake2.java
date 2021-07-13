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


/**
 * 
 */
package de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages;

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaOverlayContact;

/**
 * Second message of the Gia connection handshake. Carries the requestee's decision whether it accepts the
 * connection attempt from the requester, its degree, the initial token allocation rate and, if explicitly 
 * requested by the initiator, additional peers to connect to.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaHandshake2 extends GiaMessage {

	public GiaOverlayContact getY() {
		return y;
	}

	public int getDegreeOfY() {
		return degreeOfY;
	}

	private GiaOverlayContact y;
	private int degreeOfY;
	private boolean accepted;
	private List<GiaOverlayContact> tryPeers;
	private long initialTAR;
	private GiaOverlayContact x;

	public GiaHandshake2(boolean accepted, GiaOverlayContact y, GiaOverlayContact x, int degreeOfY, List<GiaOverlayContact> tryPeers, long initialTAR) {
		super();
		
		this.y=y;
		this.x=x;
		this.degreeOfY=degreeOfY;
		this.accepted=accepted;
		this.tryPeers=tryPeers;
		this.initialTAR = initialTAR;
	}
	
	public long getInitialTokenAllocationRate() {
		return initialTAR;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
	
	public List<GiaOverlayContact> getTryPeers() {
		return tryPeers;
	}
	
	@Override
	public long getGnutellaPayloadSize() {
		return y.getSize() + 4;	//X is just included for debugging purposes.
	}
	
	
	public String toString() {
		return "GiaHandshake2(y=" + y + ", x=" + x + ", degreeOfY=" + degreeOfY + ", accepted=" + accepted + " tryPeers=" + tryPeers + ")";
	}
	
}
