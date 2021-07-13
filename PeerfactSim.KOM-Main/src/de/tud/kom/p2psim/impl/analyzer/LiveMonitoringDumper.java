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

package de.tud.kom.p2psim.impl.analyzer;

import java.io.Writer;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class LiveMonitoringDumper implements NetAnalyzer, OperationAnalyzer,
ConnectivityAnalyzer {

	double interval = 20; //In seconds, REAL TIME!
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(Writer output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offlineEvent(Host host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onlineEvent(Host host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationFinished(Operation<?> op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub
		
	}
	
	public void onEvent() {
		
	}

}
