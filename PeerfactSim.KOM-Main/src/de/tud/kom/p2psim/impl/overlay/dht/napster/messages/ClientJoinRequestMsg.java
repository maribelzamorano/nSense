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


package de.tud.kom.p2psim.impl.overlay.dht.napster.messages;

import de.tud.kom.p2psim.api.napster.JoinLeaveMessage;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.napster.AbstractNapsterRequestMsg;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ClientJoinRequestMsg extends AbstractNapsterRequestMsg<TransInfo>
		implements JoinLeaveMessage {

	public ClientJoinRequestMsg(NapsterOverlayID sender,
			NapsterOverlayID receiver, TransInfo content, int opID) {
		super(sender, receiver, content, opID);
	}

	@Override
	public long getSize() {
		long newSize = super.getSize() + SkyNetConstants.TRANS_INFO_SIZE;
		return newSize;
	}
}
