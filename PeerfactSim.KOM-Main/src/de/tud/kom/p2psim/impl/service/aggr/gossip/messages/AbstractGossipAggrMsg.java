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


package de.tud.kom.p2psim.impl.service.aggr.gossip.messages;

import de.tud.kom.p2psim.api.common.Message;

/**
 * Superclass of all messages between nodes in the gossiping aggregation
 * protocol.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public abstract class AbstractGossipAggrMsg implements Message {

	long epoch;

	/**
	 * @return the current epoch the node is in that sent this message
	 */
	public long getEpoch() {
		return epoch;
	}

	/**
	 * Default constructor
	 * 
	 * @param epoch , the epoch the sender is currently in
	 */
	public AbstractGossipAggrMsg(long epoch) {
		super();
		this.epoch = epoch;
	}
	
}
