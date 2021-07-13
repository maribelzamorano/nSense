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
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDocument;
import de.tud.kom.p2psim.impl.application.KBRApplication.KBRDummyApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.AnnounceNewDocumentMessage;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * This operation is used to announce new documents.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class AnnounceNewDocumentOperation extends AbstractOperation {

	private final KBRDocument doc;

	private final OverlayContact ownerContact;

	private final KBR node;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param doc
	 *            the document to announce
	 * @param callback
	 */
	public AnnounceNewDocumentOperation(KBRDummyApplication app,
			KBRDocument doc, OperationCallback callback) {
		super(app, callback);

		this.doc = doc;
		this.ownerContact = app.getNode().getLocalOverlayContact();
		this.node = app.getNode();
	}

	@Override
	protected void execute() {
		getComponent().getHost().getStorage().storeDocument(doc);

		Message announceMsg = new AnnounceNewDocumentMessage(ownerContact, doc
				.getKey());
		node.route(doc.getKey(), announceMsg, null);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

}
