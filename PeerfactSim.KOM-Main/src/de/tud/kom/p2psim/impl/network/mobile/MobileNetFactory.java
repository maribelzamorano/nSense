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


package de.tud.kom.p2psim.impl.network.mobile;

import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetLatencyModel;
import de.tud.kom.p2psim.impl.network.mobile.MovementModel.MovementInstance;
import de.tud.kom.p2psim.impl.network.mobile.MovementModel.MovementModel;
import de.tud.kom.p2psim.impl.network.mobile.MovementModel.RandomWaypointModel;
import de.tud.kom.p2psim.impl.simengine.Simulator;


/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MobileNetFactory implements ComponentFactory {
	
	/**
	 * In Kbytes per second
	 */
	private final static double DEFAULT_DOWN_BANDWIDTH = 500l;

	/**
	 * In KBytes per second
	 */
	private final static double DEFAULT_UP_BANDWIDTH = 100l;
	
	private final static int DEFAULT_RESOLUTION = 10;

	private MobileSubnet subnet;
	
	private MobileMovementManager mv;

	Bandwidth bandwidth;

	private int resolution;
	
	private int idCounter = 0;
	
	private Set<Integer> usedIds = new HashSet<Integer>();

	MovementModel model = new RandomWaypointModel();	//The default model, can be assigned in the config accordingly
	
	public MobileNetFactory() {
		subnet = new MobileSubnet();
		this.resolution = DEFAULT_RESOLUTION;
		bandwidth = new Bandwidth(DEFAULT_DOWN_BANDWIDTH, DEFAULT_UP_BANDWIDTH);
		this.mv = new MobileMovementManager();
	}

	private MobileNode newPosition() {
		double oldX, oldY;
		if (usedIds.size()==0) 
		{
			//Create first node in the middle of the map
			oldX=0.5; 
			oldY=0.5;
		}
		else
		{
			oldX=Simulator.getRandom().nextDouble();
			oldY=Simulator.getRandom().nextDouble();
		}
		return new MobileNode(oldX,oldY);
	}


	public void setLatencyModel(NetLatencyModel model) {
		subnet.setLatencyModel(model);
	}
	
	public void setDownBandwidth(double downBandwidth) {
		this.bandwidth.setDownBW(downBandwidth);
	}

	public void setUpBandwidth(double upBandwidth) {
		this.bandwidth.setUpBW(upBandwidth);
	}

	/* 
	 * Defines the number of quadrants the network map is split into.
	 */
	
	public void setResolution(int quadrants) {
		mv.setResolution(quadrants);
		this.resolution = quadrants;
		}
	
	public MobileNetLayer createComponent(Host host) {
		if (mv.getResolution()==0) mv.setResolution(resolution);
		MobileNode c= newPosition();
		MobileNetID id = this.createNewID();
		MobileNetLayer n= new MobileNetLayer(subnet, id, mv, c, this.bandwidth);
		mv.addNodeToLayer(id, n);
		MovementInstance m= model.getNewInstance(); //changed by leo@relevantmusic.de
		c.setMovementModel(m);
		return n;
	}

	public MobileNetID createNewID() {
		while (usedIds.contains(idCounter))
			idCounter++;
		MobileNetID nextId = new MobileNetID(idCounter);
		usedIds.add(idCounter++);
		return nextId;
		
	}

	public MobileNetID parseID(String s) {
		int id = Integer.parseInt(s);
		MobileNetID nextId = new MobileNetID(id);
		usedIds.add(id);
		return nextId;
	}
	
	@Override
	public String toString() {
		return "Mobile Network Factory";
	}
	
	public void setMovementModel(MovementModel model) {
		this.model = model;
	}
	
}
