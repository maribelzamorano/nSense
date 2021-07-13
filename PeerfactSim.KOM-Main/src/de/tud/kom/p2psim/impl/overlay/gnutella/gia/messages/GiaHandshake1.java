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

import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaOverlayContact;

/**
 * This message initiates a Gia handshake. Carries the degree of the requesting peer and whether it requests
 * additional peers to try to connect to.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaHandshake1 extends GiaMessage {

	public GiaOverlayContact getSender() {
		return x;
	}

	public int getDegreeOfSender() {
		return degreeOfX;
	}

	private GiaOverlayContact x;
	private int degreeOfX;
	private boolean requestingTryPeers;
	private GiaOverlayContact y;

	public boolean isRequestingTryPeers() {
		return requestingTryPeers;
	}

	public GiaHandshake1(GiaOverlayContact x, GiaOverlayContact y, int degreeOfX, boolean requestingTryPeers) {
		super();
		
		this.x=x;
		this.y=y;
		this.degreeOfX=degreeOfX;
		this.requestingTryPeers=requestingTryPeers;
	}
	
	@Override
	public long getGnutellaPayloadSize() {
		return x.getSize() + 4;		//Y is just included for debugging purposes.
	}
	
	public String toString() {
		return "GiaHandshake1(x=" + x + ", y=" + y + ", degreeOfX=" + degreeOfX + ", reqTryPeers=" + requestingTryPeers + ")";
	}

}
