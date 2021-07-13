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


package de.tud.kom.p2psim.impl.service.aggr.gossip.operations;

import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.service.aggr.gossip.GossipingAggregationService;
import de.tud.kom.p2psim.impl.service.aggr.gossip.UpdateInfo;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.UpdateRequestMsg;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.UpdateResponseMsg;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.Tuple;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * Operation that is executed on every node that gets a request from another
 * node along with the other node's aggregation data. (see Fig. 1 (b) p.222)
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateCalleeOperation extends
		AbstractOperation<GossipingAggregationService, Object> {

	Logger log = SimLogger.getLogger(UpdateCalleeOperation.class);

	private UpdateRequestMsg reqMsg;

	private TransMsgEvent receivingEvent;

	public UpdateCalleeOperation(GossipingAggregationService component,
			TransMsgEvent receivingEvent, UpdateRequestMsg reqMsg) {
		super(component);
		this.reqMsg = reqMsg;
		this.receivingEvent = receivingEvent;
	}

	@Override
	protected void execute() {
		GossipingAggregationService comp = this.getComponent();
		if (comp.isRPCLocked() && comp.getConf().shallLockMergeOnRPC()) {
			// log.debug("Currently in a merging RPC, so will ignore request to merge. This may happen sometimes and is fine. Omitting message.");
			this.operationFinished(true);
			return;
		}
		List<Tuple<Object, UpdateInfo>> infoRem = reqMsg.getPayloadInfo();
		boolean epochValid = comp.getSync().onEpochSeen(reqMsg.getEpoch());
		Message msg = new UpdateResponseMsg(comp.getSync().getEpoch(),
				comp.getAllLocalUpdateInfos(),comp.getGossipingNodeCountValue().extractInfo());
		comp.getHost()
				.getTransLayer()
				.sendReply(msg, receivingEvent, comp.getPort(),
						TransProtocol.UDP);
		// log.debug("Sending reply to " + receivingEvent.getSenderTransInfo());
		if (epochValid) {
			comp.updateLocalValues(infoRem,reqMsg.getNcInfo(), "fromReq");
		} else {
			comp.updateWhenOutdatedNeighbor(infoRem);
		}
		this.operationFinished(true);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
