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


package de.tud.kom.p2psim.impl.service.aggr.gossip;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.ResyncRequest;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.ResyncResponse;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.UpdateRequestMsg;
import de.tud.kom.p2psim.impl.service.aggr.gossip.operations.UpdateCalleeOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The Gossiping Aggregation Service's message handler for the transport layer.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageHandler implements TransMessageListener {

	Logger log = SimLogger.getLogger(MessageHandler.class);

	private GossipingAggregationService component;

	public MessageHandler(GossipingAggregationService component) {
		super();
		this.component = component;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		// log.debug("Message arrived at host " + component.getHost());
		Message msg = receivingEvent.getPayload();
		if (msg instanceof ResyncRequest) {
			long time2NextEpoch = component.isSynced() ? component.getSync()
					.getTimeToNextEpoch() : -1;
			ResyncResponse response = new ResyncResponse(component.getSync()
					.getEpoch(), time2NextEpoch);
			component
					.getHost()
					.getTransLayer()
					.sendReply(response, receivingEvent, component.getPort(),
							TransProtocol.UDP);
		} else if (msg instanceof UpdateRequestMsg) {
			if (component.isSynced()) {
				// log.debug("Dispatching update request message");
				UpdateRequestMsg reqMsg = (UpdateRequestMsg) msg;
				log.trace(Simulator.getSimulatedRealtime() + " Peer with ID "
						+ component.getHost().getNetLayer().getNetID()
						+ " received UpdateRequestMsg with "
						+ reqMsg.getPayloadInfo().size() + " entries and size "
						+ reqMsg.getSize());
				new UpdateCalleeOperation(component, receivingEvent, reqMsg)
						.scheduleImmediately();
			} else {
				// Omit. Should not harm the averaging process, since omitted
				// requests always keep
				// the total sum equal, unless omitted responses.
			}
		}
	}

}
