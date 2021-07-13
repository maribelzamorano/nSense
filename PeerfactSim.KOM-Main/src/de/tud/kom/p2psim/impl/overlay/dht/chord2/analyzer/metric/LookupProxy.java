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

/**
 * This class represents a Lookup request
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */

public class LookupProxy implements Serializable {

	public static enum Status {
		FINISHED, TIMEOUT;
	}
	
	private int hop = 0;
	
	private long startTimestamp, replyTimestamp = -1;
	
	private Status endStatus = null;
	
	private boolean validResult = false;

	private final int lookupID;
	
	public LookupProxy(int lookupID, long startTimestamp) {
		this.startTimestamp = startTimestamp;
		this.lookupID = lookupID;
	}
	
	public int getHop() {
		return hop;
	}

	public void setHop(int hop) {
		this.hop = hop;
	}

	public long getReplyTimestamp() {
		return replyTimestamp;
	}

	public void setReplyTimestamp(long replyTimestamp) {
		this.replyTimestamp = replyTimestamp;
	}

	public Status getEndStatus() {
		return endStatus;
	}

	public void setEndStatus(Status endStatus) {
		this.endStatus = endStatus;
	}

	public boolean isValidResult() {
		return validResult;
	}

	public void setValidResult(boolean validResult) {
		this.validResult = validResult;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public int getLookupID() {
		return lookupID;
	}
	
	public String toString(){
		return "[id = " + lookupID 
				+ " hop = " + hop 
				+ " time = " + (replyTimestamp - startTimestamp)
				+ " status = " + endStatus
				+ " valid = " + validResult
				+ "]";
	}
}
