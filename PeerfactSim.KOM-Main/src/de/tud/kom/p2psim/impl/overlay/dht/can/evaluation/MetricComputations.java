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

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Calculates the metrics from the given Data.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 *
 */
public class MetricComputations {

	//private static Logger log = SimLogger.getLogger(CanNode.class);

	private static int receivedBytes = 0;

	private static int sentBytes = 0;

	/**
	 * Compute the drift distance. The difference between the observed postion
	 * and the real position.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		double[] returnArr = { driftDistanceAvg, minDriftDistance,
				maxDriftDistance, standardDeviation, standardDeviationMinus,
				standardDeviationPlus, median };
	 */
	public static double[] computeCurrentDriftDistance(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		int numOfNodesToConsider = globalNodes.size();
		int numOfConsideredDistances = 0;

		double minDriftDistance = Double.MAX_VALUE;
		double maxDriftDistance = 0;

		double[] realNeighbor = new double[globalNodes.size()];
		for (int i = 0; i < realNeighbor.length; i++)
			realNeighbor[i] = 0;

		ArrayList<Double> driftDistances = new ArrayList<Double>();

		double driftDistanceAvgSum = 0;

		for (CanOverlayID id : globalNodes.keySet()) {
			CanNode node = globalNodes.get(id);

			if (node.getLocalContact() == null
					|| node.getPeerStatus() != PeerStatus.PRESENT
					|| node.getHost().getNetLayer().isOffline()) {
				/*
				 * We do not want to consider this node as it is not part of the
				 * overlay and therefore the computation of the drift distance
				 * does not make any sense.
				 */
				numOfNodesToConsider--;
				continue;
			}
			List<CanOverlayContact> neighborsInfo = node.getNeighbours();

			double localDriftDistanceSum = 0;

			int numOfNeighbors = neighborsInfo.size();

			for (CanOverlayContact neighbor : neighborsInfo) {
				CanOverlayID nId = neighbor.getOverlayID();

				if (globalNodes.get(nId) == null
						|| globalNodes.get(nId).getPeerStatus() != PeerStatus.PRESENT
						|| globalNodes.get(nId).getHost().getNetLayer()
								.isOffline()) {
					/*
					 * We do not want to consider this neighbor as it is not
					 * there and a drift distance can not be computed.
					 */
					numOfNeighbors--;
					continue;
				}

				int[] observedPos = neighbor.getArea().getArea();
				Point observerdPosPoint = new Point(observedPos[1]
						- observedPos[0], observedPos[3] - observedPos[2]);

				int[] realPos = globalNodes.get(nId).getLocalContact()
						.getArea().getArea();
				Point realPosPoint = new Point(realPos[1] - realPos[0],
						realPos[3] - realPos[2]);

				double distance = observerdPosPoint.distance(realPosPoint.x,
						realPosPoint.y);

				if (distance < minDriftDistance)
					minDriftDistance = distance;

				if (distance > maxDriftDistance)
					maxDriftDistance = distance;

				localDriftDistanceSum += distance;

				driftDistances.add(new Double(distance));
			}
			numOfConsideredDistances += numOfNeighbors;

			if (numOfNeighbors != 0) {
				double localDriftDistanceAvg = localDriftDistanceSum
						/ numOfNeighbors;

				driftDistanceAvgSum += localDriftDistanceAvg;
			}
		}

		double driftDistanceAvg = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;
		double median = 0;

		if (numOfNodesToConsider != 0) {
			driftDistanceAvg = driftDistanceAvgSum / numOfNodesToConsider;

			median = computeMedian(driftDistances);

			/*
			 * Compute standard deviation
			 */
			double sumSquareDDMinusAvg = 0;
			if (driftDistances.size() > 0) {
				for (Double dd : driftDistances) {

					double ddMinusAvg = dd - driftDistanceAvg;
					sumSquareDDMinusAvg += ddMinusAvg * ddMinusAvg;
				}
				standardDeviation = Math.sqrt(sumSquareDDMinusAvg
						/ driftDistances.size());
			}

			Collections.sort(driftDistances);
			for (int i = 0; i < driftDistances.size(); i++) {
				Double d = driftDistances.get(i);

				if (d >= driftDistanceAvg) {
					List<Double> underAvg = driftDistances.subList(0, i);
					List<Double> overAvg = driftDistances.subList(i + 1,
							driftDistances.size());

					/*
					 * Compute standard deviation for values under and over the
					 * average separately.
					 */
					double sumOfSquares = 0;
					for (Double dUnder : underAvg) {
						sumOfSquares += (dUnder - driftDistanceAvg)
								* (dUnder - driftDistanceAvg);
					}
					if (underAvg.size() > 0)
						standardDeviationMinus = Math.sqrt(sumOfSquares
								/ underAvg.size());

					sumOfSquares = 0;
					for (Double dOver : overAvg) {
						sumOfSquares += (dOver - driftDistanceAvg)
								* (dOver - driftDistanceAvg);
					}
					if (overAvg.size() > 0)
						standardDeviationPlus = Math.sqrt(sumOfSquares
								/ overAvg.size());

					break;
				}

			}

		}

		else {
			maxDriftDistance = 0;
			minDriftDistance = 0;
		}

		standardDeviationMinus = driftDistanceAvg - standardDeviationMinus;
		standardDeviationPlus += driftDistanceAvg;

		double[] returnArr = { driftDistanceAvg, minDriftDistance,
				maxDriftDistance, standardDeviation, standardDeviationMinus,
				standardDeviationPlus, median };

		return returnArr;
	}

