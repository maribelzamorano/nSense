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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.ultrapeer;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.PongCachePongHandler;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPong;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.IGnutella06Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PongHandler
		extends
		PongCachePongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> {

	public PongHandler(
			AbstractGnutellaLikeNode<Gnutella06OverlayContact, IGnutella06Config> node,
			ConnectionManager<?, Gnutella06OverlayContact, IGnutella06Config, GnutellaPong<Gnutella06OverlayContact>> mgr) {
		super(node, mgr);
	}

	@Override
	public GnutellaPong<Gnutella06OverlayContact> generatePongMessage(
			Gnutella06OverlayContact requestingContact,
			Gnutella06OverlayContact thisContact) {
		return new GnutellaPong<Gnutella06OverlayContact>(thisContact,
				getLocalPongCache());
	}

}
