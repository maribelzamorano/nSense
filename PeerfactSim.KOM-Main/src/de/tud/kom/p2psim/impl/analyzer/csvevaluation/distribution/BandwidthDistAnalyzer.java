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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class BandwidthDistAnalyzer extends AbstractGnuplotDistAnalyzer implements NetAnalyzer {

	Map<NetID, Long> bwUp = new HashMap<NetID, Long>();
	Map<NetID, Long> bwDown = new HashMap<NetID, Long>();
	Map<NetID, Long> bwStale = new HashMap<NetID, Long>();
	
	private Map<NetID, Bandwidth> bwOfHosts = new HashMap<NetID, Bandwidth>();
	
	@Override
	protected void resetDistributions() {
		resetMap(bwUp);
		resetMap(bwDown);
		resetMap(bwStale);
		super.resetDistributions();
		resetHostsInDistr();
	}
	
	void resetHostsInDistr() {
		for (NetID id : bwUp.keySet()) {
			this.addHostOrUpdateAll(id, new long[]{0, 0, 0});
		}
	}
	
	void resetMap(Map<?, Long> map) {
		for (Entry<?, Long> e : map.entrySet()) {
			e.setValue(0l);
		}
	}
	
	public void start() {
		super.start();
		init();
	}
	
	public void init() {
		Map<String, List<Host>> hosts = Simulator.getInstance().getScenario().getHosts();
		for (List<Host> group : hosts.values()) {
			for (Host host : group) {
				double upBW = host.getNetLayer().getMaxUploadBandwidth();
				double downBW = host.getNetLayer().getMaxDownloadBandwidth();
				NetID netID = host.getNetLayer().getNetID();
				bwOfHosts .put(netID, new Bandwidth(downBW, upBW));
				bwUp.put(netID, 0l);
				bwDown.put(netID, 0l);
				bwStale.put(netID, 0l);
			}
		}
		resetHostsInDistr();
	}
	
	@Override
	protected String modifyResultValue(long result) {
		return NumberFormatToolkit.floorToDecimalsString(result/((double)this.getInterval())*this.TIME_UNIT_OUTPUT, 3);	//Messages per sec.
	}
	
	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		if (!isActive()) return;
		increaseValue(id, bwStale, 2, msg);
	}
	
	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		if (!isActive()) return;
		increaseValue(id, bwDown, 1, msg);
	}
	
	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		if (!isActive()) return;
		increaseValue(id, bwUp, 0, msg);
	}
	
	protected void increaseValue(NetID id, Map<NetID, Long> map, int index, NetMessage msg) {
		this.checkTimeProgress();
		long oldAmount = map.get(id);
		long newValue = oldAmount+msg.getSize();
		map.put(id, newValue);
		this.updateHost(id, index, newValue);
	}

	@Override
	protected void declareDistributions() {
		this.addDistribution("upBW");
		this.addDistribution("downBW");
		this.addDistribution("staleBW");
	}

}