	/**
	 * Computes the stale contact ration.
	 * Therefore it measure the non valid neighbours and give
	 * the ration between the valid and the non valid. 
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		ratio between non valid and valid neighbours. max=1
	 */
	public static double computeStaleContactRatio(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		int numOfOverallNodes = globalNodes.size();
		int overallStaleNeighborSum = 0;
		int overallNeighborSum = 0;

		for (CanOverlayID id : globalNodes.keySet()) {
			CanNode node = globalNodes.get(id);

			if (node.getPeerStatus() != PeerStatus.PRESENT) {
				numOfOverallNodes--;
				continue;
			}

			if (node.getLocalContact() == null)
				continue;

			List<CanOverlayContact> neighbors = node.getNeighbours();

			if (neighbors.size() == 0)
				continue;

			overallNeighborSum += neighbors.size();

			for (CanOverlayContact neighbour : node.getNeighbours()) {
				for (CanOverlayID id2 : globalNodes.keySet()) {
					CanNode node2 = globalNodes.get(id2);

					if (neighbour.getOverlayID().toString().equals(
							node2.getLocalContact().getOverlayID().toString())
							&& (!neighbour.getArea().toString().equals(
									node2.getLocalContact().getArea()
											.toString()) 
							|| (node2
									.getPeerStatus() != PeerStatus.PRESENT))) {
						overallStaleNeighborSum++;
					}
				}
			}

		}
		double overallStaleNeighborRatio = 0;
		

		if (overallNeighborSum > 0)
			overallStaleNeighborSum = overallNeighborSum - overallStaleNeighborSum;
			overallStaleNeighborRatio = (double) overallStaleNeighborSum
					/ overallNeighborSum;

		return overallStaleNeighborRatio;
	}

	/**
	 * Gives the number of online peers.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 */
	public static double computeNumOfOnlinePeers(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		int numOfOnlinePeers = 0;

		for (CanNode node : globalNodes.values()) {

			if (node.getPeerStatus() == PeerStatus.PRESENT
					&& node.getHost().getNetLayer().isOnline()) {
				numOfOnlinePeers++;
			}
		}
		return numOfOnlinePeers;
	}

