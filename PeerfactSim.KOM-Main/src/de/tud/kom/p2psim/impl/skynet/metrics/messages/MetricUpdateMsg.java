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


package de.tud.kom.p2psim.impl.skynet.metrics.messages;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsEntry;

/**
 * This message contains the data for a <i>metric-update</i>, which a
 * Coordinator sends to its ParentCoordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class MetricUpdateMsg extends AbstractSkyNetMessage {

	private MetricsEntry content;

	public MetricUpdateMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, MetricsEntry content,
			long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, false,
				false);
		this.content = content;
	}

	public Message getPayload() {
		// Not needed
		return null;
	}

	public long getSize() {
		return content.getSize() + super.getSize();
	}

	/**
	 * This method returns the content of the message, which comprises the sent
	 * <code>MetricsEntry</code> of a Coordinator.
	 * 
	 * @return the <code>MetricsEntry</code> of a Coordinator
	 */
	public MetricsEntry getContent() {
		return content;
	}

}
