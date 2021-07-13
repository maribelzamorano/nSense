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
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.StabilizeEvaluator;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class generates the <code>stabilize.dat</code> input file
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StabilizeWriter {

	private static Logger log = SimLogger.getLogger(StabilizeWriter.class);

	public final String fileName = "stabilize.dat";

	public static final String TIME = DatFileWriter.TIME;

	private BufferedWriter bufferedWriter;

	private static boolean overwrite = true;

	private final String regex = " ";

	public StabilizeWriter() {
		try {
			if (overwrite) {
				initialize();
			}
		} catch (IOException e) {
			log.error("", e);
		}
	}

	private void initialize() throws IOException {

		File root = new File(ChordStructurPostProcessing.ROOT_OUT,
				DatFileWriter.dirName);
		root.mkdirs();
		File output = new File(root, fileName);
		bufferedWriter = new BufferedWriter(new FileWriter(output, !overwrite));
		overwrite = false;
		writeMetricList();
		bufferedWriter.close();
	}

	public void writeMeasureValue(StabilizeEvaluator stabEvaluator,
			long timeStamp) throws IOException {

		File root = new File(ChordStructurPostProcessing.ROOT_OUT,
				DatFileWriter.dirName);
		File output = new File(root, fileName);
		bufferedWriter = new BufferedWriter(new FileWriter(output, !overwrite));

		String content = (double) timeStamp / Simulator.MINUTE_UNIT + " ";
		List<String> metricList = stabEvaluator.getMetricList();
		for (String metric : metricList) {
			content += regex + stabEvaluator.getMeasureValue(metric);
		}
		bufferedWriter.write(content + "\n");
		bufferedWriter.close();
	}

	private void writeMetricList() throws IOException {
		int count = 0;
		List<String> metricLits = getMetricList();
		for (String metric : metricLits) {
			count++;
			DecimalFormat formatter = new DecimalFormat("00");
			bufferedWriter.write("#" + formatter.format(count) + " " + metric
					+ "\n");
		}
		bufferedWriter.write("\n");

	}

	public List<String> getMetricList() {

		List<String> metricList = new ArrayList<String>();
		metricList.add(0, TIME);
		metricList.addAll(StabilizeEvaluator.getMetricList());
		return metricList;
	}
}
