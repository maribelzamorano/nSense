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
import de.tud.kom.p2psim.api.storage.Document;

/**
 * This message is used to send a document.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TransferDocumentMessage implements Message {

	private final OverlayContact senderContact;

	private final OverlayKey keyOfDocument;

	private final Document doc;

	/**
	 * @param keyOfDocument
	 *            the key of the document transfered within this message
	 * @param senderContact
	 *            the contact oft the node that sends the document
	 * @param doc
	 *            the document that is sent
	 */
	public TransferDocumentMessage(OverlayKey keyOfDocument,
			OverlayContact senderContact, Document doc) {

		this.senderContact = senderContact;
		this.keyOfDocument = keyOfDocument;
		this.doc = doc;
	}

	@Override
	public Message getPayload() {
		// There is no meaningful payload to return
		return null;
	}

	@Override
	public long getSize() {
		return doc.getSize() + MessageConfig.Sizes.TransferDocumentMessage;
	}

	/**
	 * @return the contact oft the node that sends the document
	 */
	public OverlayContact getSenderContact() {
		return senderContact;
	}

	/**
	 * @return the key of the document transfered within this message
	 */
	public OverlayKey getKeyOfDocument() {
		return keyOfDocument;
	}

}
