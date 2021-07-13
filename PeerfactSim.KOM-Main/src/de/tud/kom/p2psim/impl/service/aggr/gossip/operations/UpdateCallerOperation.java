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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.PeriodicOperation;
import de.tud.kom.p2psim.impl.service.aggr.gossip.GossipingAggregationService;
import de.tud.kom.p2psim.impl.service.aggr.gossip.IConfiguration;
import de.tud.kom.p2psim.impl.service.aggr.gossip.Monitoring;
import de.tud.kom.p2psim.impl.service.aggr.gossip.UpdateInfo;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.UpdateRequestMsg;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.UpdateResponseMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.DefaultTransInfo;
import de.tud.kom.p2psim.impl.util.Tuple;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.stat.distributions.StaticDistribution;
import de.tud.kom.p2psim.impl.util.toolkits.CollectionHelpers;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class UpdateCallerOperation
		extends
		PeriodicOperation.RandomIntervalPeriodicOperation<GossipingAggregationService, Object> {

	Logger log = SimLogger.getLogger(UpdateCallerOperation.class);

	static final boolean staticInterval = false;

	public UpdateCallerOperation(GossipingAggregationService component,
			long interval) {
		super(component, new StaticDistribution(((double) interval)
				/ Simulator.SECOND_UNIT));
	}

	public void stop() {
		super.stop();
		getComponent().setRPCLocked(false);
	}

	@Override
	protected void executeOnce() {
		GossipingAggregationService comp = this.getComponent();
		IConfiguration conf = comp.getConf();
		comp.measureAttributes();
		List<Tuple<Object, UpdateInfo>> info = comp.getAllLocalUpdateInfos();

		// Determine next receiver of update
		Collection<OverlayContact> nbrs = comp
				.getNeighborDeterminationStrategy().getNeighbors();
		OverlayContact nb4req = CollectionHelpers.getRandomEntry(nbrs);
		Monitoring.onNeighborCountSeen(nbrs.size());
		if (nb4req == null) {
			return;
		}

		Message msg = new UpdateRequestMsg(comp.getSync().getEpoch(), info,
				comp.getGossipingNodeCountValue().extractInfo());
		TransMessageCallback cb = new TransMessageCallback() {

			@Override
			public void receive(Message msg, TransInfo senderInfo, int commId) {
				getComponent().setRPCLocked(false);
				handleAnswer(msg, senderInfo);
				Monitoring.addSuccessfulRPC();
			}

			@Override
			public void messageTimeoutOccured(int commId) {
				getComponent().setRPCLocked(false);
				Monitoring.addUnsuccessfulRPC();
			}
		};
		log.debug(Simulator.getSimulatedRealtime() + " Peer with ID "
				+ comp.getHost().getNetLayer().getNetID()
				+ " SENDS UpdateRequestMsg with " + info.size()
				+ " entries and size " + msg.getSize());

		comp.setRPCLocked(true);
		comp.getHost()
				.getTransLayer()
				.sendAndWait(
						msg,
						DefaultTransInfo.getTransInfo(nb4req.getTransInfo()
								.getNetId(), comp.getPort()), comp.getPort(),
						TransProtocol.UDP, cb, conf.getReqRespTimeout());
	}

	protected void handleAnswer(Message msg, TransInfo senderInfo) {
		if (msg instanceof UpdateResponseMsg) {
			UpdateResponseMsg respMsg = (UpdateResponseMsg) msg;
			GossipingAggregationService comp = this.getComponent();
			log.debug(Simulator.getSimulatedRealtime() + " Peer with ID "
					+ comp.getHost().getNetLayer().getNetID()
					+ " received UpdateResponseMsg with "
					+ respMsg.getPayloadInfo().size() + " entries and size "
					+ respMsg.getSize());
			if (comp.getSync().onEpochSeen(respMsg.getEpoch())) {
				comp.updateLocalValues(respMsg.getPayloadInfo(),
						respMsg.getNcInfo(), "fromResp");
			}
			this.getComponent().getSync().onCycleFinished();
		} else {
			throw new IllegalArgumentException(
					"Got a response message of an illegal type: "
							+ msg.getClass().getName());
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

}
