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

import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;

/**
 * 
 * A missing peer is detected and the responsible peer is informed.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverMsg extends CanMessage{
	
	private CanOverlayContact missingNode;
	private List<CanOverlayContact> neighboursOfMissing; 
	private CanOverlayContact[] vidNeighbourOfMissing;

	/**
	 * A missing peer is detected and the responsible peer is informed.
	 * 
	 * @param sender
	 * @param receiver
	 * @param missingNode
	 * 		lost node
	 * @param neighboursOfMissing
	 * 		neighbourList of the lost peer
	 * @param vidNeighbourOfMissing
	 * 		VID neighbours of the lost peer
	 */
	public TakeoverMsg(CanOverlayID sender, CanOverlayID receiver, 
			CanOverlayContact missingNode, 
			List<CanOverlayContact> neighboursOfMissing, CanOverlayContact[] vidNeighbourOfMissing) {
		super(sender, receiver);
		
		this.missingNode=missingNode;
		this.neighboursOfMissing = neighboursOfMissing;
		this.vidNeighbourOfMissing=vidNeighbourOfMissing;
		if (neighboursOfMissing==null)
			this.neighboursOfMissing = new LinkedList<CanOverlayContact>();
		if(vidNeighbourOfMissing==null)
			this.vidNeighbourOfMissing = new CanOverlayContact[2];
	}

	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize+CanConfig.CanOverlayContactSize*neighboursOfMissing.size()
				+CanConfig.CanOverlayContactSize*2);
	}

	public CanOverlayContact getMissingNode() {
		return missingNode;
	}
	
	public List<CanOverlayContact> getNeighboursOfMissing(){
		return this.neighboursOfMissing;
	}
	
	public CanOverlayContact[] getVidNeighboursOfMissing(){
		return this.vidNeighbourOfMissing;
	}
}
