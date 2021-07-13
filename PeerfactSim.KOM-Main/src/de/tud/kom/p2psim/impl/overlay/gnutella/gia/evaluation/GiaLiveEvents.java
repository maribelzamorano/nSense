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


package de.tud.kom.p2psim.impl.overlay.gnutella.gia.evaluation;

import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.ConfidenceIntervals;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.GnutellaLiveEvents;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaNode;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 * Component for live monitoring of Gia.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaLiveEvents extends GnutellaLiveEvents {
	private static final long UPDATE_INTERVAL = Simulator.MINUTE_UNIT;
	String satLevelDisplay = "n/a";

	public GiaLiveEvents() {
		super();
		LiveMonitoring.addProgressValue(new AverageSatisfactionLevelMonitor());
		new Update().scheduleAtTime(0);
	}
	
	public class AverageSatisfactionLevelMonitor implements ProgressValue {
		
		@Override
		public String getName() {
			return "Gia Satisfaction";
		}

		@Override
		public String getValue() {
			return satLevelDisplay;
		}
		
	}

	void update() {
		satLevelDisplay = updateSatisfactioLevel();
	}
	
	/**
	 * Updates the Gia satisfaction level.
	 * @return
	 */
	private String updateSatisfactioLevel() {
		double avgAccu = 0d; 
		ConfidenceIntervals set = new ConfidenceIntervals(0.95d, 2);
		for (AbstractGnutellaLikeNode n : AbstractGnutellaLikeNode.allInstances) {
			if (n instanceof GiaNode) {
				double satLvl = ((GiaNode)n).getSatisfactionLevel();
				set.addValue(satLvl);
				avgAccu += satLvl;
			}
		}
		if (set.getNumberOfValues() == 0) return "n/a";
		double avg = avgAccu / set.getNumberOfValues();
		return "lQ: " + NumberFormatToolkit.floorToDecimals(set.getLowerBound(), 2)
				+ " med: " + NumberFormatToolkit.floorToDecimals(set.getMedian(), 2)
				+ " uQ: " + NumberFormatToolkit.floorToDecimals(set.getUpperBound(), 2)
				+ " avg: " + NumberFormatToolkit.floorToDecimals(avg, 2);
	}

	public class Update implements SimulationEventHandler {
		
		boolean listeningStopped = false;

		public void scheduleWithDelay(long delay) {
			long time = Simulator.getCurrentTime() + delay;
			scheduleAtTime(time);
		}

		public void scheduleAtTime(long time) {
			time = Math.max(time, Simulator.getCurrentTime());
			Simulator.scheduleEvent(this, time, this,
					SimulationEvent.Type.TIMEOUT_EXPIRED);
		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			update();
			scheduleWithDelay(UPDATE_INTERVAL);
		}

	}
	
}
