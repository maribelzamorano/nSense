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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDummyApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.RequestDocumentMessage;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * This operation requests a document to be transfered. Therefore the document
 * provider was already determined by a query for the key of the file.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RequestDocumentOperation extends AbstractOperation {

	KBRDummyApplication app;

	OverlayKey keyOfDocument;

	OverlayContact receiverContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param keyOfFile
	 *            the key of the document to request
	 * @param receiverContact
	 *            the contact of the receiver of the message
	 * @param callback
	 */
	public RequestDocumentOperation(KBRDummyApplication app,
			OverlayKey keyOfFile, OverlayContact receiverContact,
			OperationCallback callback) {
		super(app, callback);

		this.app = app;
		this.keyOfDocument = keyOfFile;
		this.receiverContact = receiverContact;
	}

	@Override
	protected void execute() {
		// Send a direct message to the file provider

		OverlayContact senderContact = app.getNode().getLocalOverlayContact();
		Message requestFileMessage = new RequestDocumentMessage(keyOfDocument,
				senderContact);
		app.getNode().route(null, requestFileMessage, receiverContact);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

	public OverlayKey getKeyOfDocument() {
		return keyOfDocument;
	}

}
