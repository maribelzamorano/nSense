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

import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;

/**
 * 
 * Sends a lookup request
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupMsg extends CanMessage {
	
	private DataID id;
	private TransInfo sender;
	private int operationID;

	/**
	 * Sends a lookup request
	 * 
	 * @param sender
	 * @param receiver
	 * @param contact
	 * 		CanOverlayContact of requesting peer
	 * @param id
	 * 		requested lookup hash
	 * @param operationID
	 * 		operation ID of the request Operation
	 */
	public LookupMsg(CanOverlayID sender, CanOverlayID receiver, CanOverlayContact contact, DataID id, 
			int operationID) {
		super(sender, receiver);
		this.id = id;
		this.sender=contact.getTransInfo();
		this.operationID=operationID;
		setHop(1);
	}

	public LookupMsg(CanOverlayID sender, CanOverlayID receiver, TransInfo senderTrans, DataID id, 
			int operationID) {
		super(sender, receiver);
		this.id = id;
		this.sender=senderTrans;
		this.operationID=operationID;
		
		setHop(1);
	}

	@Override
	public long getSize() {
		long sizeLookup = CanConfig.idSize+CanConfig.transInfoSize+CanConfig.intSize; 
		return sizeLookup;
	}

	/* getters & setters */
	public TransInfo getContact() {
		return sender;
	}

	public DataID getId() {
		return id;
	}
	
	public int getOperationID(){
		return operationID;
	}
}
