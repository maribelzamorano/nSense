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


package de.tud.kom.p2psim.impl.skynet.analyzing.analyzers;

import java.io.Serializable;

/**
 * This class defines and comprises the objects, which are stored within
 * {@link NetLayerAnalyzer}. Instead of storing the complete messages, which are
 * received, sent or dropped, just an instance of this class with the required
 * objects is stored in <code>NetLayerAnalyzer</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class NetLayerAnalyzerEntry implements Serializable {

	private long timestamp;

	private long size;

	private Class msgClass;

	public NetLayerAnalyzerEntry(long timestamp, Class msgClass, long size) {
		this.msgClass = msgClass;
		this.size = size;
		this.timestamp = timestamp;
	}

	/**
	 * Returns the size of the message, which was sent, received or dropped.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Returns the <code>Class</code> of the message, which was sent, received
	 * or dropped.
	 * 
	 * @return the <code>Class</code>
	 */
	public Class getMsgClass() {
		return msgClass;
	}

	/**
	 * Returns the time of the message, when it was sent, received or dropped.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
