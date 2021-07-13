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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.application.filesharing2.FSEvents;
import de.tud.kom.p2psim.impl.application.filesharing2.FilesharingApplication;

/**
 * An overlay handler implements the common overlay filesharing operations like 
 * defined in IOverlayHandler. It uses a specific overlay implementation to fulfill
 * these tasks. The overlay handlers decouple the filesharing application and the overlay
 * implementations.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public abstract class AbstractOverlayHandler implements IOverlayHandler {

	private FilesharingApplication app;

	@Override
	public void setFSApplication(FilesharingApplication app) {
		this.app = app;
	}

	/**
	 * Returns the filesharing application that calls the operations
	 * on IOverlayHandler
	 * @return
	 */
	protected FilesharingApplication getFSApplication() {
		return app;
	}

	private FSEvents ev = FSEvents.getInstance();

	/**
	 * Called when a lookup was started.
	 * @param initiator : the initiator of the lookup
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	protected void lookupStarted(OverlayContact initiator, Object queryUID) {
		ev.lookupStarted(initiator, queryUID);
	}

	/**
	 * Called when a lookup made a hop on a specific node.
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 * @param hop : the contact where the query hopped.
	 */
	protected void lookupMadeHop(Object queryUID, OverlayContact hop) {
		ev.lookupMadeHop(queryUID, hop);
	}

	/**
	 * Called when a lookup previously started succeeded.
	 * @param initiator : the initiator of the lookup
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 * @param hops : the number of hops that were done to make the lookup
	 */
	protected void lookupSucceeded(OverlayContact initiator, Object queryUID,
			int hops) {
		ev.lookupSucceeded(initiator, queryUID, hops);
	}

	/**
	 * Called when a publish was started.
	 * @param initiator : the initiator of the publish
	 * @param keyToPublish : the key that has to be published
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	protected void publishStarted(OverlayContact initiator, int keyToPublish,
			Object queryUID) {
		ev.publishStarted(initiator, keyToPublish, queryUID);
	}

	/**
	 * Called when a publish succeeded.
	 * @param initiator : the initiator of the publish
	 * @param holder : the holder of the resource that has been published.
	 * @param keyPublished : the key that has been published
	 * @param queryUID : a query UID that is consistent with the equals() method.
	 */
	protected void publishSucceeded(OverlayContact initiator,
			OverlayContact holder, int keyPublished, Object queryUID) {
		ev.publishSucceeded(initiator, holder, keyPublished, queryUID);
	}

}
