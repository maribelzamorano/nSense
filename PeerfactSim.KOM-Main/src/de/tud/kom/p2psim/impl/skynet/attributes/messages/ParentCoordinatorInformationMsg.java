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
import de.tud.kom.p2psim.impl.skynet.SupportPeerInfo;

/**
 * This message contains the information, which a Coordinator sends to its
 * Sub-Coordinators concerning the <i>attribute-updates</i>. The contents of
 * this messages is utilized to tell every child, if a new Support Peer for
 * load-distribution was chosen, and to submit the maximum amount of allowed
 * entries to every Sub-Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class ParentCoordinatorInformationMsg extends AbstractSkyNetMessage {

	private SupportPeerInfo spInfo;

	private int maxEntriesForCo;

	public ParentCoordinatorInformationMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, SupportPeerInfo spInfo,
			int maxEntriesForCo, long skyNetMsgID, boolean receiverSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, receiverSP,
				false);
		this.spInfo = spInfo;
		this.maxEntriesForCo = maxEntriesForCo;
	}

	/**
	 * If the Coordinator uses a new Support Peer for load-distribution, this
	 * method returns the <code>SupportPeerInfo</code>-object, which contains
	 * the required information for a Sub-Coordinator to address that Support
	 * Peer.
	 * 
	 * @return the <code>SupportPeerInfo</code>-object
	 */
	public SupportPeerInfo getSupportPeerInfo() {
		return spInfo;
	}

	@Override
	public long getSize() {
		if (spInfo == null) {
			return 2 * SkyNetConstants.INT_SIZE + super.getSize();
		} else {
			return 2 * SkyNetConstants.INT_SIZE + spInfo.getSize()
					+ super.getSize();
		}
	}

	/**
	 * Within the process of negotiating the amount of
	 * <code>AttributeEntry</code>s between a Coordinator and its
	 * Sub-Coordinators, this method returns the maximum amount of entries,
	 * which a Coordinator assigns to its children.
	 * 
	 * @return the maximum amount of entries, which a Sub-Coordinator may send.
	 */
	public int getMaxEntriesForCo() {
		return maxEntriesForCo;
	}

}
