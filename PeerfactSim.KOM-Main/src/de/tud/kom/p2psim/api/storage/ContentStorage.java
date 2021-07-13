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



package de.tud.kom.p2psim.api.storage;

import java.util.Collection;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * 
 * Represents a storage of documents inside of a host, which can be used by
 * applications and overlays to store documents locally and fetch them again
 * later.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @version 1.0, 11/25/2007
 * 
 */
public interface ContentStorage extends Component {
	/**
	 * Fetch doc from the local storage.
	 * 
	 * @param key
	 *            - document's key
	 * @return the requested document if present, null otherwise
	 */
	public Document loadDocument(OverlayKey key);

	/**
	 * Store the document in the local storage.
	 * 
	 * @param doc
	 *            - document to stores
	 */
	public void storeDocument(Document doc);

	/**
	 * List the keys of all locally stored documents
	 * 
	 * @return documents stored locally.
	 */
	public Collection<OverlayKey> listDocumentKeys();

	/**
	 * The way to find out whether a document with the given key is inside of
	 * this storage.
	 * 
	 * @param key
	 *            - document key
	 * @return whether the document with the <code>key</code> is inside
	 */
	public boolean containsDocument(OverlayKey key);

	/**
	 * 
	 * @return all documents stored inside of this storage
	 */
	public Collection<Document> listDocuments();
}
