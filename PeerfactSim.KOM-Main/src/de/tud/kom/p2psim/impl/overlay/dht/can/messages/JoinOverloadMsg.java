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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.can.CanArea;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * Answer in the join Operation.
 * Tells the node the new area, neighbours and vid neighbours. 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinOverloadMsg extends CanMessage {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	List neighbours;
	CanArea area;
	CanOverlayContact[] vidNeighbours;
	
	/**
	 * Answer in the join Operation.
	 * Tells the node the new area, neighbours and vid neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param neighbours
	 * 		new Neighbours
	 * @param area
	 * 		new area
	 * @param vidNeighbours
	 * 		new vidNeighbours
	 */
	public JoinOverloadMsg(CanOverlayID sender, CanOverlayID receiver,
			List neighbours, CanArea area, CanOverlayContact[] vidNeighbours) {
		super(sender, receiver);
		log.debug("JoinOverloadMsg sent.");
		this.neighbours=neighbours;
		this.area= area;
		this.vidNeighbours = vidNeighbours;
	}

	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize*neighbours.size()
				+CanConfig.CanAreaSize+CanConfig.CanOverlayContactSize*2);
	}

	public List getNeighbours() {
		return neighbours;
	}
	
	public CanArea getArea(){
		return area;
	}
	
	public CanOverlayContact[] getVidNeighbours(){
		return vidNeighbours;
	}

}
