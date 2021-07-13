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

import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanVID;

/**
 * 
 * Responsible peer starts this message an collect all data from 
 * peers with common parents. This message is send from VID neighbour
 * to VID neighbour until all peers with common parents are found.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverReorganizeMsg extends CanMessage {
	private CanOverlayContact senderNode;
	private CanOverlayContact missing;
	
	/**
	 * Responsible peer starts this message an collect all data from 
	 * peers with common parents. This message is send from VID neighbour
	 * to VID neighbour until all peers with common parents are found.
	 * 
	 * @param sender
	 * @param receiver
	 * @param senderNode
	 * 		responsible peer
	 * @param missing
	 * 		lost peer
	 */
	public TakeoverReorganizeMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact senderNode, 
			CanOverlayContact missing) {
		super(sender, receiver);
		this.senderNode=senderNode;
		this.missing=missing;

	}

	public CanOverlayContact getSenderNode() {
		return senderNode;
	}

	public CanOverlayContact getMissing() {
		return missing;
	}
	
	public CanVID getVid(){
		return missing.getArea().getVid();
	}

	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize*2);
	}

}
