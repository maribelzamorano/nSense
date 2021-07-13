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


package de.tud.kom.p2psim.impl.overlay.dht.can.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;

/**
 * 
 * Abstract Method for Messages. 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public abstract class CanMessage extends AbstractOverlayMessage<CanOverlayID> {
	private int hopCount = 0;

	/**
	 * creates a new AbstractOverlayMessage
	 * 
	 * @param sender
	 * @param receiver
	 */
	public CanMessage(CanOverlayID sender, CanOverlayID receiver) {
		super(sender, receiver);
	}

	public Message getPayload() {
		return this;
	}

	public abstract long getSize();

	public int getHopCount() {
		return this.hopCount;
	}

	public void setHop(int hop) {
		this.hopCount = hop;
	}
	
}
