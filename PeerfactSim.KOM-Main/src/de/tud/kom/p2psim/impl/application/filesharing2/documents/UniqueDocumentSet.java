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

import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Documents of this set are only allowed to be published by one host in the
 * entire overlay network. Furthermore, they are looked up equally-distributed.
 * 
 * Thought for modeling a scenario where every peer publishes e.g. its contact
 * data, like it can be used by a VoIP or Instant Messaging service.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UniqueDocumentSet implements IDocumentSet {

	String name;

	RandomGenerator rand = Simulator.getRandom();

	private int startRank;

	private int size;

	private int nextDoc;

	/**
	 * Sets the size of this document set to the given value.
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

	/**
	 * Returns a key that can be used for publishing.
	 */
	public int getKeyForPublish() {
		if (nextDoc >= startRank + size)
			throw new ConfigurationException( name + 
					": Tried to publish more unique documents than "
							+ "are defined. Increase the document size of this set or decrease publishes.");
		int result = nextDoc;
		nextDoc++;
		return result;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the document set to the specified value.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		System.out.println("Using resource set \"" + name + "\"");
	}

	@Override
	public void setBeginRank(int rank) {
		this.startRank = rank;
		this.nextDoc = rank;
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
