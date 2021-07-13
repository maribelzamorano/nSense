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


package de.tud.kom.p2psim.api.overlay.gnutella;

import java.util.Set;

import de.tud.kom.p2psim.api.overlay.JoinLeaveOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IQueryInfo;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IResource;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.GnutellaEvents;

/**
 * 
 * API of Gnutella peers for application layers
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface GnutellaAPI extends JoinLeaveOverlayNode {

	public abstract GnutellaLikeOverlayContact getOwnContact();

	/**
	 * Publishes the given ranks that are given in a comma-separated string representation of integers, e.g.:
	 * "1,2,3,4,5"
	 */
	public abstract void publishRanks(String docs);

	/**
	 * Publishes the documents given in the set.
	 */
	public abstract void publishSet(Set<IResource> res);

	/**
	 * Queries a document, represented by a rank.
	 */
	public abstract void queryRank(int rank, int hitsWanted);

	/**
	 * Queries a document given query information (for "advanced" users and custom query semantics)
	 */
	public abstract void query(IQueryInfo info, int hitsWanted);

	/**
	 * Returns the set of resources this node has currently published.
	 */
	public abstract Set<IResource> getResources();



	/**
	 * Returns the local event dispatcher
	 */
	public abstract GnutellaEvents getLocalEventDispatcher();

}
