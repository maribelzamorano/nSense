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


package de.tud.kom.p2psim.impl.service.aggr.gossip.messages;

import java.util.List;

import de.tud.kom.p2psim.impl.service.aggr.gossip.UpdateInfo;
import de.tud.kom.p2psim.impl.service.aggr.gossip.UpdateInfoNodeCount;
import de.tud.kom.p2psim.impl.util.Tuple;

/**
 * Superclass of gossiping update messsages, see p. 222
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractUpdateMsg extends AbstractGossipAggrMsg {

	List<Tuple<Object, UpdateInfo>> payloadInfo;

	UpdateInfoNodeCount ncInfo;

	public AbstractUpdateMsg(long epoch,
			List<Tuple<Object, UpdateInfo>> payloadInfo,
			UpdateInfoNodeCount ncInfo) {
		super(epoch);
		this.payloadInfo = payloadInfo;
		this.ncInfo = ncInfo;
	}

	@Override
	public long getSize() {
		int sz = 8;
		sz += ncInfo.getSize();
		for (Tuple<Object, UpdateInfo> infoElem : payloadInfo)
			sz += infoElem.getB().getSize() + 1;
		return sz;
	}

	public List<Tuple<Object, UpdateInfo>> getPayloadInfo() {
		return payloadInfo;
	}

	public UpdateInfoNodeCount getNcInfo() {
		return ncInfo;
	}

}
