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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer;

import java.io.Writer;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.operations.WriteChordOverlayStateOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer.ChordStructurStatsWriter;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class contains a list of boolean variable, that enable or disable the
 * corresponding evaluation component. The variable values can be changed
 * directly in configuration file.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordOverlayAnalyzer implements Configurable, Analyzer {
	// default value = 10 Minutes
	private long scheduleTime = Simulator.MINUTE_UNIT * 10;

	public static boolean lookupStats = false;

	public static boolean messageStats = false;

	public static boolean stabilizeStats = false;

	public static boolean peerStats = false;

	public void writeCurrentStats() {

		// periodically insert operation in schedule
		new WriteChordOverlayStateOperation(this)
				.scheduleWithDelay(scheduleTime);

		// write out statistic
		new ChordStructurStatsWriter().writeStats();
	}

	@Override
	public void start() {
		// clear output directory
		new ChordStructurStatsWriter().clearOutputDir();
		// insert operation in schedule
		new WriteChordOverlayStateOperation(this)
				.scheduleWithDelay(this.scheduleTime);
	}

	@Override
	public void stop(Writer output) {
		// Nothing to do here
	}

	// Getters and Setters

	public void setScheduleTime(long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public void setLookupStats(boolean lookupStats) {
		ChordOverlayAnalyzer.lookupStats = lookupStats;
	}

	public void setMessageStats(boolean messageStats) {
		ChordOverlayAnalyzer.messageStats = messageStats;
	}

	public void setStabilizeStats(boolean stabilizeStats) {
		ChordOverlayAnalyzer.stabilizeStats = stabilizeStats;
	}

	public void setPeerStats(boolean peerStats) {
		ChordOverlayAnalyzer.peerStats = peerStats;
	}

}
