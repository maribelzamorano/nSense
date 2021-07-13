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

import org.apache.commons.math.random.RandomGenerator;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * In this set of documents, all documents have the same probability to be looked up and stored.
 * An example for the declaration in the XML configuration file: 
 * <pre>
 * &lt;ResourceSet class="de.tud.kom.p2psim.impl.application.filesharing2.documents.FlatDocumentSet" name="files1" size="150"/&gt;
 * </pre>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class FlatDocumentSet implements IDocumentSet {

	String name;

	// private final static Logger log =
	// SimLogger.getLogger(FlatDocumentSet.class);

	RandomGenerator rand = Simulator.getRandom();

	private int startRank;

	private int size;

	/**
	 * Sets the size of this document set to the given value
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getKeyForLookup() {

		return startRank + rand.nextInt(size);

	}

	@Override
	public int getKeyForPublish() {
		return startRank + rand.nextInt(size);
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the string name of this document set
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		System.out.println("Using resource set \"" + name + "\"");
	}

	@Override
	public void setBeginRank(int rank) {
		this.startRank = rank;
	}

	@Override
	public boolean containsResourcesOf(Iterable<Integer> set) {
		for (Integer key : set) {
			if (key >= startRank && key < startRank + size)
				return true;
		}
		return false;
	}

}
