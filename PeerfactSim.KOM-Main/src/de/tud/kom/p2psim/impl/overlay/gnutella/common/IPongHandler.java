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


package de.tud.kom.p2psim.impl.overlay.gnutella.common;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPong;

/**
 * Handles the pong mechanism of a node. Manages received pong messages and generates
 * them for sending them to other peers.
 * 
 * @author Leo Nobach  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public interface IPongHandler<TContact extends GnutellaLikeOverlayContact, 
	TPongMsg extends GnutellaPong<TContact>> {

	/**
	 * Handles a pong that was received by this node.
	 * @param reply
	 */
	public abstract void receivedPong(TPongMsg reply);

	/**
	 * The node is supposed to reply with a pong. This method creates the pong message
	 * with all required information for the specific pong mechanism.
	 * @param requestingContact: the contact that requested the pong (with a ping).
	 * @param thisContact: the contact that is running this mechanism.
	 * @return
	 */
	public abstract TPongMsg generatePongMessage(TContact requestingContact, TContact thisContact);

}
