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

import java.math.BigDecimal;
import java.util.TreeMap;

import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeEntry;

/**
 * This message contains the data for an <i>attribute-update</i>, which a
 * Coordinator sends to its ParentCoordinator or Support Peer. Beside the
 * update, this message is utilized to advertise the maximum amount of entries,
 * which a Coordinator would like to send and to inform the Parent-Coordinator
 * of a crashed Support Peer.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class AttributeUpdateMsg extends AbstractSkyNetMessage {

	private TreeMap<BigDecimal, AttributeEntry> attributes;

	private int numberOfUpdates;

	private int numberOfMaxEntries;

	private boolean downSupportPeer;

	public AttributeUpdateMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo,
			TreeMap<BigDecimal, AttributeEntry> attributes,
			int numberOfUpdates, int numberOfMaxEntries,
			boolean downSupportPeer, long skyNetMsgID, boolean receiverSP,
			boolean senderSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, receiverSP,
				senderSP);
		this.attributes = attributes;
		this.numberOfUpdates = numberOfUpdates;
		this.numberOfMaxEntries = numberOfMaxEntries;
		this.downSupportPeer = downSupportPeer;
	}

	/**
	 * If the message is sent to a Support Peer, this method returns the amount
	 * of updates, which a Coordinator will still send to the Support Peer.
	 * 
	 * @return the amount of the remaining attributes
	 */
	public int getNumberOfUpdates() {
		return numberOfUpdates;
	}

	/**
	 * This method returns the advertised amount of <code>AttributeEntry</code>
	 * s, which a Coordinator would like to send.
	 * 
	 * @return the advertised amount of <code>AttributeEntry</code>
	 */
	public int getNumberOfMaxEntries() {
		return numberOfMaxEntries;
	}

	/**
	 * This method signalizes a potential crash of a Support Peer to the
	 * Parent-Coordinator, which utilized the Support Peer for
	 * load-distribution.
	 * 
	 * @return <code>true</code>, if the Coordinator addressed a crashed Support
	 *         Peer, <code>false</code> otherwise
	 */
	public boolean isDownSupportPeer() {
		return downSupportPeer;
	}

	/**
	 * This method returns the content of the message, which comprises the sent
	 * <code>AttributeEntry</code>s of a Coordinator.
	 * 
	 * @return the <code>AttributeEntry</code>s of a Coordinator
	 */
	public TreeMap<BigDecimal, AttributeEntry> getContent() {
		return attributes;
	}

	@Override
	public long getSize() {
		return attributes.size()
				* SkyNetConstants.ATTRIBUTE_ENTRY_SIZE_ESTIMATE + 2
				* SkyNetConstants.INT_SIZE + SkyNetConstants.INT_SIZE
				+ super.getSize();
	}

}
