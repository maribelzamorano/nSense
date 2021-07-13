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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * Sent to the node that requested a publish in order to confirm it as
 * successful.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PublishSucceededMessage extends FilesharingMessage {

	long queryUID;

	OverlayKey keyPublished;

	private OverlayContact requestor;

	private OverlayContact publisher;

	/**
	 * Default Constructor
	 * 
	 * @param queryUID
	 *            : the UID that uniquely identifies the publish being made.
	 * @param keyPublished
	 *            : the key of the resource that was stored in the overlay
	 * @param requestor
	 *            : the requestor, i.e. the node that receives this message.
	 * @param publisher
	 *            : the responsible node for the publish, i.e. the node that
	 *            sends this message.
	 */
	public PublishSucceededMessage(long queryUID, OverlayKey keyPublished,
			OverlayContact requestor, OverlayContact publisher) {
		super();
		this.queryUID = queryUID;
		this.keyPublished = keyPublished;
		this.requestor = requestor;
		this.publisher = publisher;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return 12;
	}

	/**
	 * Returns the UID that uniquely identifies the publish being made.
	 * 
	 * @return
	 */
	public long getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the key of the resource that was stored in the overlay
	 * 
	 * @return
	 */
	public OverlayKey getKeyPublished() {
		return keyPublished;
	}

	/**
	 * Returns the requestor, i.e. the node that receives this message.
	 * 
	 * @return
	 */
	public OverlayContact getRequestor() {
		return requestor;
	}

	/**
	 * Returns the responsible node for the publish, i.e. the node that sends
	 * this message.
	 * 
	 * @return
	 */
	public OverlayContact getPublishResponsibleNode() {
		return publisher;
	}

	public String toString() {
		return "(Published: " + keyPublished + ")";
	}

}
