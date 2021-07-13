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


package de.tud.kom.p2psim.impl.overlay.gnutella.api;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * A query is made by arbitrary nodes in the network in order to locate
 * matching resources.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Query {

	private IQueryInfo info;

	private int queryUID;

	/**
	 * Constructs a query with the given query information.
	 * @param info: the abstract query information.
	 */
	public Query(IQueryInfo info) {
		this.info = info;
		newUID();
	}

	public String toString() {
		return "(Query: info=" + info + " uid=" + queryUID + ")";
	}

	/**
	 * Assigns a random new UID to this query. If this operation is called,
	 * the query is recognized as a different query than before by the
	 * nodes that received it.
	 */
	public void newUID() {
		this.queryUID = Simulator.getRandom().nextInt();
	}

	/**
	 * Returns the abstract query information that is given to this query.
	 * @return
	 */
	public IQueryInfo getInfo() {
		return info;
	}

	/**
	 * Returns the integer UID of this query.
	 * @return
	 */
	public int getQueryUID() {
		return queryUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + queryUID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (queryUID != other.queryUID)
			return false;
		return true;
	}

	/**
	 * Returns the size of this query in transport representation.
	 * @return
	 */
	public int getSize() {
		return 4 + info.getSize();
	}

}
