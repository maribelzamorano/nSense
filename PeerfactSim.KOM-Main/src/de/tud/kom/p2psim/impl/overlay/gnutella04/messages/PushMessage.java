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


package de.tud.kom.p2psim.impl.overlay.gnutella04.messages;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PushMessage extends BaseMessage {

	private static final long GNUTELLA_PUSH_MESSAGE_SIZE = 9999;

	private GnutellaOverlayID pushTarget;

	private FilesharingKey key;

	// TODO Nachrichtengröße
	private OverlayContact<GnutellaOverlayID> pushInitiator;

	public PushMessage(GnutellaOverlayID sender, GnutellaOverlayID receiver,
			int ttl, int hops, BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> pushInitiator,
			GnutellaOverlayID pushTarget, FilesharingKey key) {
		super(sender, receiver, ttl, hops, descriptor);
		this.pushInitiator = pushInitiator;
		this.pushTarget = pushTarget;
		this.key = key;
	}

	public OverlayContact<GnutellaOverlayID> getPushSender() {
		return this.pushInitiator;
	}

	public GnutellaOverlayID getPushReceiver() {
		return this.pushTarget;
	}

	public FilesharingKey getKey() {
		return this.key;
	}

	public long getSize() {
		return super.getSize() + GNUTELLA_PUSH_MESSAGE_SIZE;
	}

}
