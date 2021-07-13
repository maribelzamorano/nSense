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



package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * This class represents the local index database of an OverlayNode to store
 * DHTObjects. It provides a put and get functionality. Data items have a
 * constant expiration time. If that time has elapsed, these items are evicted
 * from the database. (The expiration time is updated if the item is reinserted
 * into the database.)
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Kaune
 * @version 05/06/2011
 */
public final class KademliaIndexer<T extends KademliaOverlayID> {

	/**
	 * A Map with KademliaOverlayKeys of data items as keys and
	 * KademliaIndexEntries that contain the data items as values.
	 */
	private final Map<KademliaOverlayKey, KademliaIndexEntry> index;

	/**
	 * Configuration values ("constants").
	 */
	protected final ComponentsConfig config;

	/**
	 * Constructs a new local index.
	 * 
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaIndexer(final ComponentsConfig conf) {
		this.config = conf;
		/*
		 * The approximate expected size of the database, assuming an equal
		 * distribution of data items among peers and a peer being responsible
		 * for the items of its K neighbours as well. Multiplied with 2 to
		 * account for the fact that it is unlikely that all peers are online at
		 * the same time.
		 */
		final double expectedIndexSize = 2 * config.getNumberOfDataItems()
				/ (double) config.getNumberOfPeers() * config.getBucketSize();
		this.index = new HashMap<KademliaOverlayKey, KademliaIndexEntry>(
				(int) (expectedIndexSize * 1.2), 0.95f);
	}

	/**
	 * Puts a DHTObject into the index database. Internally, the expiration
	 * timeout of the data item is set to be
	 * {@link KademliaConfig#DATA_EXPIRATION_TIME} from the current simulation
	 * time ("now").
	 * <p>
	 * Adding an item does <i>not</i> overwrite an existing value if it is
	 * stored under the same key. It is furthermore assumed that this method is
	 * called each time a data item is republished, even if the initiator of
	 * that republish is the local node.
	 * 
	 * @param key
	 *            the KademliaOverlayKey belonging to the DHTObject.
	 * @param newValue
	 *            the DHTObject that represents the data item to be inserted.
	 */
	public final void put(final KademliaOverlayKey key, final DHTObject newValue) {
		KademliaIndexEntry indexEntry;
		indexEntry = index.get(key);
		if (indexEntry == null) {
			indexEntry = new KademliaIndexEntry(newValue, config);
			index.put(key, indexEntry);
		}
		// set last republish time
		indexEntry.updateLastRepublish();
	}

	/**
	 * Puts the initial data items into the database. These are data items that
	 * have not yet "officially" been published. (This method is called by
	 * scenario setup components.) Consequently, the last republish time is
	 * <i>not</i> set. Besides that exception, this method behaves as
	 * {@link #put(KademliaOverlayKey, DHTObject)}.
	 * 
	 * @param key
	 *            the KademliaOverlayKey belonging to the DHTObject.
	 * @param newValue
	 *            the DHTObject that represents the data item to be inserted.
	 */
	public final void putInitialDataItem(final KademliaOverlayKey key,
			final DHTObject newValue) {
		final KademliaIndexEntry indexEntry;
		indexEntry = new KademliaIndexEntry(newValue, config);
		index.put(key, indexEntry);
		// do not call updateLastRepublish() here...
	}

	/**
	 * Looks up the DHTObject associated with <code>key</code> in the local
	 * database.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of the data item to be looked up.
	 * @return the DHTObject associated with <code>key</code>. If no such object
	 *         exists (or it has expired), <code>null</code> is returned.
	 */
	public final DHTObject get(final KademliaOverlayKey key) {
		final KademliaIndexEntry indexEntry = index.get(key);
		if (indexEntry == null) { // no entry known
			return null;
		} else if (indexEntry.hasExpired()) { // old entry expired
			index.remove(key);
			return null;
		}
		return indexEntry.getValue();
	}

	/**
	 * @return a Map that contains all DHTObjects from this local index mapped
	 *         by their KademliaOverlayKey.
	 */
	public final Map<KademliaOverlayKey, DHTObject> getEntries() {
		refreshIndex();
		final Map<KademliaOverlayKey, DHTObject> result = new HashMap<KademliaOverlayKey, DHTObject>(
				index.size(), 1.0f);

		for (final Map.Entry<KademliaOverlayKey, KademliaIndexEntry> entry : index
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getValue());
		}
		return result;
	}

	/**
	 * @return a Map that contains the DHTObjects from this local index that
	 *         need to be republished because the last republish is more than
	 *         one hour ago mapped by their KademliaOverlayKey.
	 */
	public final Map<KademliaOverlayKey, DHTObject> getEntriesToRepublish() {
		refreshIndex();
		/*
		 * Initial capacity: currently, it is likely that all entries need to be
		 * republished when this function is called.
		 */
		final Map<KademliaOverlayKey, DHTObject> result = new HashMap<KademliaOverlayKey, DHTObject>(
				index.size(), 1.0f);

		for (final Map.Entry<KademliaOverlayKey, KademliaIndexEntry> entry : index
				.entrySet()) {
			if (entry.getValue().needsRepublish()) {
				result.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		return result;
	}

	/**
	 * Refreshes the local index by sorting out entries that have expired.
	 */
	private final void refreshIndex() {
		final List<KademliaOverlayKey> toRemove = new ArrayList<KademliaOverlayKey>();

		for (final Map.Entry<KademliaOverlayKey, KademliaIndexEntry> entry : index
				.entrySet()) {
			if (entry.getValue().hasExpired()) {
				toRemove.add(entry.getKey());
			}
		}
		index.keySet().removeAll(toRemove);
	}
}
