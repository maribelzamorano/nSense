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


package de.tud.kom.p2psim.impl.overlay.dht.can.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent.Type;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * Write the measured data in a file.
 * 
 * @author Bjoern dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StatisticGenerationEvent implements SimulationEventHandler {

	final static Logger log = SimLogger
			.getLogger(StatisticGenerationEvent.class);

	private Writer statWriter, statWriter2, statWriter3;

	private boolean isActive = false;

	private EvaluationControlAnalyzer evaluationControlAnalyzer;

	/**
	 * Write the measured data in a file. Opens the files and writes the titles.
	 * 
	 * @param evaluationControlAnalyzer
	 */
	protected StatisticGenerationEvent(
			EvaluationControlAnalyzer evaluationControlAnalyzer) {
		mostLookupReq();
		this.evaluationControlAnalyzer = evaluationControlAnalyzer;

		String dirName = CanConfig.statisticsOutputPath;
		File statDir = new File(dirName);
		if (!statDir.exists() || !statDir.isDirectory()) {
			statDir.mkdirs();
		}

		File statFile = new File(dirName + "stats.dat");

		try {
			statWriter = new BufferedWriter(new FileWriter(statFile));

			// Write head line
			statWriter.write("#time[sec]\n" + "#NumOfOnlinePeers\n"
					+ "#AverageDriftDistanceAvg\n"
					+ "#AverageDriftDistanceMin\n"
					+ "#AverageDriftDistanceMax\n"
					+ "#AverageDriftDistanceStDev\n"
					+ "#AverageDriftDistanceStDevUnderAvg\n"
					+ "#AverageDriftDistanceStDevOverAvg\n"
					+ "#AverageDriftDistanceMedian\n"
					+ "#AverageStaleNeighborRatio \n"
					+ "#NumerLeaveJoinTakeoverPerMin\n"
					+ "#NumerLeaveJoinTakeoverPerMinPerNode\n"
					+ "#NumerStabilizePerMin\n"
					+ "#NumerStabilizePerMinPerNode\n"
					+ "#NumerLookupStorePerMin\n"
					+ "#NumerLookupStorePerMinPerNode\n" + "#hopsAvg\n"
					+ "#medianHops\n" + "#hopsNumberstandardDeviation\n"
					+ "#hopsNumberstandardDeviationMinus\n"
					+ "#hopsNumberstandardDeviationPlus\n"
					+ "#hopsTimeAvg all in ms\n" + "#medianHopsTime\n"
					+ "#hopsTimeStandardDeviation\n"
					+ "#hopsTimestandardDeviationMinus\n"
					+ "#hopsTimestandardDeviationPlus\n" + "#sentMsg\n"
					+ "#receivedMsg\n" + "#sendDataPerMin\n"
					+ "#sendDataPerMinPerNode\n" + "#receivedDataPerMin\n"
					+ "#receivedDataPerMin\n" + "#NeighboursAvg\n"
					+ "#medianNeighboursTime\n"
					+ "#NeighboursTimeStandardDeviation\n"
					+ "#NeighboursTimestandardDeviationMinus\n"
					+ "#NeighboursTimestandardDeviationPlus\n"
					+ "#NeighboursAvgWithoutEdge\n" + "#sendMsgPerMin\n"
					+ "#sendMsgPerMinPerNode\n" + "#receivedMsgPerMin\n"
					+ "#receivedMsgPerMinPerNode\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File statFile2 = new File(dirName + "mostReq.dat");

		try {
			statWriter2 = new BufferedWriter(new FileWriter(statFile2));
			statWriter2
					.write("#nodes which received more than (network size/10) messages.\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File statFile3 = new File(dirName + "sortedMostReq.dat");
		try {
			statWriter3 = new BufferedWriter(new FileWriter(statFile3));
			statWriter3
					.write("#nodes which received more than (network size/10) messages.\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Writes the data to the states.dat file. It doesn't generate it writes
	 * them out.
	 */
	protected void generateStatistics() {

		try {

			LinkedHashMap<CanOverlayID, CanNode> nodes = getAllCanNodes();

			double[] driftDistance = MetricComputations
					.computeCurrentDriftDistance(nodes);

			double[] leaveJoinTakeover = MetricComputations
					.numberLeaveJoinTakeover(nodes);

			double[] stabilizeMsg = MetricComputations
					.numberStabilizeMsg(nodes);

			double[] LookupStore = MetricComputations
					.numberLookupStoreMsg(nodes);

			double[] hops = MetricComputations.hops(nodes);

			double[] hopsTime = MetricComputations.transferTimeAvg(nodes);

			double sentLookupMsg = MetricComputations.sendLookupMsg(nodes);

			double receivedLookupMsg = MetricComputations
					.receivedLookupMsg(nodes);

			double[] receivedBytes2 = this.evaluationControlAnalyzer
					.getReceivedBytes(nodes);

			double[] sendBytes = this.evaluationControlAnalyzer
					.getSentBytes(nodes);

			double[] numberNeighbours = MetricComputations
					.numberNeighbours(nodes);

			double[] sentMsg = this.evaluationControlAnalyzer.getSentMsg(nodes);

			double[] receivedMsg = this.evaluationControlAnalyzer
					.getReceivedMsg(nodes);

			if (isActive)
				statWriter
						.write((Simulator.getCurrentTime() / Simulator.MINUTE_UNIT)
								+ "\t"
								+ MetricComputations
										.computeNumOfOnlinePeers(getAllCanNodes())
								+ "\t"
								+ driftDistance[0]
								+ "\t"
								+ driftDistance[1]
								+ "\t"
								+ driftDistance[2]
								+ "\t"
								+ driftDistance[3]
								+ "\t"
								+ driftDistance[4]
								+ "\t"
								+ driftDistance[5]
								+ "\t"
								+ driftDistance[6]
								+ "\t"
								+ MetricComputations
										.computeStaleContactRatio(getAllCanNodes())
								+ "\t"
								+ leaveJoinTakeover[0]
								+ "\t"
								+ leaveJoinTakeover[1]
								+ "\t"
								+ stabilizeMsg[0]
								+ "\t"
								+ stabilizeMsg[1]
								+ "\t"
								+ LookupStore[0]
								+ "\t"
								+ LookupStore[1]
								+ "\t"
								+ hops[0]
								+ "\t"
								+ hops[1]
								+ "\t"
								+ hops[2]
								+ "\t"
								+ hops[3]
								+ "\t"
								+ hops[4]
								+ "\t"
								+ hopsTime[0]
								+ "\t"
								+ hopsTime[1]
								+ "\t"
								+ hopsTime[2]
								+ "\t"
								+ hopsTime[3]
								+ "\t"
								+ hopsTime[4]
								+ "\t"
								+ sentLookupMsg
								+ "\t"
								+ receivedLookupMsg
								+ "\t"
								+ sendBytes[0]
								+ "\t"
								+ sendBytes[1]
								+ "\t"
								+ receivedBytes2[0]
								+ "\t"
								+ receivedBytes2[1]
								+ "\t"
								+ numberNeighbours[0]
								+ "\t"
								+ numberNeighbours[1]
								+ "\t"
								+ numberNeighbours[2]
								+ "\t"
								+ numberNeighbours[3]
								+ "\t"
								+ numberNeighbours[4]
								+ "\t"
								+ numberNeighbours[5]
								+ "\t"
								+ sentMsg[0]
								+ "\t"
								+ sentMsg[1]
								+ "\t"
								+ receivedMsg[0]
								+ "\t" + receivedMsg[1] + "\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Gets all peers from the globalOracle
	 * 
	 * @return all peers
	 */
	private LinkedHashMap<CanOverlayID, CanNode> getAllCanNodes() {

		LinkedHashMap<CanOverlayID, CanNode> allCanNodes = new LinkedHashMap<CanOverlayID, CanNode>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode node = h.getOverlay(CanNode.class);

			if (node != null) {
				CanNode cNode = (CanNode) node;
				allCanNodes.put(cNode.getCanOverlayID(), cNode);
			}
		}
		return allCanNodes;
	}

	/**
	 * Is started with the first event and afterwards it's repeated every
	 * CanConfig.intervallBetweenStatistics intervals
	 */
	public void eventOccurred(SimulationEvent se) {
		if (se.getData() instanceof StatisticGenerationEvent) {

			log.debug(Simulator.getSimulatedRealtime()
					+ " Triggered statistic generation.");

			generateStatistics();

			/*
			 * Schedule new STATUS event
			 */
			long scheduleAtTime = Simulator.getCurrentTime()
					+ CanConfig.intervallBetweenStatistics;
			Simulator.scheduleEvent(this, scheduleAtTime, this, Type.STATUS);

		}
	}

	public void scheduleImmediatly() {
		Simulator.scheduleEvent(this, Simulator.getCurrentTime(), this,
				Type.STATUS);
	}

	/**
	 * Starts the writer.
	 */
	public void writerStarted() {
		isActive = true;
	}

	/**
	 * Stops the writer and closes the files
	 */
	public void writerStopped() {
		isActive = false;
		try {
			mostLookupReq();
			statWriter.flush();
			statWriter.close();
			statWriter2.flush();
			statWriter2.close();
			statWriter3.flush();
			statWriter3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Writes all hashes into a file, as well the peers which received the most
	 * lookups.
	 */
	public void mostLookupReq() {

		try {
			LinkedHashMap<CanOverlayID, CanNode> nodes = getAllCanNodes();
			TreeMap<Integer, CanOverlayContact> receivedLookupRequest = MetricComputations
					.getLookupRequest(nodes);
			double sqare = 0;
			for (Integer value : receivedLookupRequest.keySet()) {
				sqare = (receivedLookupRequest.get(value).getArea().getArea()[1] - receivedLookupRequest
						.get(value).getArea().getArea()[0])
						* (receivedLookupRequest.get(value).getArea().getArea()[3] - receivedLookupRequest
								.get(value).getArea().getArea()[2]);
				statWriter2.write(receivedLookupRequest.get(value)
						.getOverlayID().toString()
						+ "\t"
						+ "LookupRequests:\t"
						+ value
						+ "\t"
						+ "RequestPerScare:\t"
						+ (((double) value) / sqare)
						+ "\t"
						+ "area:\t"
						+ receivedLookupRequest.get(value).getArea().toString()
						+ "\t" + "square\t" + sqare + "\n");

			}

			LinkedHashMap<BigInteger, Integer> lookupValues = MetricComputations
					.getLookupValues(nodes);
			for (BigInteger value : lookupValues.keySet()) {
				statWriter2.write("Requested Value: " + value.toString()
						+ " number: " + lookupValues.get(value) + "\n");

			}

			List<Integer> list2 = new ArrayList<Integer>();
			list2.addAll(receivedLookupRequest.keySet());
			Comparator<Integer> comparator = Collections
					.<Integer> reverseOrder();
			Collections.sort(list2, comparator);

			for (Integer value : list2) {
				statWriter3.write(value + "\t" + (((double) value) / sqare)
						+ "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
