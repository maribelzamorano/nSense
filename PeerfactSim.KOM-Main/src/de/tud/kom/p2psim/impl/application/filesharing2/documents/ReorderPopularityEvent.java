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


package de.tud.kom.p2psim.impl.application.filesharing2.documents;

import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * At a predefined interval, numbers in the Zipf document set are reordered to gain
 * more or less popularity than before.
 * This is supposed to model the sudden change of popularity as it often happens 
 * in reality. This event periodically reorders the popularity.
 * 
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class ReorderPopularityEvent implements SimulationEventHandler {

	private ZipfDocumentSet set2reorder;

	public ReorderPopularityEvent(ZipfDocumentSet set2reorder) {
		this.set2reorder = set2reorder;
	}

	public void scheduleWithDelay(long delay) {
		long time = Simulator.getCurrentTime() + delay;
		scheduleAtTime(time);
	}

	public void scheduleAtTime(long time) {
		time = Math.max(time, Simulator.getCurrentTime()); 
			//to avoid to accidentally jump back in time.
		Simulator.scheduleEvent(this, time, this,
				SimulationEvent.Type.SCENARIO_ACTION);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		set2reorder.doReorder();
		new ReorderPopularityEvent(set2reorder)
				.scheduleWithDelay((long) set2reorder.getReorderDistribution()
						.returnValue());
	}

}
