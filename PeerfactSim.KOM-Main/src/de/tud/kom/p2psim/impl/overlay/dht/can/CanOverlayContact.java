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


package de.tud.kom.p2psim.impl.overlay.dht.can;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.can.evaluation.GetDataOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The Contact-card of every node. It includes the ID,
 * TransInfo, isAllive and CanArea. These contact cards are sent
 * through the network. 
 * 
 * @author Bjoern Dollk <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 *
 */
public class CanOverlayContact implements OverlayContact<CanOverlayID>, 
	Comparable<CanOverlayID> {
	private static Logger log = SimLogger.getLogger(CanNode.class);

	private CanOverlayID ID;
	private TransInfo transInfo;
	private boolean isAlive;
	private CanArea area;
	public GetDataOperation getDataOperation; //just used for evaluation 
	
	public CanOverlayContact(CanOverlayID ID, TransInfo transInfo){
		this.ID = ID;
		this.transInfo = transInfo;
		this.area = new CanArea();
		isAlive=false;
		log.debug(Simulator.getSimulatedRealtime() + " New Contact: ID: " + ID.toString());
	}
	
	public CanOverlayContact(CanOverlayID ID, TransInfo transInfo, GetDataOperation getDataOperation){
		this.ID = ID;
		this.transInfo = transInfo;
		this.area = new CanArea();
		isAlive=false;
		log.debug(Simulator.getSimulatedRealtime() + " New Contact: ID: " + ID.toString());
		this.getDataOperation = getDataOperation;
	}
	
	public CanOverlayContact(CanOverlayID ID, TransInfo transInfo, CanArea area, boolean isAlive){
		this.ID = ID;
		this.transInfo = transInfo;
		this.area = area;
		this.isAlive = isAlive;
	}

	public CanOverlayID getOverlayID() {
		return this.ID;
	}
	

	
	public TransInfo getTransInfo() {
		return this.transInfo;
	}

	public void setAlive(boolean alive){
		this.isAlive=alive;
	}
	
	public boolean isAlive(){
		return this.isAlive;
	}

	public String toString(){
		String ret = "[oid= x=" + ID.getValue() + " -> ip="
		+ transInfo.getNetId() + "]";
		return ret;
	}
	
	public int compareTo(CanOverlayID o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public CanOverlayContact clone() {
		CanOverlayContact cloned = new CanOverlayContact(this.ID, this.transInfo, this.area, this.isAlive());
		cloned.setAlive(this.isAlive);
		return cloned;
	}
	
	public CanArea getArea() {
		return area;
	}

	public void setArea(CanArea area) {
		this.area = area;
		
	}

	
}
