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


/**
 * Behaves like a set of documents where keys can be drawn for lookup
 * and publishing.
 * 
 * @author Leo Nobach  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public interface IDocumentSet {

	/**
	 * Returns the number of keys this set consists of.
	 * @return
	 */
	public abstract int getSize();

	/**
	 * Returns a key that can be looked up by the application layer.
	 * @return
	 */
	public abstract int getKeyForLookup();

	/**
	 * Returns the string name of the document set.
	 * @return
	 */
	public abstract String getName();

	/**
	 * The ranks of different document sets must not overlap. Sets
	 * the first rank of this document set. Further ranks of this set are numbers
	 * following this one.
	 * @param rank
	 */
	public abstract void setBeginRank(int rank);

	/**
	 * Returns a key that can be published by the application layer.
	 * @return
	 */
	public abstract int getKeyForPublish();
	
	/**
	 * Returns whether this set contains at least one resource of the given set.
	 * @param set
	 * @return
	 */
	public abstract boolean containsResourcesOf(Iterable<Integer> set);

}
