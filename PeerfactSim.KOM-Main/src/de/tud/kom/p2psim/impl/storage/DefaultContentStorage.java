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



package de.tud.kom.p2psim.impl.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.storage.ContentStorage;
import de.tud.kom.p2psim.api.storage.Document;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Default implementation of a content storage.
 * 
 * @author pussep <peerfact@kom.tu-darmstadt.de>
 * @version 0.1, 28.11.2007
 * @see ContentStorage
 */
public class DefaultContentStorage implements ContentStorage {
	private static final Logger log = SimLogger
			.getLogger(DefaultContentStorage.class);

	Map<OverlayKey, Document> documents = new LinkedHashMap<OverlayKey, Document>();

	private Host host;

	public DefaultContentStorage() {
		// nothing to do
	}

	public Collection<Document> listDocuments() {
		return new LinkedHashSet<Document>(documents.values());
	}

	public void storeDocument(Document doc) {
		OverlayKey key = doc.getKey();
		documents.put(key, doc);
		log.debug("store " + doc + " at " + this);
	}

	public Document loadDocument(OverlayKey key) {
		return documents.get(key);
	}

	public Collection<OverlayKey> listDocumentKeys() {
		// return new ArrayList<OverlayKey>(documents.keySet());
		return Collections.unmodifiableSet(documents.keySet());
	}

	public boolean containsDocument(OverlayKey key) {
		return documents.containsKey(key);
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

}
