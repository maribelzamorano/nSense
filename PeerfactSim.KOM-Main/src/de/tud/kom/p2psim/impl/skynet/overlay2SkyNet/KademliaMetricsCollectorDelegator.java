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


package de.tud.kom.p2psim.impl.skynet.overlay2SkyNet;

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.KademliaMsg;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.OPAnalyzerEntry;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsAggregate;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util.AbstractMetricsCollectorDelegator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class KademliaMetricsCollectorDelegator extends
		AbstractMetricsCollectorDelegator {

	private static Logger log = SimLogger
			.getLogger(KademliaMetricsCollectorDelegator.class);

	@Override
	public HashMap<String, MetricsAggregate> getStatisticsOfMsgs(
			Vector<NetMessage> msgVector, double interval, boolean sent) {
		HashMap<String, MetricsAggregate> map = new HashMap<String, MetricsAggregate>();
		String prefix;
		if (sent) {
			prefix = "Sent";
		} else {
			prefix = "Rec";
		}

		// count all instantiated messages in the simulation
		int completeMsgCounter = 0;
		double completeTraffic = 0;

		// count the different messages of the overlay
		int overlayMsgCounter = 0;
		double overlayTraffic = 0;

		if (msgVector != null) {
			completeMsgCounter = msgVector.size();
			Message msg = null;
			KademliaMsg overlayMsg;
			for (int i = 0; i < msgVector.size(); i++) {
				// determine the statistics for the complete traffic
				msg = msgVector.get(i).getPayload().getPayload();
				completeTraffic = completeTraffic + msgVector.get(i).getSize();
				// check for Overlay-message
				if (msg instanceof KademliaMsg) {
					// determine the statistics for the overlay traffic
					overlayMsgCounter = overlayMsgCounter + 1;
					overlayTraffic = overlayTraffic
							+ msgVector.get(i).getSize();
					overlayMsg = (KademliaMsg) msg;
					// TODO implement further type-checking for the statistics
					// of Kademlia
				} else {
					log.debug(msg.toString()
							+ "is not a MessageType of the current overlay");
				}
			}// for()

		}

		// amount of all messages
		MetricsAggregate ag = createAggregate(prefix + "CompleteMessages",
				completeMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// size of all messages
		ag = createAggregate(prefix + "SizeCompleteMessages", completeTraffic,
				interval);
		map.put(ag.getAggregateName(), ag);

		// bandwidth-consumption
		double bandwidth;
		double bandwidthConsumption;
		if (sent) {
			bandwidth = skyNetNode.getHost().getNetLayer()
					.getMaxUploadBandwidth();

		} else {
			bandwidth = skyNetNode.getHost().getNetLayer()
					.getMaxDownloadBandwidth();
		}
		bandwidthConsumption = completeTraffic / bandwidth;
		ag = createAggregate("Average" + prefix + "BandwidthConsumption",
				bandwidthConsumption, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of overlay-messages
		ag = createAggregate(prefix + "OverlayMessages", overlayMsgCounter,
				interval);
		map.put(ag.getAggregateName(), ag);

		// size of overlay-messages
		ag = createAggregate(prefix + "SizeOverlayMessages", overlayTraffic,
				interval);
		map.put(ag.getAggregateName(), ag);

		return map;
	}

	@Override
	public HashMap<String, MetricsAggregate> getStatisticsOfOperations(
			Vector<OPAnalyzerEntry> opVector, double interval) {
		return new HashMap<String, MetricsAggregate>();
	}

}
