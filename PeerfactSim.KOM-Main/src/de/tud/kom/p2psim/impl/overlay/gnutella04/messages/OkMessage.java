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


package de.tud.kom.p2psim.impl.overlay.gnutella04.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class OkMessage extends AbstractOverlayMessage<GnutellaOverlayID> {

	private final static long GNUTELLA_OK_MESSAGE_SIZE = 23;

	private OverlayContact<GnutellaOverlayID> contact;

	public OkMessage(GnutellaOverlayID sender, GnutellaOverlayID receiver,
			OverlayContact<GnutellaOverlayID> contact) {
		super(sender, receiver);
		this.contact = contact;
	}

	public Message getPayload() {
		return this;
	}

	public OverlayContact<GnutellaOverlayID> getContact() {
		return contact;
	}

	public long getSize() {
		return GNUTELLA_OK_MESSAGE_SIZE;
	}

}
