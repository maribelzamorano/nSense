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



package de.tud.kom.p2psim.api.overlay;

import java.util.List;

/**
 * Interface for a general overlay routing table.
 * 
 * @param <T>
 *            Type of the used overlay IDs, overlay specific
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface OverlayRoutingTable<T extends OverlayID, S extends OverlayContact<T>> {

	/**
	 * Inserts a new OverlayContact into the routing table
	 * 
	 * @param contact
	 *            the new contact
	 */
	public void addContact(S contact);

	/**
	 * Removes an contact in the routing table
	 * 
	 * @param oid
	 *            the OverlayID of the contact to be removed
	 */
	public void removeContact(T oid);

	/**
	 * Returns the OverlayContact belonging to its OverlayID
	 * 
	 * @param oid
	 *            the particular OverlayID
	 * @return the OverlayContact belonging to its OverlayID
	 */
	public S getContact(T oid);

	/**
	 * Delete all the connections of the specified direction.
	 */
	public void clearContacts();

	/**
	 * @return an immutable list of all known contacts.
	 */
	public List<S> allContacts();
}
