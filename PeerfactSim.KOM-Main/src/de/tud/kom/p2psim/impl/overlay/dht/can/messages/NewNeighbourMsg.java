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

import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;


/**
 * 
 * Tells a peer its new neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class NewNeighbourMsg extends CanMessage {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	List<CanOverlayContact> oldNeighbours;
	List<CanOverlayContact> newNeighbours;
	CanOverlayContact c;
	
	/**
	 * Tells a peer its new neighbours.
	 * 
	 * @param sender
	 * @param receiver
	 * @param c
	 * 		sender peer is as well removed
	 * @param oldNeighbours
	 * 		these neighbours will be deleted
	 * @param newNeighbours
	 * 		these neighbours are added.
	 */
	public NewNeighbourMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact c, List<CanOverlayContact> oldNeighbours, 
			List<CanOverlayContact> newNeighbours) {
		super(sender, receiver);
		this.newNeighbours=newNeighbours;
		this.oldNeighbours=oldNeighbours;
		this.c=c;
		log.debug("NewNeighbourMsg sent. sent from node " + sender.toString()
				+ " to: " + receiver.toString());
	}

	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize*oldNeighbours.size()+
				CanConfig.CanOverlayContactSize*newNeighbours.size()+ CanConfig.CanOverlayContactSize);
	}

	public List getNewNeighbours() {
		return newNeighbours;
	}
	
	public List getOldNeighbours() {
		return oldNeighbours;
	}
	
	public CanOverlayContact getContact(){
		return this.c;
	}
}
