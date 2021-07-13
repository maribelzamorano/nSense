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

import java.math.BigInteger;
import java.util.List;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class QueryHitMessage extends BaseMessage {

	private List<FilesharingKey> keys;

	private final static long QUERY_HIT_MESSAGE_SIZE = 18;

	// TODO Dateibeschreibung
	private OverlayContact<GnutellaOverlayID> contact;

	private long searchStringSize;

	public QueryHitMessage(GnutellaOverlayID sender,
			GnutellaOverlayID receiver, int ttl, int hops,
			BigInteger descriptor, OverlayContact<GnutellaOverlayID> contact,
			List<FilesharingKey> keys) {
		super(sender, receiver, ttl, hops, descriptor);
		this.contact = contact;
		this.keys = keys;
		searchStringSize = (long) (Simulator.getRandom().nextDouble() * 40);
	}

	public OverlayContact<GnutellaOverlayID> getContact() {
		return contact;
	}

	public List<FilesharingKey> getKeys() {
		return keys;
	}

	public long getSize() {
		// TODO low priority: Korrekte Nachrichtengröße
		return super.getSize() + QUERY_HIT_MESSAGE_SIZE + searchStringSize;
	}
}
