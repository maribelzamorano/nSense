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


package de.tud.kom.p2psim.impl.application.KBRApplication.operations;

import java.util.Collection;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.storage.Document;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDummyApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.TransferDocumentMessage;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * This operation transfers a document direct to a contact
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TransferDocumentOperation extends AbstractOperation {

	KBRDummyApplication app;

	OverlayKey keyOfDocument;

	OverlayContact receiverContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param keyOfDocument
	 *            the key of the document to be sent
	 * @param receiverContact
	 *            the contact of the receiver of the message
	 * @param callback
	 */
	public TransferDocumentOperation(KBRDummyApplication app,
			OverlayKey keyOfDocument, OverlayContact receiverContact,
			OperationCallback callback) {
		super(app, callback);

		this.app = app;
		this.keyOfDocument = keyOfDocument;
		this.receiverContact = receiverContact;
	}

	@Override
	protected void execute() {
		OverlayContact senderContact = app.getNode().getLocalOverlayContact();

		// Load document from local storage
		Document doc = getDocumnetFromStorage(keyOfDocument);

		// Send file direct to the receiver without routing for the key
		TransferDocumentMessage msg = new TransferDocumentMessage(
				keyOfDocument, senderContact, doc);
		app.getNode().route(null, msg, receiverContact);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

	private Document getDocumnetFromStorage(OverlayKey key) {
		Collection<OverlayKey> keys = app.getHost().getStorage()
				.listDocumentKeys();

		for (OverlayKey currentKey : keys) {
			if (currentKey.compareTo(key) == 0) {
				return app.getHost().getStorage().loadDocument(currentKey);
			}
		}
		return null;
	}

}
