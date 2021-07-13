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


package de.tud.kom.p2psim.impl.util.guirunner.impl;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.guirunner.progress.ProgressUIAnalyzer;
import de.tud.kom.p2psim.impl.util.guirunner.progress.SimulationProgressView;
import de.tud.kom.p2psim.impl.util.livemon.LivemonCommonAnalyzer;
import de.tud.kom.p2psim.impl.util.Tuple;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SimulationThread extends Thread {

	int seed;
	private ConfigFile f;

	public SimulationThread(ConfigFile f, int chosenSeed) {
		this.setName("SimulationThread");
		this.f = f;
		this.seed = chosenSeed;
	}

	@Override
	public void run() {
		SimulationProgressView view = SimulationProgressView.getInstance();
		Thread.setDefaultUncaughtExceptionHandler(view);
		view.setVisible(true);

		Simulator sim = Simulator.getInstance();
		Map<String, String> variables = new HashMap<String, String>((int)((f.getVariables().size() + 1)*1.33));
		variables.put("seed", String.valueOf(seed));
		for (Tuple<String, String> t : f.getVariables()) {
			variables.put(t.getA(), t.getB());
			//System.out.println("GUIRunner: Setting variable: " + t);
		}
		String filename = f.getFile().getAbsolutePath();
		
		sim.configure(filename, variables);
		Simulator.getMonitor().setAnalyzer(new LivemonCommonAnalyzer());
		Simulator.getMonitor().setAnalyzer(new ProgressUIAnalyzer(filename));
		sim.start(true);
		
		System.err.println("Simulation finished.");
	}

}
