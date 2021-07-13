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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.Bandwidth;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.BandwidthConsumption;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.Hosts;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.MessagesCounter;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.StaleContactRatio;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics.StaleMessageRatio;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class DefaultGnuplotAnalyzer extends AbstractGnuplotAnalyzer implements
		NetAnalyzer, ConnectivityAnalyzer {

	protected Hosts hosts = new Hosts();

	MessagesCounter netMsgs = new MessagesCounter();

	StaleContactRatio stale = new StaleContactRatio(hosts);

	StaleMessageRatio staleMsg = new StaleMessageRatio();

	int offlineEvents;

	int onlineEvents;

	Bandwidth upstreamBW = new Bandwidth("Upstream");

	Bandwidth downstreamBW = new Bandwidth("Downstream");

	Bandwidth staleBW = new Bandwidth("Stale");

	BandwidthConsumption cons = new BandwidthConsumption();

	public DefaultGnuplotAnalyzer() {
		super();
		LiveMonitoring.addProgressValue(this.new ChurnProgress());
	}

	@Override
	protected void declareMetrics() {
		addMetric(hosts);
		addMetric(netMsgs);
		addMetric(stale);
		addMetric(staleMsg);
		addMetric(upstreamBW.getAvgBW());
		addMetric(upstreamBW.getPeakBW());
		addMetric(downstreamBW.getAvgBW());
		addMetric(downstreamBW.getPeakBW());
		addMetric(cons.getAvgDn());
		addMetric(cons.getAvgUp());
		addMetric(cons.getPeakDn());
		addMetric(cons.getPeakUp());
		addMetric(staleBW.getAvgBW());
		addMetric(staleBW.getPeakBW());
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		this.checkTimeProgress();
		if (!this.isActive())
			return;
		staleMsg.messageFailed();
		staleBW.addMsg(id, msg);
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		this.checkTimeProgress();
		if (!this.isActive())
			return;
		hosts.hostSeen(id);
		staleMsg.messageSucceeded();
		downstreamBW.addMsg(id, msg);
		cons.messageReceived(id, msg);
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		this.checkTimeProgress();
		if (!this.isActive())
			return;
		hosts.hostSeen(id);
		stale.messageSent(msg.getReceiver());
		netMsgs.messageSent(msg);
		upstreamBW.addMsg(id, msg);
		cons.messageSent(id, msg);
	}

	@Override
	public void offlineEvent(Host host) {
		this.checkTimeProgress();
		hosts.goneOffline(host.getNetLayer().getNetID());
		offlineEvents++;
	}

	@Override
	public void onlineEvent(Host host) {
		this.checkTimeProgress();
		hosts.goneOnline(host.getNetLayer().getNetID());
		onlineEvents++;
	}

	public void start() {
		super.start();
		cons.init();
	}

	public class ChurnProgress implements ProgressValue {

		@Override
		public String getName() {
			return "Churn";
		}

		@Override
		public String getValue() {
			return "Online-Events: " + onlineEvents + ", Offline-Events: "
					+ offlineEvents;
		}

	}

}