	/**
	 * Gives the number of leave, join and takeover messages.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public static double[] numberLeaveJoinTakeover(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			for (CanOverlayID id : globalNodes.keySet()) {
				CanNode node = globalNodes.get(id);
				outputPerMin += node.getDataOperation
						.getNumberLeaveJoinTakeover();
			}
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ CanConfig.intervallBetweenStatistics;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			output[0] = outputPerMin;
			output[1] = outputPerMinPerNode;
		}
		return output;

	}

	/**
	 * Gives the number of stabilize messages (ping and pong).
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public static double[] numberStabilizeMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			for (CanOverlayID id : globalNodes.keySet()) {
				CanNode node = globalNodes.get(id);
				outputPerMin += node.getDataOperation.getNumberStabilizeMsg();
			}
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ CanConfig.intervallBetweenStatistics;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			output[0] = outputPerMin;
			output[1] = outputPerMinPerNode;
		}
		return output;

	}

	/**
	 * Gives the number of lookup and store messages as well as 
	 * the replies.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public static double[] numberLookupStoreMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			for (CanOverlayID id : globalNodes.keySet()) {
				CanNode node = globalNodes.get(id);
				outputPerMin += node.getDataOperation.getNumberLookupStore();
			}
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ CanConfig.intervallBetweenStatistics;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			output[0] = outputPerMin;
			output[1] = outputPerMinPerNode;
		}
		return output;

	}

	public static void addReceivedMsg(NetMessage msg) {
		receivedBytes += msg.getSize();
	}

	/**
	 * Gives the number of all received bytes.
	 * Measured in the analyzer.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		double[] output = { outputPerMin, outputPerMinPerNode }
	 */
	public static double[] getReceivedBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		long outputPerMin = receivedBytes * Simulator.MINUTE_UNIT
				/ CanConfig.intervallBetweenStatistics;
		long outputPerMinPerNode = outputPerMin / globalNodes.size();
		receivedBytes = 0;

