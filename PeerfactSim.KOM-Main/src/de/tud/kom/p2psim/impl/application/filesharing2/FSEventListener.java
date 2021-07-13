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


package de.tud.kom.p2psim.impl.application.filesharing2;

import de.tud.kom.p2psim.api.overlay.OverlayContact;

/**
 * Listener for important Filesharing2-specific events.
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public interface FSEventListener {

	/**
	 * A lookup has been started by the filesharing application.
	 * @param initiator : the initiator of the lookup
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	public void lookupStarted(OverlayContact initiator, Object queryUID);

	/**
	 * A lookup started by the filesharing application has succeeded.
	 * @param initiator : the initiator of the lookup
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	public void lookupSucceeded(OverlayContact initiator, Object queryUID,
			int hops);

	/**
	 * A publish has been started by the filesharing application.
	 * @param initiator : the initiator of the publish
	 * @param keyToPublish : the key that has to be published
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	public void publishStarted(OverlayContact initiator, int keyToPublish,
			Object queryUID);

	/**
	 * A publish started by the filesharing application has succeeded.
	 * @param initiator : the initiator of the publish
	 * @param holder : the holder of the resource that has been published.
	 * @param keyPublished : the key that has been published
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	public void publishSucceeded(OverlayContact initiator,
			OverlayContact holder, int keyPublished, Object queryUID);

	/**
	 * A lookup has made a hop at a contact.
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 * @param hop : the contact where the query hopped.
	 */
	public void lookupMadeHop(Object queryUID, OverlayContact hop);

}
