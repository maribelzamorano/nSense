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
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MsgsRcvDistAnalyzer extends AbstractGnuplotDistAnalyzer implements NetAnalyzer, OperationAnalyzer, ConnectivityAnalyzer {

	Map<NetID, Integer> msgs = new HashMap<NetID, Integer>();
	
	@Override
	protected void resetDistributions() {
		msgs = new HashMap<NetID, Integer>();	//Reset message count at each interval.
		super.resetDistributions();
	}
	
	@Override
	protected String modifyResultValue(long result) {
		return NumberFormatToolkit.floorToDecimalsString(result/(double)this.getInterval()*this.TIME_UNIT_OUTPUT, 3);	//Messages per sec.
	}

	@Override
	public void operationFinished(Operation<?> op) {
		hostSeen(op.getComponent().getHost().getNetLayer().getNetID());
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		hostSeen(op.getComponent().getHost().getNetLayer().getNetID());
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		hostSeen(id);
	}
	
	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		hostGotMsg(id, msg);
	}
	
	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		hostSeen(id);
	}
	
	protected void hostSeen(NetID id) {
		this.checkTimeProgress();
		if (!msgs.containsKey(id)) {
			msgs.put(id, 0);
			this.addHostOrUpdateAll(id, new long[]{0});
		}
	}
	
	protected void hostGotMsg(NetID id, NetMessage msg) {
		this.checkTimeProgress();
		if (!msgs.containsKey(id)) {
			msgs.put(id, 1);
			this.addHostOrUpdateAll(id, new long[]{1});
		} else {
			int oldAmount = msgs.get(id);
			msgs.put(id, oldAmount+1);
			this.updateHost(id, 0, oldAmount+1);
		}
	}
	
	protected void hostLeft(NetID id) {
		this.checkTimeProgress();
		msgs.remove(id);
		this.removeHost(id);
	}

	@Override
	public void offlineEvent(Host host) {
		hostLeft(host.getNetLayer().getNetID());
	}

	@Override
	public void onlineEvent(Host host) {
		hostSeen(host.getNetLayer().getNetID());
	}

	@Override
	protected void declareDistributions() {
		this.addDistribution("msgs/sec");
	}

}
