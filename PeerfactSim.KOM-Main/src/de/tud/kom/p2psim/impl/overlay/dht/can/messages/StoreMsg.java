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
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;

/**
 * 
 * Tries to find the right peer to send a hash value and the connected contact. 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreMsg extends CanMessage{

	private CanOverlayContact sender;
	private DataID id;
	private int operationID;
	
	/**
	 * Tries to find the right peer to send a hash value and the connected contact. 
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 * 		CanOverlayContact of the peer which want to save the hash
	 * @param id
	 * 		hash value
	 * @param operationID
	 * 		operation id of the storeOperation
	 */
	public StoreMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact contact, DataID id, int operationID) {
		super(sender, receiver);
		this.sender=contact;
		this.id=id;
		this.operationID=operationID;
		
		setHop(1);
	}

	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize+CanConfig.idSize+CanConfig.intSize);
	}
	
	public CanOverlayContact getContact(){
		return this.sender;
	}
	
	public DataID getId(){
		return id;
	}
	
	public int getOperationID(){
		return this.operationID;
	}
}
