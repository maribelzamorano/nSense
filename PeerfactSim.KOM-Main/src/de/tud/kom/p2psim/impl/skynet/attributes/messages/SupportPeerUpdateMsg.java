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

import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;

/**
 * A parent Coordinator uses this message-type to inform its Support Peer, if it
 * calculated a new Parent-Coordinator. For that reason, he sends this message,
 * which comprises the ID of the new Parent-Coordinator, to the Support Peer,
 * which is thereby informed about the new receiver for the
 * <i>attribute-updates</i>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerUpdateMsg extends AbstractSkyNetMessage {

	private SkyNetNodeInfo parentCoordinator;

	public SupportPeerUpdateMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SkyNetNodeInfo parentCoordinator,
			long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, true, false);
		this.parentCoordinator = parentCoordinator;
	}

	@Override
	public long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE + super.getSize();
	}

	/**
	 * This method returns the ID of the new Parent-Coordinator, which the
	 * Support Peer must address.
	 * 
	 * @return the ID of a Coordinator's Parent-Coordinator
	 */
	public SkyNetNodeInfo getParentCoordinatorInfo() {
		return parentCoordinator;
	}

}
