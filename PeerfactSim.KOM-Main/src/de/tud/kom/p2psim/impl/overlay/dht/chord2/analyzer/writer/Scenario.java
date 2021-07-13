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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer;

import java.io.Serializable;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.LookupStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.MessageFlowStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.MessageStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.PeerStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.StabilizeEvaluator;

/**
 * This class represents the overlay state at a specified time period.
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Scenario implements Serializable{

	private long timeStamp;
	
	private PeerStore peerStore = PeerStore.getInstance();
	
	private MessageStore messageStore = MessageStore.getInstance();
	
	private LookupStore lookupStore = LookupStore.getInstance();
	
	private StabilizeEvaluator stabilizeEvaluator;
	
	private MessageFlowStore messageFlowStore = MessageFlowStore.getInstance();
	
	// Getter and Setter

	public StabilizeEvaluator getStabilizeEvaluator() {
		return stabilizeEvaluator;
	}

	public void setStabilizeEvaluator(StabilizeEvaluator stabilizeEvaluator) {
		this.stabilizeEvaluator = stabilizeEvaluator;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
	
	public PeerStore getPeerStore() {
		return peerStore;
	}

	public void setPeerStore(PeerStore peerStore) {
		this.peerStore = peerStore;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}

	public LookupStore getLookupStore() {
		return lookupStore;
	}

	public void setLookupStore(LookupStore lookupStore) {
		this.lookupStore = lookupStore;
	}

	public MessageFlowStore getMessageFlowStore() {
		return messageFlowStore;
	}

	public void setMessageFlowStore(MessageFlowStore messageFlowStore) {
		this.messageFlowStore = messageFlowStore;
	}
	
	
}