		double[] output = { outputPerMin, outputPerMinPerNode };
		return output;
	}

	public static void addSentMsg(NetMessage msg) {
		sentBytes += msg.getSize();
	}

	/**
	 * Gives the number of all send bytes
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		double[] output = { outputPerMin, outputPerMinPerNode }
	 */
	public static double[] getSentBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		long outputPerMin = sentBytes * Simulator.MINUTE_UNIT
				/ CanConfig.intervallBetweenStatistics;
		long outputPerMinPerNode = outputPerMin / globalNodes.size();
		sentBytes = 0;

		double[] output = { outputPerMin, outputPerMinPerNode };
		return output;
	}

	/**
	 * Gives the number of all received bytes.
	 * Measured in the every peer.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		double[] output = { outputPerMin, outputPerMinPerNode }
	 */
	public static double[] receivedBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			for (CanOverlayID id : globalNodes.keySet()) {
				CanNode node = globalNodes.get(id);
				outputPerMin += node.getDataOperation.getReceivedBytes();
			}
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ CanConfig.intervallBetweenStatistics;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			output[0] = outputPerMin;
			output[1] = outputPerMinPerNode;
		}
		return output;

	}

	/**
	 * Gives the number of needed of hops of all lookup messages
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {hopsAvg, median, standardDeviation, 
	 * 			standardDeviationMinus, standardDeviationPlus}
	 */
	public static double[] hops(LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0, 0, 0, 0 };

		double hopsAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> numberHops = new ArrayList<Double>();
			for (CanOverlayID id : globalNodes.keySet()) {
				List<Integer> hops = globalNodes.get(id).getDataOperation
						.getHopCount();
				for (int i = 0; i < hops.size(); i++) {
					numberHops.add((double) hops.get(i));
				}
			}

			if (numberHops.size() != 0) {
				double numberHopsSum = 0;
				for (int i = 0; i < numberHops.size(); i++) {
					numberHopsSum += (double) numberHops.get(i);
				}

				hopsAvg = numberHopsSum / (double) numberHops.size();

				median = computeMedian(numberHops);

				double[] deviation = computeStandardDeviation(numberHops,
						hopsAvg);
				output[0] = hopsAvg;
				output[1] = median;
				output[2] = deviation[0];
				output[3] = deviation[1];
				output[4] = deviation[2];

			} else {
				output[0] = hopsAvg;
				output[1] = median;
				output[2] = standardDeviation;
				output[3] = standardDeviationMinus;
				output[4] = standardDeviationPlus;
			}
		}

		return output;
	}

	/**
	 * Gives the time needed for a lookup to travel to the destination
	 * and back, for all lookups
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {hopsAvg, median, standardDeviation, 
	 * 			standardDeviationMinus, standardDeviationPlus}
	 */
	public static double[] transferTimeAvg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double[] output = { 0, 0, 0, 0, 0 };

		double hopsAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> timeHops = new ArrayList<Double>();
			for (CanOverlayID id : globalNodes.keySet()) {
				CanNode node = globalNodes.get(id);
				List<Long> hopTimes = node.getDataOperation.getTimeForHop();
				for (int i = 0; i < hopTimes.size(); i++) {
					timeHops
							.add((double) (hopTimes.get(i) / Simulator.MILLISECOND_UNIT));
				}
			}

			if (timeHops.size() != 0) {
				double timeHopsSum = 0;
				for (int i = 0; i < timeHops.size(); i++) {
					timeHopsSum += (double) timeHops.get(i);
				}

				hopsAvg = timeHopsSum / (double) timeHops.size();

				median = computeMedian(timeHops);

				double[] deviation = computeStandardDeviation(timeHops, hopsAvg);
				output[0] = hopsAvg;
				output[1] = median;
				output[2] = deviation[0];
				output[3] = deviation[1];
				output[4] = deviation[2];
			}
		} else {
			output[0] = hopsAvg;
			output[1] = median;
			output[2] = standardDeviation;
			output[3] = standardDeviationMinus;
			output[4] = standardDeviationPlus;
		}

		return output;

	}

	public static int sendLookupMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		int output = 0;

		if (globalNodes.size() != 0) {
			for (CanOverlayID id : globalNodes.keySet()) {
				output += globalNodes.get(id).getDataOperation
						.getStartedLookup();
			}
		}
		return output;
	}

	public static int receivedLookupMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		int output = 0;

		if (globalNodes.size() != 0) {
			for (CanOverlayID id : globalNodes.keySet()) {
				output += globalNodes.get(id).getDataOperation
						.getReceivedLookup();
			}
		}
		return output;
	}

	/**
	 * All lookups arrived in a peer.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		Map<Integer, CanOverlayContact> gives the number of received lookups
	 * 		in all contacts
	 */
	public static TreeMap<Integer, CanOverlayContact> getLookupRequest(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		TreeMap<Integer, CanOverlayContact> receivedLookupRequest = new TreeMap<Integer, CanOverlayContact>();

		if (globalNodes.size() != 0) {
			for (CanOverlayID id : globalNodes.keySet()) {
				if (globalNodes.get(id).getDataOperation
						.getReceivedLookupRequest() > 0)
					receivedLookupRequest.put(
							globalNodes.get(id).getDataOperation
									.getReceivedLookupRequest(), globalNodes
									.get(id).getLocalContact());
			}
		}
		return receivedLookupRequest;
	}

	/**
	 * Gives the number of times a hash value was looked up
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		LinkedHashMap<BigInteger, Integer> BigInteger=hash, 
	 * 		Integer=number of lookups
	 */
	public static LinkedHashMap<BigInteger, Integer> getLookupValues(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		LinkedHashMap<BigInteger, Integer> lookups = new LinkedHashMap<BigInteger, Integer>();

		for (CanOverlayID id : globalNodes.keySet()) {
			LinkedHashMap<BigInteger, Integer> lookupValues = globalNodes
					.get(id).getDataOperation.getLookupValues();

			for (BigInteger request : lookupValues.keySet()) {
				if (lookups.containsKey(request)) {
					int lookupsInt = lookups.get(request);
					lookups.remove(request);
					lookups
							.put(request, lookupsInt
									+ lookupValues.get(request));
					break;
				} else
					lookups.put(request, lookupValues.get(request));

			}

		}

		return lookups;
	}

	/**
	 * Gives the number of neighbours.
	 * 
	 * @param globalNodes
	 * 		all peers in the CAN
	 * @return
	 * 		output[] = {hopsAvg, median, standardDeviation, 
	 * 			standardDeviationMinus, standardDeviationPlus}
	 */
	public static double[] numberNeighbours(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] output = { 0, 0, 0, 0, 0, 0 };

		double neighboursAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;
		double withoutEdge = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> numberNeighbours = new ArrayList<Double>();
			ArrayList<Double> numberNeighboursWithoutEdge = new ArrayList<Double>();
			for (CanOverlayID id : globalNodes.keySet()) {
				numberNeighbours.add((double) globalNodes.get(id)
						.getNeighbours().size());

				if (globalNodes.get(id).getLocalContact().getArea().getArea()[0] != 0
						&& globalNodes.get(id).getLocalContact().getArea()
								.getArea()[1] != CanConfig.CanSize
						&& globalNodes.get(id).getLocalContact().getArea()
								.getArea()[2] != 0
						&& globalNodes.get(id).getLocalContact().getArea()
								.getArea()[3] != CanConfig.CanSize)
					numberNeighboursWithoutEdge.add((double) globalNodes
							.get(id).getNeighbours().size());
			}

			if (numberNeighbours.size() != 0) {
				double numberNeighboursSum = 0;
				double withoutEdgeSum = 0;
				for (int i = 0; i < numberNeighbours.size(); i++) {
					numberNeighboursSum += (double) numberNeighbours.get(i);
				}

				for (int i = 0; i < numberNeighboursWithoutEdge.size(); i++) {
					withoutEdgeSum += (double) numberNeighboursWithoutEdge
							.get(i);
				}

				neighboursAvg = numberNeighboursSum
						/ (double) numberNeighbours.size();

				withoutEdge = withoutEdgeSum
						/ (double) numberNeighboursWithoutEdge.size();

				median = computeMedian(numberNeighbours);

				double[] deviation = computeStandardDeviation(numberNeighbours,
						neighboursAvg);
				output[0] = neighboursAvg;
				output[1] = median;
				output[2] = deviation[0];
				output[3] = deviation[1];
				output[4] = deviation[2];
				output[5] = withoutEdge;

			} else {
				output[0] = neighboursAvg;
				output[1] = median;
				output[2] = standardDeviation;
				output[3] = standardDeviationMinus;
				output[4] = standardDeviationPlus;
				output[5] = withoutEdge;
			}
		}

		return output;
	}

	private static double[] computeStandardDeviation(ArrayList<Double> values,
			double average) {
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		/*
		 * Compute standard deviation
		 */
		double sumSquareDDMinusAvg = 0;
		if (values.size() > 0) {
			for (Double dd : values) {

				double ddMinusAvg = dd - average;
				sumSquareDDMinusAvg += ddMinusAvg * ddMinusAvg;
			}
			standardDeviation = Math.sqrt(sumSquareDDMinusAvg / values.size());
		}

		Collections.sort(values);
		for (int i = 0; i < values.size(); i++) {
			Double d = values.get(i);

			if (d >= average) {
				List<Double> underAvg = values.subList(0, i);
				List<Double> overAvg = values.subList(i + 1, values.size());

				/*
				 * Compute standard deviation for values under and over the
				 * average separately.
				 */
				double sumOfSquares = 0;
				for (Double dUnder : underAvg) {
					sumOfSquares += (dUnder - average) * (dUnder - average);
				}
				if (underAvg.size() > 0)
					standardDeviationMinus = Math.sqrt(sumOfSquares
							/ (underAvg.size() - 1));

				sumOfSquares = 0;
				for (Double dOver : overAvg) {
					sumOfSquares += (dOver - average) * (dOver - average);
				}
				if (overAvg.size() > 0)
					standardDeviationPlus = Math.sqrt(sumOfSquares
							/ (overAvg.size() - 1));

				break;
			}

		}
		standardDeviationMinus = average - standardDeviationMinus;
		standardDeviationPlus += average;
		double[] returnArr = { standardDeviation, standardDeviationMinus,
				standardDeviationPlus };

		return returnArr;
	}

	private static double computeMedian(List<Double> values) {
		Collections.sort(values);
		double median;
		try {
			median = values.get((int) Math.floor(values.size() / 2));
		} catch (Exception e) {
			median = 0;
		}

		return median;
	}
}
