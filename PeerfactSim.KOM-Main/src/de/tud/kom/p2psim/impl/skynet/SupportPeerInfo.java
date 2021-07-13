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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;

/**
 * This class defines the representation of a SkyNet-node, which is called
 * Support Peer and receives the <i>attribute-updates</i> of a SkyNet-node. The
 * <code>SupportPeerInfo</code>-object is provided by the parent of the sending
 * node. Beside the predefined methods of <code>AbstractAliasInfo</code>, this
 * class defines further variables, including their accessing methods, to handle
 * the required information, which are needed for sending
 * <i>attribute-updates</i> to the Support Peer. The node, which receives this
 * <code>SupportPeerInfo</code>-object, is denoted as Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public class SupportPeerInfo extends AbstractAliasInfo {

	private int tThreshold;

	public SupportPeerInfo(SkyNetNodeInfo nodeInfo, int numberOfUpdates,
			long timestampOfUpdate, int tThreshold) {
		this.nodeInfo = nodeInfo;
		this.numberOfUpdates = numberOfUpdates;
		this.timestampOfUpdate = timestampOfUpdate;
		this.tThreshold = tThreshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.skynet.AliasInfo#setTimestampOfUpdate(long)
	 */
	public void setTimestampOfUpdate(long timestampOfUpdate) {
		this.timestampOfUpdate = timestampOfUpdate;
	}

	public long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE + 2
				* SkyNetConstants.INT_SIZE + SkyNetConstants.LONG_SIZE
				+ SkyNetConstants.BOOLEAN_SIZE;
	}

	/**
	 * If an <i>attribute-update</i> is sent to the Support Peer, the variable
	 * <i>numberOfUpdates</i>, which contained the complete amount of updates
	 * for the Support Peer, is decremented.
	 */
	public void decrementNumberOfUpdates() {
		numberOfUpdates = numberOfUpdates - 1;
	}

	/**
	 * This method returns the amount of <code>AttributeEntry</code>s, which the
	 * Coordinator can send to the Support Peer at most.
	 * 
	 * @return the maximum amount of <code>AttributeEntry</code>s.
	 */
	public int getTThreshold() {
		return tThreshold;
	}

	/**
	 * This method sets the maximum amount of <code>AttributeEntry</code>s,
	 * which the Coordinator can send to the Support Peer at most.
	 * 
	 * @param threshold
	 *            contains the new maximum amount of <code>AttributeEntry</code>
	 *            s.
	 */
	public void setTThreshold(int threshold) {
		tThreshold = threshold;
	}

}
