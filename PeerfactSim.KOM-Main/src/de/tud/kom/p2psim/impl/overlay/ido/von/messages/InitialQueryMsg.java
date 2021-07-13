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

package de.tud.kom.p2psim.impl.overlay.ido.von.messages;

import java.awt.Point;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonContact;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonID;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNodeInfo;

/**
 * This message is used to query for the initial neighbors of a node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class InitialQueryMsg extends AbstractVonMsg {

	private final VonNodeInfo senderInfo;

	public InitialQueryMsg(VonContact senderContact, Point position,
			int aoiRadius) {
		/*
		 * The receiver is not specified as it is unknown -> other nodes will
		 * forward the msg to the right receiver
		 */
		super(senderContact.getOverlayID(), VonID.EMPTY_ID);

		this.senderInfo = new VonNodeInfo(senderContact, position, aoiRadius);
	}

	@Override
	public Message getPayload() {
		// There is no payload message in this case
		return null;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMsg + sizeOfNodeInfo
		return getSizeOfAbstractMessage() + senderInfo.getTransmissionSize();
	}

	public VonNodeInfo getSenderInfo() {
		return senderInfo;
	}
}
