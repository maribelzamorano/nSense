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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric;

import java.io.Serializable;
import java.util.ArrayList;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;

/**
 * This class represents message flow
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageFlowProxy implements Serializable {

	private int id;

	private ArrayList<ChordID> hops;

	public MessageFlowProxy(int id) {

		this.id = id;
		hops = new ArrayList<ChordID>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<ChordID> getHops() {
		return hops;
	}

	public void setHops(ArrayList<ChordID> hops) {
		this.hops = hops;
	}

}
