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
 * Tells the peer the new VID-neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class NewVIDNeighbourMsg extends CanMessage {

	CanOverlayContact[] newVidNeighbours;
	
	/**
	 * Tells the peer the new VID-neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param newVidNeighbours
	 * 		new vidNeighbours
	 */
	public NewVIDNeighbourMsg(CanOverlayID sender, CanOverlayID receiver,
			CanOverlayContact[] newVidNeighbours) {
		super(sender, receiver);
		this.newVidNeighbours=newVidNeighbours;
	}

	@Override
	public long getSize() {
		return CanConfig.CanOverlayContactSize*2;
	}
	
	public CanOverlayContact[] getContact(){
		return this.newVidNeighbours;
	}
}
