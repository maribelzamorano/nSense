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


package de.tud.kom.p2psim.impl.application.KBRApplication.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * This message is used to query for a document.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryForDocumentMessage implements Message {

	private final OverlayKey key;

	private final OverlayContact senderContact;

	/**
	 * @param senderContact
	 *            the contact of the node that initiated the query
	 * @param key
	 *            the key to query for
	 */
	public QueryForDocumentMessage(OverlayContact senderContact, OverlayKey key) {
		this.senderContact = senderContact;
		this.key = key;
	}

	/**
	 * @return the key to query for
	 */
	public OverlayKey getKey() {
		return key;
	}

	/**
	 * @return the contact of the node that initiated the query
	 */
	public OverlayContact getSenderContact() {
		return senderContact;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		return MessageConfig.Sizes.QueryForDocumentMessage;
	}

}
