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


package de.tud.kom.p2psim.impl.network.gnp;

import org.apache.log4j.Logger;

import umontreal.iro.lecuyer.probdist.LognormalDist;
import de.tud.kom.p2psim.api.network.NetLatencyModel;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.impl.network.IPv4Message;
import de.tud.kom.p2psim.impl.network.gnp.topology.CountryLookup;
import de.tud.kom.p2psim.impl.network.gnp.topology.PingErLookup;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TCPMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnpLatencyModel implements NetLatencyModel {

	private static Logger log = SimLogger.getLogger(GnpLatencyModel.class);

	public static final int MSS = IPv4Message.MTU_SIZE - IPv4Message.HEADER_SIZE - TCPMessage.HEADER_SIZE;

	private static PingErLookup pingErLookup;

	private static CountryLookup countryLookup;

	private boolean usePingErInsteadOfGnp = false;

	private boolean useAnalyticalFunctionInsteadOfGnp = false;

	private boolean usePingErJitter = false;

	private boolean usePingErPacketLoss = false;

	public void init(PingErLookup pingErLookup, CountryLookup countryLookup) {
		GnpLatencyModel.pingErLookup = pingErLookup;
		GnpLatencyModel.countryLookup = countryLookup;
	}

	private double getMinimumRTT(GnpNetLayer sender, GnpNetLayer receiver) {
		String ccSender = sender.getCountryCode();
		String ccReceiver = receiver.getCountryCode();
		double minRtt = 0.0;
		if (usePingErInsteadOfGnp) {
			minRtt = pingErLookup.getMinimumRtt(ccSender, ccReceiver, countryLookup);
		} else if (useAnalyticalFunctionInsteadOfGnp) {
			double distance = GeoLocationOracle.getGeographicalDistance(sender.getNetID(), receiver.getNetID());
			minRtt = 62 + (0.02 * distance);
		} else {
			NetPosition senderPos = sender.getNetPosition();
			NetPosition receiverPos = receiver.getNetPosition();
			minRtt = senderPos.getDistance(receiverPos);
		}
		log.info("Minimum RTT for " + ccSender + " to " + ccReceiver + ": " + minRtt + " ms");
		return minRtt;
	}

	private double getPacketLossProbability(GnpNetLayer sender, GnpNetLayer receiver) {
		String ccSender = sender.getCountryCode();
		String ccReceiver = receiver.getCountryCode();
		double twoWayLossRate = 0.0;
		double oneWayLossRate = 0.0;
		if (usePingErPacketLoss) {
			twoWayLossRate = pingErLookup.getPacktLossRate(ccSender, ccReceiver, countryLookup);
			twoWayLossRate /= 100;
			oneWayLossRate = 1 - Math.sqrt(1 - twoWayLossRate);
		}
		log.debug("Packet Loss Probability for " + ccSender + " to " + ccReceiver + ": " + (oneWayLossRate * 100) + " %");
		return oneWayLossRate;

	}

	private double getNextJitter(GnpNetLayer sender, GnpNetLayer receiver) {
		String ccSender = sender.getCountryCode();
		String ccReceiver = receiver.getCountryCode();
		double randomJitter = 0.0;
		if (usePingErJitter) {
			LognormalDist distri = pingErLookup.getJitterDistribution(ccSender, ccReceiver, countryLookup);
			randomJitter = distri.inverseF(Simulator.getRandom().nextDouble());
		}
		log.debug("Random Jitter for " + ccSender + " to " + ccReceiver + ": " + randomJitter + " ms");
		return randomJitter;

	}

	private double getAverageJitter(GnpNetLayer sender, GnpNetLayer receiver) {
		String ccSender = sender.getCountryCode();
		String ccReceiver = receiver.getCountryCode();
		double jitter = 0.0;
		if (usePingErJitter) {
			jitter = pingErLookup.getAverageRtt(ccSender, ccReceiver, countryLookup) - pingErLookup.getMinimumRtt(ccSender, ccReceiver, countryLookup);
		}
		log.debug("Average Jitter for " + ccSender + " to " + ccReceiver + ": " + jitter + " ms");
		return jitter;
	}

	public double getUDPerrorProbability(GnpNetLayer sender, GnpNetLayer receiver, IPv4Message msg) {
		if (msg.getPayload().getSize() > 65507)
			throw new IllegalArgumentException("Message-Size ist too big for a UDP-Datagramm (max 65507 byte)");
		double lp = getPacketLossProbability(sender, receiver);
		double errorProb = 1 - Math.pow(1 - lp, msg.getNoOfFragments());
		log.debug("Error Probability for a " + msg.getPayload().getSize() + " byte UDP Datagram from " + sender.getCountryCode() + " to " + receiver.getCountryCode() + ": " + errorProb * 100 + " %");
		return errorProb;
	}

	public double getTcpThroughput(GnpNetLayer sender, GnpNetLayer receiver) {
		double minRtt = getMinimumRTT(sender, receiver);
		double averageJitter = getAverageJitter(sender, receiver);
		double packetLossRate = getPacketLossProbability(sender, receiver);
		double mathisBW = ((MSS * 1000) / (minRtt + averageJitter)) * Math.sqrt(1.5 / packetLossRate);
		return mathisBW;
	}

	public long getTransmissionDelay(double bytes, double bandwidth) {
		double messageTime = bytes / bandwidth;
		long delay = Math.round((messageTime * Simulator.SECOND_UNIT));
		log.debug("Transmission Delay (s): " + messageTime + " ( " + bytes + " bytes  /  " + bandwidth + " bytes/s )");
		return delay;
	}

	public long getPropagationDelay(GnpNetLayer sender, GnpNetLayer receiver) {
		double minRtt = getMinimumRTT(sender, receiver);
		double randomJitter = getNextJitter(sender, receiver);
		double receiveTime = (minRtt + randomJitter) / 2.0;
		long latency = Math.round(receiveTime * Simulator.MILLISECOND_UNIT);
		log.debug("Propagation Delay for " + sender.getCountryCode() + " to " + receiver.getCountryCode() + ": " + receiveTime + " ms");
		return latency;
	}

	public long getLatency(NetLayer sender, NetLayer receiver) {
		return getPropagationDelay((GnpNetLayer) sender, (GnpNetLayer) receiver);
	}

	public void setUsePingErRttData(boolean pingErRtt) {
		usePingErInsteadOfGnp = pingErRtt;
	}

	public void setUseAnalyticalRtt(boolean analyticalRtt) {
		useAnalyticalFunctionInsteadOfGnp = analyticalRtt;
	}

	public void setUsePingErJitter(boolean pingErRtt) {
		usePingErJitter = pingErRtt;
	}

	public void setUsePingErPacketLoss(boolean pingErPacketLoss) {
		usePingErPacketLoss = pingErPacketLoss;
	}

}
