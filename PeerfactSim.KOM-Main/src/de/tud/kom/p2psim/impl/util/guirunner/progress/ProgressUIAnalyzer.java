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

package de.tud.kom.p2psim.impl.util.guirunner.progress;

import java.io.File;
import java.io.Writer;

import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ProgressUIAnalyzer implements NetAnalyzer, OperationAnalyzer {

	SimulationProgressView view;
	
	public ProgressUIAnalyzer(String configFile) {
		
		view = SimulationProgressView.getInstance();
		view.setConfigurationName(new File(configFile).getName());
		
		view.rebuildProgressValues();
		
		Thread.setDefaultUncaughtExceptionHandler(view);
		Thread.currentThread().setUncaughtExceptionHandler(view);
		
	}

	@Override
	public void start() {
		view.notifySimulationRunning();
		view.update();
	}

	@Override
	public void stop(Writer output) {
		view.notifySimulationFinished();
		view.update();
	}

	@Override
	public void operationFinished(Operation<?> op) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}
	
	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

}
