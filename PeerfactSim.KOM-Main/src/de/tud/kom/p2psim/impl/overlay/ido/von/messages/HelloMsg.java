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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonID;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNodeInfo;

/**
 * This message is used to contact new neighbors.
 * 
 * It includes the contact information of the sender and a list of his known
 * enclosing neighbors.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HelloMsg extends AbstractVonMsg {

	private final VonID[] receiversEnclosings;

	private final VonNodeInfo senderNodeInfo;

	public HelloMsg(VonID sender, VonID receiver, VonNodeInfo senderNodeInfo,
			VonID[] receiversEnclosings) {
		super(sender, receiver);

		this.senderNodeInfo = senderNodeInfo;
		this.receiversEnclosings = receiversEnclosings;
	}

	@Override
	public Message getPayload() {
		// There is no payload
		return null;
	}

	public VonID[] getReceiversEnclosings() {
		return receiversEnclosings;
	}

	public VonNodeInfo getSenderNodeInfo() {
		return senderNodeInfo;
	}

	@Override
	public long getSize() {
		// Size = sizeOfAbstractMsg + #recEncl * SizeVonID + SizeOfVonNodeInfo
		return getSizeOfAbstractMessage()
				+ receiversEnclosings.length
				* (receiversEnclosings.length > 0 ? receiversEnclosings[0]
						.getTransmissionSize() : 0)
				+ senderNodeInfo.getTransmissionSize();
	}

}
