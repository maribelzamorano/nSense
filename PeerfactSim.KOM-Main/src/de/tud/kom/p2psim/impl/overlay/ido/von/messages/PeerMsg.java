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
 * This message is used to inform nodes about new neighbors.
 * 
 * It includes a list of new nodes and timestamps when they were last in contact
 * with the sender.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class PeerMsg extends AbstractVonMsg {

	VonNodeInfo[] nodes;

	Long[] timestamps;

	public PeerMsg(VonID sender, VonID receiver, VonNodeInfo[] nodes,
			Long[] timestamps) {
		super(sender, receiver);
		this.nodes = nodes;
		this.timestamps = timestamps;
	}

	@Override
	public Message getPayload() {
		// This message does not contain a payload
		return null;
	}

	@Override
	public long getSize() {
		// size = sizeOfAbstractMsg + nodes.length*sizeOfNodeInfo +
		// timestamps.length*sizeOfTimestamp
		// sizeOfTimestamp = 4byte (according to the VON specifications)
		int sizeOfNodeInfo = (nodes.length > 0) ? nodes[0]
				.getTransmissionSize() : 0;
		return getSizeOfAbstractMessage() + nodes.length * sizeOfNodeInfo
				+ timestamps.length * 4;
	}

	public VonNodeInfo[] getNodes() {
		return nodes;
	}

	public Long[] getTimestamps() {
		return timestamps;
	}

}
