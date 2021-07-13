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

import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.impl.overlay.dht.can.CanArea;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanVID;

/**
 * 
 * Answer for leave or takeover, tells the node the new data.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LeaveLeftMsg extends CanMessage {
	
	private CanArea area;
	private List neighbours;
	private CanOverlayContact[] vidNeighbours;
	private CanVID vid;
	private List<Object[]> newHashs;
	
	/**
	 * Answer for leave or takeover, tells the node the new data.
	 *  
	 * @param sender
	 * @param receiver
	 * @param c
	 * 		new CanOverlayContact data
	 * @param neighbours
	 * 		new list of neighbours
	 * @param vidNeighbours
	 * 		new array of VID-neighbours
	 * @param vid
	 * 		new own VID
	 * @param newHashs
	 * 		new hashs which should be saved in the area of the peer
	 */
	public LeaveLeftMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact c, List neighbours, 
			CanOverlayContact[] vidNeighbours, CanVID vid, List<Object[]> newHashs) {
		super(sender, receiver);
		this.area=c.getArea();
		this.vid=vid;
		this.neighbours=neighbours;
		this.vidNeighbours=vidNeighbours;
		this.newHashs = newHashs;
		if(newHashs == null)
			newHashs = new LinkedList<Object[]>();
		if(neighbours == null)
			neighbours = new LinkedList<Object[]>();
	}
	
	public long getSize() {
		return ( CanConfig.CanAreaSize+CanConfig.CanOverlayContactSize*neighbours.size()
			+CanConfig.CanOverlayContactSize*2+CanConfig.CanVIDSize+CanConfig.hashSize*newHashs.size() );
	}

	public CanArea getArea() {
		return area;
	}

	public List getNeighbours() {
		return neighbours;
	}
	
	public CanOverlayContact[] getVidNeighbours(){
		return this.vidNeighbours;
	}
	
	public CanVID getVid(){
		return this.vid;		
	}
	
	public List<Object[]> getNewHashs(){
		return this.newHashs;
	}



}
