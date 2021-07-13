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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.gnup.DatFileWriter;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.gnup.MessageFlowWriter;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.gnup.StabilizeWriter;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.AbstractMetricStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.LookupStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.MessageStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.PeerStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.StabilizeEvaluator;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer.ChordStructurStatsWriter;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer.Scenario;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer.SummaryWriter;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This class is called after the simulation to evaluate the results
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordStructurPostProcessing {

	private static Logger log = SimLogger
			.getLogger(ChordStructurPostProcessing.class);

	public static final String ROOT_IN = ChordStructurStatsWriter.ROOT_PATH;

	public static final String ROOT_OUT = Constants.OUTPUTS_DIR
			+ File.separator + "Chord2AnalyzerResult";

	private static final String DAT_INPUT_PREFIX = ChordStructurStatsWriter.FILE_NAME_PREFIX;

	// input data for every test sample
	private Scenario scenario;

	public ChordStructurPostProcessing() {
		try {
			execute();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void execute() throws Exception {
		log.debug("read Chord structure from file");
		File dir = new File(ROOT_IN);
		String[] children = dir.list();
		if (children == null) {
			log.error("Either dir does not exist or is no file a directory");
		} else {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				if (filename.startsWith(DAT_INPUT_PREFIX)) {
					log.info("analyzer file " + filename);
					readSimpleScenario(filename);
				}
			}
		}
	}

	private void readSimpleScenario(String fileName) throws Exception {
		log.error("\n" + "\n" + "read file " + fileName);
		File datFile = new File(ChordStructurStatsWriter.ROOT_PATH, fileName);
		ObjectInputStream inputStream = null;
		// Construct the ObjectInputStream object
		inputStream = new ObjectInputStream(new FileInputStream(datFile));
		Object obj = inputStream.readObject();
		inputStream.close();

		Scenario scenario = (Scenario) obj;
		initialNewScenario(scenario);
		createStatistic(fileName);
	}

	private void initialNewScenario(Scenario scenario) {
		// Scenario
		this.scenario = scenario;
	}

	private void createStatistic(String fileInput) throws IOException {

		StabilizeEvaluator stabEvaluator = scenario.getStabilizeEvaluator();
		new StabilizeWriter().writeMeasureValue(stabEvaluator,
				scenario.getTimeStamp());
		Set<ChordContact> keyset = stabEvaluator.getRoutingTableMap().keySet();
		ArrayList<ChordContact> keyList = new ArrayList<ChordContact>(keyset);
		Collections.sort(keyList);

		log.info("node list at " + scenario.getTimeStamp()
				/ Simulator.SECOND_UNIT + " [s]");
		log.info(Arrays.toString(keyList.toArray()));

		// use the last dat file to generate statistics
		if (isLastOutput(fileInput)) {
			PeerStore peerStore = scenario.getPeerStore();
			MessageStore messageStore = scenario.getMessageStore();
			LookupStore lookupStore = scenario.getLookupStore();

			List<AbstractMetricStore> metricStores = new ArrayList<AbstractMetricStore>();
			metricStores.add(peerStore);
			metricStores.add(messageStore);
			metricStores.add(lookupStore);

			long endSimulationTime = scenario.getTimeStamp();
			new DatFileWriter(metricStores).execute(endSimulationTime);

			// calculate Summary
			SummaryWriter summaryWriter = new SummaryWriter();
			summaryWriter.putValue(SummaryWriter.Num_Peer,
					peerStore.getNum_registered_Node() + "");

			summaryWriter.putValue(SummaryWriter.Num_Lookup,
					lookupStore.getNumOfLookup() + "");

			double avg_Valid_Lookup = lookupStore.getMeasureValue(
					LookupStore.Metrics.RationValidLookupResult.name(), 0l,
					endSimulationTime);
			summaryWriter.putValue(SummaryWriter.Valid_Lookup, avg_Valid_Lookup
					+ "");

			double avg_Lookup_Time = lookupStore.getMeasureValue(
					LookupStore.Metrics.AverageLookupTimeInSec.name(), 0l,
					endSimulationTime);
			summaryWriter.putValue(SummaryWriter.Avg_Lookup_Time,
					avg_Lookup_Time + " [s]");

			double traffic_Out = messageStore.getMeasureValue(
					MessageStore.Metrics.SentSizeSetupMessages.name(), 0L,
					endSimulationTime)
					+ messageStore
							.getMeasureValue(
									MessageStore.Metrics.SentSizeServiceMessages
											.name(), 0L, endSimulationTime);
			summaryWriter.putValue(SummaryWriter.Data_traffic_Out, traffic_Out
					+ "[bytes]");

			double traffic_In = messageStore.getMeasureValue(
					MessageStore.Metrics.RecSizeSetupMessages.name(), 0L,
					endSimulationTime)
					+ messageStore.getMeasureValue(
							MessageStore.Metrics.RecSizeServiceMessages.name(),
							0L, endSimulationTime);
			summaryWriter.putValue(SummaryWriter.Data_traffic_In, traffic_In
					+ "[bytes]");

			double system_traffic_Out = messageStore.getMeasureValue(
					MessageStore.Metrics.SentSizeSetupMessages.name(), 0L,
					endSimulationTime);
			summaryWriter.putValue(SummaryWriter.System_traffic_Out,
					system_traffic_Out + "[bytes]");

			summaryWriter.write();

			// message flow
			new MessageFlowWriter().writeMeasureValue(scenario
					.getMessageFlowStore());
		}

	}

	private boolean isLastOutput(String datFile) {
		File dir = new File(ROOT_IN);
		String[] children = dir.list();
		if (children == null) {
			log.error("Either dir does not exist or is no file a directory");
			return false;
		} else {
			List<String> allFile = Arrays.asList(children);
			String last = Collections.max(allFile);
			// log.error("last file " + last);
			return datFile.equals(last);
		}
	}

	public static void main(String[] args) {
		new ChordStructurPostProcessing();
	}

}
