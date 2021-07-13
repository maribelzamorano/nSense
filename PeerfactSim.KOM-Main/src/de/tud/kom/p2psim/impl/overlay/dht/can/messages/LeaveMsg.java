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

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.dht.can.CanArea;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;

/**
 * 
 * Tells the vid neighbour with a common parent node,
 * that it wants to leave.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LeaveMsg extends CanMessage {
	private CanOverlayContact leavingNode;
	private List neighbours;
	private CanOverlayContact[] vidNeighbours;
	private CanArea area;

	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 * 		leaving contact 
	 * @param neighbours
	 * 		neighbours of leaving contact
	 * @param area
	 * 		area of leaving node
	 * @param vidNeighbours
	 * 		VID neighbours of leaving node
	 */
	public LeaveMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact c, 
			List neighbours, CanArea area, CanOverlayContact[] vidNeighbours) {
		super(sender, receiver);
		this.leavingNode = c;
		this.neighbours=neighbours;
		this.area=area;
		this.vidNeighbours=vidNeighbours;
		this.area=area;
	}

	public CanOverlayContact[] getVidNeighbours() {
		return vidNeighbours;
	}

	public CanOverlayContact getLeavingNode() {
		return leavingNode;
	}
	
	public List getNeighbours(){
		return this.neighbours;
	}
	
	public CanArea getArea(){
		return this.area;
	}

	public long getSize() {
		return ( CanConfig.CanOverlayContactSize+CanConfig.CanOverlayContactSize*neighbours.size()
				+CanConfig.CanOverlayContactSize*2 + CanConfig.CanAreaSize);
	}

}
