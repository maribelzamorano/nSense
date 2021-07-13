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


package de.tud.kom.p2psim.impl.skynet.attributes.messages;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;

/**
 * This class defines a SkyNet-message, which is used as ACK to a
 * <code>SupportPeerRequestMsg</code>. In addition, it contains the answer of
 * the SkyNet-node, concerning the question of being a Support Peer for a
 * requesting Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerRequestACKMsg extends AbstractSkyNetMessage {

	private SkyNetNodeInfo nodeInfo;

	private boolean accept;

	public SupportPeerRequestACKMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SkyNetNodeInfo nodeInfo,
			boolean accept, long skyNetMsgID, boolean senderSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, true, false,
				senderSP);
		this.nodeInfo = nodeInfo;
		this.accept = accept;
	}

	/**
	 * This method returns the actual ID of a SkyNet-node, if it accepts to play
	 * the role of a Support Peer for the requesting Coordinator.
	 * 
	 * @return the ID of the answering SkyNet-node
	 */
	public SkyNetNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	/**
	 * This method outlines, if a requested SkyNet-node accepts its role as
	 * Support Peer or not.
	 * 
	 * @return <code>true</code>, if the requested node accepts,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAccept() {
		return accept;
	}

}
