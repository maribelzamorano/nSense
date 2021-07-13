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


package de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages;

import de.tud.kom.p2psim.impl.overlay.gnutella.common.PongCache;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPong;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaOverlayContact;

/**
 * Like the Gnutella pong message, but additionally carries the peer's actual degree and the token
 * rate the neighbor that pinged shall use for its query token bucket.
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaPongMessage extends GnutellaPong<GiaOverlayContact> {

	int actualDegree;
	long tokenRate;
	
	public int getActualDegree() {
		return actualDegree;
	}
	
	public long getTokenRate() {
		return tokenRate;
	}

	public GiaPongMessage(GiaOverlayContact sender,
			PongCache<GiaOverlayContact> pongCache, int actualNumNeighbors, long tokenRate) {
		super(sender, pongCache);
		this.actualDegree = actualNumNeighbors;
		this.tokenRate = tokenRate;
		
	}
	
	public long getGnutellaPayloadSize() {
		return super.getGnutellaPayloadSize() + 3;
	}

}
