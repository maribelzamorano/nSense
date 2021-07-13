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


package de.tud.kom.p2psim.impl.application.KBRApplication;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.AnnounceNewDocumentMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.QueryForDocumentMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.QueryResultMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.RequestDocumentMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.messages.TransferDocumentMessage;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.AnnounceNewDocumentOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.QueryForDocumentOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.QueryResultOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.RequestDocumentOperation;
import de.tud.kom.p2psim.impl.application.KBRApplication.operations.TransferDocumentOperation;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is a dummy application to check some basic functionality of KBR
 * overlays.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRDummyApplication extends AbstractApplication implements
		KBRListener {

	private final static Logger log = SimLogger
			.getLogger(KBRDummyApplication.class);

	private final KBR node;

	private final HashMap<OverlayKey, OverlayContact> localDB = new HashMap<OverlayKey, OverlayContact>();

	private final OverlayID localOverlayID;

	private static RandomGenerator rGen = Simulator.getRandom();

	/**
	 * @param node
	 */
	public KBRDummyApplication(KBR node) {
		this.node = node;
		localOverlayID = node.getLocalOverlayContact().getOverlayID();

		// Set the application as KBRListener
		node.setKBRListener(this);
	}

	@Override
	public void deliver(OverlayKey key, Message msg) {
		if (key == null) {
			// It is a direct message to this node

			if (msg instanceof QueryResultMessage) {
				QueryResultMessage queryResMsg = (QueryResultMessage) msg;
				log.debug(localOverlayID + " - Received query result for rank "
						+ queryResMsg.getKey() + " from "
						+ queryResMsg.getSenderContact().getOverlayID());

				if (queryResMsg.getDocumentProvider() != null) {
					log.debug(localOverlayID + " - Got a query result for key "
							+ queryResMsg.getKey() + ": File provider is "
							+ queryResMsg.getDocumentProvider().getOverlayID());

					RequestDocumentOperation operation = new RequestDocumentOperation(
							this, queryResMsg.getKey(), queryResMsg
									.getDocumentProvider(),
							Operations.EMPTY_CALLBACK);

					operation.scheduleImmediately();

				} else {
					log.debug(localOverlayID + " - Got a query result for key "
							+ queryResMsg.getKey() + ": The root of the key ("
							+ queryResMsg.getSenderContact().getOverlayID()
							+ ") does not know about such a document.");
				}

			} else if (msg instanceof RequestDocumentMessage) {
				RequestDocumentMessage requestDocumentMsg = (RequestDocumentMessage) msg;
				log.debug(localOverlayID + " - Received request for document: "
						+ requestDocumentMsg.getKeyOfDocument());

				TransferDocumentOperation operation = new TransferDocumentOperation(
						this, requestDocumentMsg.getKeyOfDocument(),
						requestDocumentMsg.getSenderContact(),
						Operations.EMPTY_CALLBACK);

				operation.scheduleImmediately();
			} else if (msg instanceof TransferDocumentMessage) {

				TransferDocumentMessage transferDocumentMsg = (TransferDocumentMessage) msg;

				log.debug(localOverlayID + " - Received requested document: "
						+ transferDocumentMsg.getKeyOfDocument());
			}

		} else if (node.isRootOf(key)) {
			// It is a message delivered here because this is the root of the
			// key

			if (msg instanceof AnnounceNewDocumentMessage) {
				// As this message was delivered to this node, it must be the
				// root of the key.
				// So we store the information about the key and the node that
				// holds the key.

				AnnounceNewDocumentMessage announceMsg = (AnnounceNewDocumentMessage) msg;

				// Store key of file and sender of message in local database
				localDB.put(announceMsg.getDocumentKey(), announceMsg
						.getSenderContact());

				log.debug(localOverlayID
						+ " - Stored information about new file (Key: "
						+ announceMsg.getDocumentKey() + ")");
			} else if (msg instanceof QueryForDocumentMessage) {
				// As this message was delivered to this node, it must be the
				// root of the

				QueryForDocumentMessage queryMessage = (QueryForDocumentMessage) msg;
				log.debug(localOverlayID
						+ " - Received query message for rank:"
						+ queryMessage.getKey());

				OverlayContact documentProvider = lookupDocumentProviderInLocalDB(queryMessage
						.getKey());

				OverlayContact receiver = queryMessage.getSenderContact();
				QueryResultOperation operation = new QueryResultOperation(this,
						queryMessage.getKey(), documentProvider, receiver,
						Operations.EMPTY_CALLBACK);

				operation.scheduleImmediately();
			}
		} else {
			log.error(localOverlayID
					+ " - I received a message that is not for me!");
		}

	}

	@Override
	public void forward(KBRForwardInformation information) {
		// Nothing to do here
	}

	@Override
	public void update(OverlayContact contact, boolean joined) {
		if (joined)
			log.debug(contact.getOverlayID() + " - joined");
		else
			log.debug(contact.getOverlayID() + " - left");
	}

	/**
	 * @return the KBR node on which the application is running
	 */
	public KBR getNode() {
		return node;
	}

	/**
	 * Stores an new document and announces the existence in the overlay.
	 * Documents have no names but ranks that identifies them.
	 * 
	 * @param rank
	 *            the rank oft the new document
	 * @param size
	 *            the size in bytes of the new document
	 */
	public void storeNewFile(int rank, int size) {
		// Create the document
		OverlayKey key = node.getNewOverlayKey(rank);
		storeNewFile(key, size);
	}

	private void storeNewFile(OverlayKey key, int size) {
		log.debug(localOverlayID + " - Store new file initiated (rank:" + key
				+ " size:" + size + ")");
		KBRDocument doc = new KBRDocument(key);
		doc.setSize(size);

		AnnounceNewDocumentOperation operation = new AnnounceNewDocumentOperation(
				this, doc, Operations.EMPTY_CALLBACK);
		operation.scheduleImmediately();
	}

	public void storeRandomNewFile() {

		OverlayKey ranKey = getNode().getRandomOverlayKey();
		int ranSize = rGen.nextInt(200);

		storeNewFile(ranKey, ranSize);
	}

	/**
	 * Queries for document and transfers it to this host.
	 * 
	 * @param rank
	 *            the rank that identifies the document
	 */
	public void getDocument(int rank) {
		OverlayKey key = node.getNewOverlayKey(rank);
		log.debug(localOverlayID
				+ " - Start procedure to get the document with rank " + rank
				+ " (key: " + key + ")");

		getDocument(key);
	}

	private void getDocument(OverlayKey key) {
		QueryForDocumentOperation operation = new QueryForDocumentOperation(
				this, key, Operations.EMPTY_CALLBACK);
		operation.scheduleImmediately();
	}

	public void getRandomDocument() {
		OverlayKey ranKey = getNode().getRandomOverlayKey();

		getDocument(ranKey);
	}

	private OverlayContact lookupDocumentProviderInLocalDB(OverlayKey key) {
		Set<Entry<OverlayKey, OverlayContact>> dbAsSet = localDB.entrySet();

		// return localDB.get(key);

		for (Entry<OverlayKey, OverlayContact> entry : dbAsSet) {
			if (entry.getKey().compareTo(key) == 0) {
				return entry.getValue();
			}
		}
		return null;
	}

}
