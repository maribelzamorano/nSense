/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package de.tud.kom.p2psim.impl.overlay.ido.evaluation;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.IDOOracle;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.ido.NodeInfo;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent.Type;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class controls the statistic gathering process. It holds the reference
 * for the writers and is concerned with writing the statistic data on the one
 * hand and for scheduling new statistic generation events after the time that
 * is set in the constructor.
 * 
 * @author Christoph Muenker
 * @author Maribel Zamorano
 * @version 12/20/2013
 **/
public class StatisticGenerationEvent implements SimulationEventHandler {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger
			.getLogger(StatisticGenerationEvent.class);

	/**
	 * The time interval between two samples.
	 */
	private long sampleStatisticRate;

	private Writer statWriter;

	/**
	 * The analyzer of the received and sent message of the peers
	 */
	private EvaluationControlAnalyzer analyzer;

	/**
	 * Whether the generation of the statistic is active
	 */
	private boolean isActive = false;

	/**
	 * The IDO oracle for the IDO, that can be used to determined the ideal set
	 * of neighbors
	 */
	private IDOOracle oracle;

	/**
	 * Assignment of IDs for the Host.
	 * <p>
	 * Is used from other classes
	 */
	public static Map<OverlayID, Integer> hostIDs = new Hashtable<OverlayID, Integer>();

