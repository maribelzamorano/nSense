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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.gnup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordStructurPostProcessing;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.AbstractMetricStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class prepares data files for gnuplot.
 *  
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class DatFileWriter {

	public static final String dirName = "data";

	public static final String fileName = "chord.dat";

	private BufferedWriter bufferedWriter;

	private List<AbstractMetricStore> metricStores;

	private static Logger log = SimLogger.getLogger(DatFileWriter.class);

	public static final String TIME = "TIME";

	private final long METRIC_INTERVALL = ChordConfiguration.METRIC_INTERVALL;  
		
	private final String regex = " ";
	
	public DatFileWriter(List<AbstractMetricStore> metricStores) {

		this.metricStores = metricStores;
	}

	public void execute(long endTime) throws IOException {
		File root = new File(ChordStructurPostProcessing.ROOT_OUT, dirName);
		root.mkdir();
		File output = new File(root, fileName);
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(output, false));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		writeMetricList();
		writeMeasureValue(endTime);
		bufferedWriter.close();
	}

	private void writeMetricList() throws IOException {
		int count = 0;
		List<String> metricLits = getMetricList();
		for (String metric : metricLits) {
			count++;
			DecimalFormat formatter = new DecimalFormat("00");
			bufferedWriter.write("#" + formatter.format(count) + " " + metric + "\n");
		}
		bufferedWriter.write("\n");
	}

	private void writeMeasureValue(long endTime) throws IOException {
		long time = 0;
		while (time < endTime) {
			long middle = time + METRIC_INTERVALL / 2;
			String output = (double) middle / Simulator.MINUTE_UNIT + " ";
			for (AbstractMetricStore store : metricStores) {
				List<String> subMetric = store.getMetricList();
				for (String metric : subMetric) {
					double value = store.getMeasureValue(metric, time, time + METRIC_INTERVALL);
					output += regex + value;
				}
			}
			bufferedWriter.write(output+"\n");
			time += METRIC_INTERVALL;
		}

	}
	
	public List<String> getMetricList() {

		List<String> metricList = new ArrayList<String>();
		metricList.add(0, TIME);
		for (AbstractMetricStore store : metricStores) {
			metricList.addAll(store.getMetricList());
		}
		return metricList;
	}
}
