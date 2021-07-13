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
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.QueryResultMessage;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * This operation sends a QueryResultMessage direct to a contact
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryResultOperation extends AbstractOperation {

	private final KBRDummyApplication app;

	private final OverlayKey key;

	private final OverlayContact senderContact;

	private final OverlayContact documentProvider;

	private final OverlayContact receiverContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param key
	 *            the key of the documents that was queried
	 * @param documentProvider
	 *            the provider of the document
	 * @param receiverContact
	 *            the contact of the receiver of the message
	 * @param callback
	 */
	public QueryResultOperation(KBRDummyApplication app, OverlayKey key,
			OverlayContact documentProvider, OverlayContact receiverContact,
			OperationCallback callback) {
		super(app, callback);

		this.key = key;
		this.app = app;
		this.senderContact = app.getNode().getLocalOverlayContact();
		this.documentProvider = documentProvider;
		this.receiverContact = receiverContact;
	}

	@Override
	protected void execute() {

		Message msg = new QueryResultMessage(senderContact, key,
				documentProvider);

		// Route the message direct to the receiver (direct means the key is
		// null and
		// the receiver is given as hint)
		app.getNode().route(null, msg, receiverContact);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

}
