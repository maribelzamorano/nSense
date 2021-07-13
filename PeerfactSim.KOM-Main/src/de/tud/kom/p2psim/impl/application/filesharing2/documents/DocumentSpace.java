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


package de.tud.kom.p2psim.impl.application.filesharing2.documents;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.util.MultiSet;

/**
 * 
 * Container and manager of all document sets defined in the XML config file.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DocumentSpace {

	private static DocumentSpace inst;

	int resourceOffset = 0;

	MultiSet<Integer> publishedRanks = new MultiSet<Integer>();

	public Map<String, IDocumentSet> resources = new HashMap<String, IDocumentSet>();

	public Map<IDocumentSet, Boolean> resourcesOfSetPublished = new HashMap<IDocumentSet, Boolean>();

	/**
	 * Returns the document set with the given string name. The name is assigned to every document set
	 * in the XML config file.
	 * @param name : the string name of the given document set
	 * @return the document set with the given name.
	 */
	public IDocumentSet getResourceSet(String name) {
		IDocumentSet set = resources.get(name);
		if (set == null)
			throw new ConfigurationException("Document set " + name
					+ " is not declared in the config.");
		return set;
	}

	/**
	 * Returns all document sets that were declared in the XML config file.
	 * @return : an unmodifiable collection of all document sets declared.
	 */
	public Collection<IDocumentSet> getAllSets() {
		return Collections.unmodifiableCollection(resources.values());
	}

	private DocumentSpace() {
		// Singleton
	}

	/**
	 * Deprecated, since ranks are now always used.
	 * @param useRanks
	 */
	@Deprecated
	public void setUseRanks(boolean useRanks) {
		//Ranks are now always used, nothing to do
	}

	public static DocumentSpace getInstance() {
		if (inst == null)
			inst = new DocumentSpace();
		return inst;
	}

	/**
	 * Adds a document set to the document space.
	 * @param set
	 */
	public void setResourceSet(IDocumentSet set) {
		set.setBeginRank(resourceOffset);
		resources.put(set.getName(), set);
		resourcesOfSetPublished.put(set, false);
		resourceOffset += set.getSize();
	}

	/**
	 * Returns docAmount arbitrary keys that shall be published from the given document set.
	 * @param documentSet : the document set to draw keys from
	 * @param docAmount : the amount of keys to draw from this document set.
	 * @return
	 */
	public Set<Integer> getSomeKeysForPublish(IDocumentSet documentSet,
			int docAmount) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < docAmount; i++)
			result.add(documentSet.getKeyForPublish());
		return result;
	}

	void recalculateIfResourcesOfSetPublished() {
		for (Entry<IDocumentSet, Boolean> e : resourcesOfSetPublished
				.entrySet()) {
			e.setValue(e.getKey().containsResourcesOf(
					publishedRanks.getUnmodifiableMap().keySet()));
		}
	}

	/**
	 * Returns a key from the document set that shall be looked up.
	 * @param documentSet : the document set to draw the key from.
	 * @return
	 */
	public int getKeyForLookup(IDocumentSet documentSet) {
		if (resourcesOfSetPublished.get(documentSet) == false)
			throw new IllegalStateException("No key of document set "
					+ documentSet + "was ever published.");
		int key;

		do {
			key = documentSet.getKeyForLookup();
		} while (!publishedRanks.containsOccurrence(key));

		return key;
	}

	/**
	 * Activates the the given published keys that were deactivated before, e.g. following an
	 * online event.
	 * @param myKeys
	 */
	public void activateMyPublishedKeys(Set<Integer> myKeys) {
		publishedRanks.addOccurrences(myKeys);
		recalculateIfResourcesOfSetPublished();
	}

	/**
	 * Deactivates the given published keys, e.g. following an offline event.
	 * @param myKeys
	 */
	public void deactivateMyPublishedKeys(Set<Integer> myKeys) {
		publishedRanks.removeOccurrences(myKeys);
		recalculateIfResourcesOfSetPublished();
	}

}
