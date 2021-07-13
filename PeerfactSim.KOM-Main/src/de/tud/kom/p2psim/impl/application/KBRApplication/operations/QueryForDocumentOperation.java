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
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.QueryForDocumentMessage;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * This operation is used to query for documents.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class QueryForDocumentOperation extends AbstractOperation {

	private final OverlayKey key;

	private final KBRDummyApplication app;

	private final OverlayContact senderContact;

	/**
	 * @param app
	 *            the application that created the operation
	 * @param key
	 *            the key of the document to query for
	 * @param callback
	 */
	public QueryForDocumentOperation(KBRDummyApplication app, OverlayKey key,
			OperationCallback callback) {
		super(app, callback);

		this.key = key;
		this.app = app;
		this.senderContact = app.getNode().getLocalOverlayContact();
	}

	@Override
	protected void execute() {

		Message msg = new QueryForDocumentMessage(senderContact, key);
		app.getNode().route(key, msg, null);

		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// There is no meaningful result to return
		return null;
	}

	public OverlayKey getKeyQueriedFor() {
		return key;
	}

}
