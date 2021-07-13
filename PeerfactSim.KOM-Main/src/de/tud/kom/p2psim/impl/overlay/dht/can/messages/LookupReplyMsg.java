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
 * Lookup reply, tells the contact data which has the data.
 * Just one result is sent. If more than one contact has the data, 
 * it would be another way to send the list of contacts.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupReplyMsg extends CanMessage {
	
	private Object result;
	private int operationID;

	/**
	 * Lookup reply, tells the contact data which has the data.
	 * Just one result is sent. If more than one contact has the data, 
	 * it would be another way to send the list of contacts.
	 * 
	 * @param sender
	 * @param receiver
	 * @param result
	 * 		CanOverlayContact contains the result
	 * @param operationID
	 * 		OperatinoID of the store or lookup operation
	 */
	public LookupReplyMsg(CanOverlayID sender, CanOverlayID receiver,
			Object result, int operationID) {
		super(sender,receiver);
		this.result = result;
		this.operationID=operationID;
	}
	
	public Object getResult() {
		return result;
	}


	@Override
	public long getSize() {
		return (CanConfig.CanOverlayContactSize+CanConfig.intSize);
	}
	
	public int getOperationID(){
		return operationID;
	}
}
