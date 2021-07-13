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

import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricUpdateSyncMsg extends AbstractSkyNetMessage {

	private long lastMetricSync;

	private long updateIntervalOffset;

	public MetricUpdateSyncMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, long updateIntervalOffset,
			long lastMetricSync, long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, false,
				false);
		this.lastMetricSync = lastMetricSync;
		this.updateIntervalOffset = updateIntervalOffset;
	}

	public long getLastMetricSync() {
		return lastMetricSync;
	}

	public long getUpdateIntervalOffset() {
		return updateIntervalOffset;
	}

	@Override
	public long getSize() {
		return SkyNetConstants.LONG_SIZE + SkyNetConstants.LONG_SIZE
				+ super.getSize();
	}
}
