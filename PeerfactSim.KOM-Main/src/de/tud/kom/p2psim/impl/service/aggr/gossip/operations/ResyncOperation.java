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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.service.aggr.gossip.GossipingAggregationService;
import de.tud.kom.p2psim.impl.service.aggr.gossip.IConfiguration;
import de.tud.kom.p2psim.impl.service.aggr.gossip.Monitoring;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.ResyncRequest;
import de.tud.kom.p2psim.impl.service.aggr.gossip.messages.ResyncResponse;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.DefaultTransInfo;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.toolkits.CollectionHelpers;
import de.tud.kom.p2psim.impl.util.toolkits.TimeToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ResyncOperation extends
		AbstractOperation<GossipingAggregationService, Object> {

	static final Logger log = SimLogger.getLogger(ResyncOperation.class);

	boolean stopped = false;

	public ResyncOperation(GossipingAggregationService component) {
		super(component, Operations.getEmptyCallback());
	}

	@Override
	protected void execute() {
		final GossipingAggregationService comp = getComponent();
		IConfiguration conf = getComponent().getConf();

		Collection<OverlayContact> nbrs = comp
				.getNeighborDeterminationStrategy().getNeighbors();
		OverlayContact nb4req = CollectionHelpers.getRandomEntry(nbrs);
		Monitoring.onNeighborCountSeen(nbrs.size());

		if (nb4req == null) {
			// log.debug("Trying to get random neighbor, but there are currently no neighbors at "
			// + comp + ". Suspending.");
			if (!stopped)
				ResyncOperation.this.scheduleWithDelay(conf.getResyncTimeout());
			return;
		}

		Message msg = new ResyncRequest(comp.getSync().getEpoch());
		TransMessageCallback cb = new TransMessageCallback() {

			@Override
			public void receive(Message msg, TransInfo senderInfo, int commId) {
				log.debug(Simulator.getSimulatedRealtime() + " Host "
						+ getComponent().getHost().getNetLayer().getNetID()
						+ " received ResyncResponse from "
						+ senderInfo.getNetId() + " with id " + commId);
				if (!stopped)
					handleAnswer(msg, senderInfo);
			}

			@Override
			public void messageTimeoutOccured(int commId) {
				log.debug("Did not receive response in time: " + comp.getUID()
						+ ". Rescheduling.");
				if (!stopped)
					ResyncOperation.this.scheduleImmediately();
			}
		};
		// log.debug("Sending resync request from " +
		// comp.getOverlayUsed().getOverlayID() + " to " +
		// nb4req.getOverlayID());
		comp.getHost()
				.getTransLayer()
				.sendAndWait(
						msg,
						DefaultTransInfo.getTransInfo(nb4req.getTransInfo()
								.getNetId(), comp.getPort()), comp.getPort(),
						TransProtocol.UDP, cb, conf.getResyncTimeout());
	}

	protected void handleAnswer(Message msg, TransInfo senderInfo) {
		if (msg instanceof ResyncResponse) {
			// log.debug("Received response from " + senderInfo.getNetId());
			ResyncResponse respMsg = (ResyncResponse) msg;
			final GossipingAggregationService comp = this.getComponent();
			comp.getSync().onEpochSeen(respMsg.getEpoch() + 1);// We already set
																// the host

			long delay = respMsg.getTimeToNextEpoch();
			log.debug("Delaying update caller operation of "
					+ comp.getUID()
					+ " to "
					+ new TimeToolkit(Simulator.MILLISECOND_UNIT)
							.timeStringFromLong(delay) + " for epoch "
					+ (respMsg.getEpoch() + 1) + ".");
			new AbstractOperation<GossipingAggregationService, Object>(comp) {

				@Override
				protected void execute() {
					if (!stopped)
						comp.startNewUpdateCallerOperation();
					this.operationFinished(true);
				}

				@Override
				public Object getResult() {
					return null;
				}

			}.scheduleWithDelay(delay);
			this.operationFinished(true);
		} else {
			throw new IllegalArgumentException(
					"Got a response message of an illegal type: "
							+ msg.getClass().getName());
		}
	}

	public void stop() {
		this.stopped = true;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
