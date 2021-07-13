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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordStructurPostProcessing;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.MessageFlowStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class generates the <code>peer.dat</code> input file
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MessageFlowWriter {

	private static Logger log = SimLogger.getLogger(MessageFlowWriter.class);

	public final String fileName = "peer.dat";

	public static final String PeerID = "PeerId";

	private BufferedWriter bufferedWriter;

	private static boolean overwrite = true;
	
	private final String regex = " ";
	
	public MessageFlowWriter() {
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
		root.mkdir();
		File output = new File(root, fileName);
		bufferedWriter = new BufferedWriter(new FileWriter(output, !overwrite));
		overwrite = false;
		writeMetricList();
		bufferedWriter.close();
	}

	public void writeMeasureValue(MessageFlowStore messageFlowStore) throws IOException {

		File root = new File(ChordStructurPostProcessing.ROOT_OUT,
				DatFileWriter.dirName);
		File output = new File(root, fileName);
		bufferedWriter = new BufferedWriter(new FileWriter(output, !overwrite));
		// message flow
		HashMap<ChordID, double []> result = messageFlowStore.getMeasureValue();
		Set<ChordID> keySet = result.keySet();
		for (ChordID node : keySet) {
			String contain = node.getValue() + "";
			double[] values = result.get(node);
			for (double value : values) {
				contain = contain + regex + value;
			}
			bufferedWriter.write(contain + "\n");
		}
		bufferedWriter.close();
	}

	private void writeMetricList() throws IOException{
		int count = 0;
		List<String> metricLits = getMetricList();
		for (String metric : metricLits) {
			count++;
			DecimalFormat formatter = new DecimalFormat("00");
			bufferedWriter.write("#" + formatter.format(count) + " " + metric + "\n");
		}
		bufferedWriter.write("\n");

	}

	public List<String> getMetricList() {

		List<String> metricList = new ArrayList<String>();
		metricList.add(0, PeerID);
		metricList.addAll(MessageFlowStore.getMetricList());
		return metricList;
	}
}
