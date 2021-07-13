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

import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;

/**
 * 
 * Reply for storeMsg. Tells that the hash value is saved.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreReplyMsg extends CanMessage{

	private boolean saved;
	private int operationID;
	
	/**
	 * Reply for storeMsg. Tells that the hash value is saved.
	 * 
	 * @param sender
	 * @param receiver
	 * @param saved
	 * 		true if saved
	 * @param operationID
	 * 		operation if of the storeOperation
	 */
	public StoreReplyMsg(CanOverlayID sender, CanOverlayID receiver, 
			boolean saved, int operationID) {
		super(sender, receiver);
		this.saved=saved;
		this.operationID=operationID;
	}

	@Override
	public long getSize() {
		return (CanConfig.booleanSize+ CanConfig.intSize);
	}
	
	
	public boolean getSaved(){
		return saved;
	}

	public String getDescription() {
		return "store operation succeeded";
	}
	
	public int getOperationID(){
		return operationID;
	}
}