	/**
	 * Sets the given parameter.
	 * 
	 * @param sampleStatisticRate
	 *            The time interval between two samples.
	 * @param analyzer
	 *            The analyzer of the received and sent message of the peers
	 * @param oracle2
	 *            The IDO oracle for the IDO, that can be used to determined the
	 *            ideal set of neighbors
	 * @param outputWriter
	 *            The output writer for the metrics
	 */
	protected StatisticGenerationEvent(long sampleStatisticRate,
			EvaluationControlAnalyzer analyzer, IDOOracle oracle2) {
		this.sampleStatisticRate = sampleStatisticRate;
		this.analyzer = analyzer;
		this.oracle = oracle2;
		String dirName = CanConfig.statisticsOutputPath;
		File statDir = new File(dirName);
		if (!statDir.exists() || !statDir.isDirectory()) {
			statDir.mkdirs();
		}

		File statFile = new File(dirName + "VON_Stats.dat");

		try {
			statWriter = new BufferedWriter(new FileWriter(statFile));

			// Write head line
			statWriter.write("#time[sec]\t" + "#NumOfOnlinePeers\t"
					+ "#globaleConnectivity\t"
					+ "#globalConnectedComponentFactor\t" + "#HostID\t"
					+ "#visionRange\t" + "#position\t" + "#nodeSpeed\t"
					+ "#trafficDown\t" + "#trafficUp\t" + "#traffic\t"
					+ "#msgCountDown\t" + "#msgCountUp\t" + "#truePositive\t"
					+ "#falsePositive\t" + "#falseNegative\t" + "#precision\t"
					+ "#recall\t" + "#positionError\t" + "#dispersion\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Add this event to the scheduler of the simulator.
	 */
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
			statWriter.flush();
			statWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generates the metrics and add a new event to the scheduler.
	 */
	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getData() instanceof StatisticGenerationEvent) {

			log.debug(Simulator.getSimulatedRealtime()
					+ " Triggered statistic generation.");

			generateMetrics();

			/*
			 * Schedule new STATUS event
			 */
			long scheduleAtTime = Simulator.getCurrentTime()
					+ sampleStatisticRate;
			Simulator.scheduleEvent(this, scheduleAtTime, this, Type.STATUS);

		}
	}

	/**
	 * Measure the metrics and write out the data.
	 */
	private void generateMetrics() {

		if (isActive) {
			LinkedHashMap<OverlayID, IDONode> onlineNodes = getAllOnlineNodes();
			LinkedHashMap<OverlayID, IDONode> onlineNodesIDMap = getOverlayIDMap(onlineNodes);

			// reset the oracle and fill it with the actually information
			oracle.reset();
			oracle.insertNodeInfos(getNodeInfos(onlineNodesIDMap));
			try {
				double numberOnlinePeers = MetricsComputation
						.computeNumOfOnlinePeers(onlineNodes);
				double globaleConnectivity = MetricsComputation
						.computeGlobalConnectivity(onlineNodesIDMap);
				double globalConnectedComponentFactor = MetricsComputation
						.computeGlobalConnectedComponentFactor(onlineNodesIDMap);

				for (OverlayID host : onlineNodes.keySet()) {
					IDONode node = onlineNodes.get(host);

					Integer hostID = StatisticGenerationEvent.hostIDs.get(host);
					if (hostID == null) {
						StatisticGenerationEvent.hostIDs.put(host,
								StatisticGenerationEvent.hostIDs.size());
						hostID = StatisticGenerationEvent.hostIDs.get(host);
						log.info("No host identifiere for this host! The new hostID for this host is: "
								+ hostID);
					}

					int visionRange = MetricsComputation
							.computeVisionRange(node);
					java.awt.geom.Point2D centroid = MetricsComputation
							.computeCentroid(node, onlineNodesIDMap);
					Point ownPosition = MetricsComputation
							.computeOwnPosition(node);

					double nodeSpeed = MetricsComputation
							.computeNodeSpeed(node);

					double trafficDown = MetricsComputation.computeTraffic(
							node, analyzer.getReceivedMsgsPerPeer());
					double trafficUp = MetricsComputation.computeTraffic(node,
							analyzer.getSentMsgsPerPeer());
					double traffic = trafficDown + trafficUp;
					int msgCountDown = MetricsComputation.computeMsgsCount(
							node, analyzer.getReceivedMsgsPerPeer());
					int msgCountUp = MetricsComputation.computeMsgsCount(node,
							analyzer.getSentMsgsPerPeer());

					int[] confusionMatrix = MetricsComputation
							.computeConfusionMatrix(node, oracle);
					int truePositive = confusionMatrix[0];
					int falsePositive = confusionMatrix[1];
					int falseNegative = confusionMatrix[2];
					double precision = MetricsComputation.computePrecision(
							truePositive, falsePositive);
					double recall = MetricsComputation.computeRecall(
							truePositive, falseNegative);

					double positionError = MetricsComputation
							.computePositionError(node, onlineNodesIDMap);
					double dispersion = MetricsComputation.computeDispersion(
							node, centroid, onlineNodesIDMap);

					statWriter
							.write((Simulator.getCurrentTime() / Simulator.SECOND_UNIT)
									+ "\t"
									+ numberOnlinePeers
									+ "\t"
									+ globaleConnectivity
									+ "\t"
									+ globalConnectedComponentFactor
									+ "\t"
									+ host.toString()
									+ "\t"
									+ visionRange
									+ "\t"
									+ ownPosition.x
									+ "\t"
									+ ownPosition.y
									+ "\t"
									+ nodeSpeed
									+ "\t"
									+ trafficDown
									+ "\t"
									+ trafficUp
									+ "\t"
									+ traffic
									+ "\t"
									+ msgCountDown
									+ "\t"
									+ msgCountUp
									+ "\t"
									+ truePositive
									+ "\t"
									+ falsePositive
									+ "\t"
									+ falseNegative
									+ "\t"
									+ precision
									+ "\t"
									+ recall
									+ "\t"
									+ positionError + "\t" + dispersion + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// clear Msgs-storage
			analyzer.getReceivedMsgsPerPeer().clear();
			analyzer.getSentMsgsPerPeer().clear();
		}
	}

	/**
	 * Creates to every node in the given list, a {@link NodeInfo}.
	 * 
	 * @param onlineNodesIDMap
	 *            A map of nodes.
	 * @return A list of NodeInfos.
	 */
	private static List<IDONodeInfo> getNodeInfos(
			LinkedHashMap<OverlayID, IDONode> nodes) {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>();
		for (OverlayID id : nodes.keySet()) {
			IDONode node = nodes.get(id);
			if (node.getPeerStatus() == PeerStatus.PRESENT
					&& node.getHost().getNetLayer().isOnline()) {

				Point position = node.getPosition();
				int aoi = node.getAOI();

				result.add(new NodeInfo(position, aoi, id));
			}
		}
		return result;

	}

	/**
	 * Gets a map of all {@link IDONodeNDim}s, which are created in this
	 * simulation.
	 * 
	 * @return A map of {@link IDONodeNDim}.
	 */
	private static LinkedHashMap<Host, IDONode> getAllNodes() {

		LinkedHashMap<Host, IDONode> result = new LinkedHashMap<Host, IDONode>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode node = h.getOverlay(IDONode.class);

			if (node != null) {
				IDONode idoNode = (IDONode) node;

				if (!hostIDs.containsKey(h)) {
					hostIDs.put(idoNode.getOverlayID(), hostIDs.size());
				}
				result.put(h, idoNode);
			}
		}
		return result;
	}

	/**
	 * Gets {@link IDONodeNDim}s back, that think, they are connected with the
	 * Overlay.
	 * 
	 * @return A map of {@link IDONodeNDim}s.
	 */
	private static LinkedHashMap<OverlayID, IDONode> getAllOnlineNodes() {

		LinkedHashMap<OverlayID, IDONode> result = new LinkedHashMap<OverlayID, IDONode>();
		for (Host h : getAllNodes().keySet()) {
			OverlayNode node = h.getOverlay(IDONode.class);

			if (node != null) {
				IDONode idoNode = (IDONode) node;
				if (idoNode.isPresent()) {
					result.put(node.getOverlayID(), idoNode);
				}
			}
		}
		return result;
	}

	/**
	 * Change the key of the map. From {@link Host} to the {@link OverlayID}.
	 * 
	 * @param onlineNodes
	 *            A map with key {@link Host} and value {@link IDONodeNDim}.
	 * @return A map with key {@link OverlayID} and value {@link IDONodeNDim}.
	 */
	private static LinkedHashMap<OverlayID, IDONode> getOverlayIDMap(
			LinkedHashMap<OverlayID, IDONode> onlineNodes) {
		LinkedHashMap<OverlayID, IDONode> result = new LinkedHashMap<OverlayID, IDONode>();
		for (OverlayID h : onlineNodes.keySet()) {
			IDONode node = onlineNodes.get(h);
			result.put(node.getOverlayID(), node);
		}
		return result;
	}
}
