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

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.stat.StatUtils;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ChurnAnalyzer;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ChurnStatsAnalyzer implements ChurnAnalyzer {

	private boolean isRunning;

	private List<Long> sessionTimes;

	private List<Long> interSessionTimes;

	public ChurnStatsAnalyzer() {
		this.isRunning = false;
		this.interSessionTimes = new LinkedList<Long>();
		this.sessionTimes = new LinkedList<Long>();
	}

	public void nextInterSessionTime(long time) {
		if (this.isRunning) {
			this.interSessionTimes.add(time);
		}
	}

	public void nextSessionTime(long time) {
		if (this.isRunning)
			this.sessionTimes.add(time);
	}

	public void start() {
		this.isRunning = true;
	}

	public void stop(Writer output) {
		this.isRunning = false;
		Collections.sort(sessionTimes);
		Collections.sort(interSessionTimes);

		double[] sessionDataSet = new double[sessionTimes.size()];
		double[] interSessionDataSet = new double[interSessionTimes.size()];

		int tmp = 0;
		for (double d : interSessionTimes) {
			interSessionDataSet[tmp] = d;
			tmp++;
		}

		tmp = 0;
		for (double d : sessionTimes) {
			sessionDataSet[tmp] = d;
			tmp++;
		}

		if (sessionDataSet.length != 0 && interSessionDataSet.length != 0) {
			try {
				output.write("\n******** Session-Time Stats ***********\n");
				output.write("Median (in minutes): "
						+ StatUtils.percentile(sessionDataSet, 50)
						/ Simulator.MINUTE_UNIT + "\n");
				output.write("Mean (in minutes): "
						+ StatUtils.mean(sessionDataSet)
						/ Simulator.MINUTE_UNIT + "\n");
				output.write("******* Session-Time Stats End *********\n");
				output.write("\n");
				output
						.write("\n******** Inter-Session Time Stats ***********\n");
				output.write("Median (in minutes): "
						+ StatUtils.percentile(interSessionDataSet, 50)
						/ Simulator.MINUTE_UNIT + "\n");
				output.write("Mean (in minutes): "
						+ StatUtils.mean(interSessionDataSet)
						/ Simulator.MINUTE_UNIT + "\n");
				output
						.write("******* Inter-Session Time Stats End *********\n");

			} catch (IOException e) {
				throw new IllegalStateException("Problems in writing results"
						+ e);
			}
		}

	}

}
