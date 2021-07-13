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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.operations;

import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * A generic superclass for AnalyzerOperation.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractAnalyzerOperation implements
		SimulationEventHandler {

	public void scheduleWithDelay(long delay) {
		long time = Simulator.getCurrentTime() + delay;
		scheduleAtTime(time);
	}

	public void scheduleAtTime(long time) {
		time = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, time, this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

	public void scheduleImmediately() {
		Simulator.scheduleEvent(this, Simulator.getCurrentTime(), this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

	public abstract void eventOccurred(SimulationEvent se);
}
