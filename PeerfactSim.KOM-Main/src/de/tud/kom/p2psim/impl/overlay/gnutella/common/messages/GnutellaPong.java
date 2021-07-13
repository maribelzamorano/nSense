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


package de.tud.kom.p2psim.impl.overlay.gnutella.common.messages;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.PongCache;

/**
 * The pong message is replied to every connected peer that requests
 * it via a ping message. It tells the requesting peer that it is alive
 * and transmits information for node discovery, like a pong cache.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaPong<TContact extends GnutellaLikeOverlayContact> extends AbstractGnutellaMessage {

	/**
	 * Returns the sender of this pong message.
	 * @return
	 */
	public TContact getSender() {
		return sender;
	}

	private TContact sender;

	private PongCache<TContact> pongCache;

	/**
	 * Creates a new pong message, given the sender of it and the pong cache
	 * that shall be transmitted.
	 * @param sender
	 * @param pongCache
	 */
	public GnutellaPong(TContact sender, PongCache<TContact> pongCache) {
		this.sender = sender;
		this.pongCache = pongCache;
	}

	@Override
	public long getGnutellaPayloadSize() {
		if (pongCache == null)
			return 0;
		return pongCache.getSize();
	}

	/**
	 * Returns the pong cache transmitted with this message.
	 * @return
	 */
	public PongCache<TContact> getPongCache() {
		return pongCache;
	}

	public String toString() {
		return "PONG: sender=" + sender + ", pongCache=" + pongCache;
	}

}
