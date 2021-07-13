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

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;

/**
 * This class represents Message
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MessageProxy implements Serializable {

	public static enum SentOrReceive {
		SEND, RECEIVE;
	}

	private ChordContact node;

	private Class msgClass;

	private long timeStamp;

	private long size;
	
	private SentOrReceive type;

	public MessageProxy(ChordContact node, Class msgClass, long timeStamp, long size,
			SentOrReceive type) {
		this.msgClass = msgClass;
		this.node = node;
		this.timeStamp = timeStamp;
		this.size = size;
		this.type = type;
	}

	// Getter and Setter

	public ChordContact getNode() {
		return node;
	}

	public Class getMsgClass() {
		return msgClass;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public SentOrReceive getType() {
		return type;
	}

	public long getSize() {
		return size;
	}

}
