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

/**
 * 
 * Answer message for overloaded mode. 
 * Tells the new node the main node of the overloaded area.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinReplyMsg extends CanMessage {

	private CanOverlayContact mainNode;
	
	/**
	 * Answer message for overloaded mode. 
	 * Tells the new node the main node of the overloaded area.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 * 		contact data of the overload main node
	 */
	public JoinReplyMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact c) {
		super(sender, receiver);
		this.mainNode=c;
	}

	@Override
	public long getSize() {
		return CanConfig.CanOverlayContactSize;
	}

	public CanOverlayContact getMainNode() {
		return mainNode;
	}

}
