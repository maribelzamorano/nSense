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

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;

/**
 * This class represents a Peer
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class OverlayNodeProxy implements Serializable{

	private ChordID id;

	private long join_Timestamp = -1;

	private long leave_Timestamp = -1;

	public OverlayNodeProxy(ChordID id) {
		this.id = id;
	}
	
	// Getter and Setter
	
	public long getJoin_Timestamp() {
		return join_Timestamp;
	}

	public void setJoin_Timestamp(long join_Timestamp) {
		this.join_Timestamp = join_Timestamp;
	}

	public long getLeave_Timestamp() {
		return leave_Timestamp;
	}

	public void setLeave_Timestamp(long leave_Timestamp) {
		this.leave_Timestamp = leave_Timestamp;
	}

	public ChordID getId() {
		return id;
	}

}
