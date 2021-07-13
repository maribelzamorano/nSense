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
 */
package de.tu.darmstadt.dvs.nSense.overlay.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

import de.tu.darmstadt.dvs.nSense.application.IDONodeNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * @author Christoph Muenker
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class SinglePointMovementAnalyzer implements Analyzer,
		SimulationEventHandler {

	private Writer statWriter;

	private static String DISTANCE_SINGLE_POINT = "distance_single_point";

	/**
	 * The measurement interval
	 */
	private long measurementInterval = -1;

	/**
	 * Flag for the analyzer for activity.
	 */
	private boolean active = false;

	/**
	 * coordinates for the single point for Movement
	 */
	private VectorN point;

	//
	// Setter for Configuration
	//

	public void setMeasurementInterval(long interval) {
		this.measurementInterval = interval;
	}

	public void setPoint(VectorN vector) {
		this.point = vector;
	}

	//
	// For Collecting data and write out
	//

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (active) {

			writeDistanceToMiddlePoint();

			/*
			 * Schedule new STATUS event
			 */
			long scheduleAtTime = Simulator.getCurrentTime()
					+ measurementInterval;
			Simulator.scheduleEvent(this, scheduleAtTime, this, null);
		}
	}

	private void writeDistanceToMiddlePoint() {
		for (int ii = 0; ii < point.getSize(); ii++) {
			if (point.getValue(ii) == -1) {
				throw new RuntimeException("point " + ii + " is not set!");
			}
		}

		VectorN singlePoint = point;
		LinkedHashMap<Host, IDONodeNDim> onlineNodes = getAllOnlineNodes();
		long time = Simulator.getCurrentTime();

		String dirName = Constants.OUTPUTS_DIR;
		File statDir = new File(dirName);
		if (!statDir.exists() || !statDir.isDirectory()) {
			statDir.mkdirs();
		}

		File statFile = new File(dirName
				+ "SinglePointMovementAnalyzerStats.dat");

		for (Host host : onlineNodes.keySet()) {
			IDONodeNDim node = onlineNodes.get(host);
			double dist = singlePoint.distance(node.getPosition());

			try {
				statWriter = new BufferedWriter(new FileWriter(statFile));

				statWriter.write(StatisticGenerationEvent.hostIDs.get(host)
						+ "/t" + time + "/t" + DISTANCE_SINGLE_POINT + "/t"
						+ dist + "/n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets {@link IDONode}s back, that think, they are connected with the
	 * Overlay.
	 * 
	 * @return A map of {@link IDONode}s.
	 */
	private static LinkedHashMap<Host, IDONodeNDim> getAllOnlineNodes() {

		LinkedHashMap<Host, IDONodeNDim> result = new LinkedHashMap<Host, IDONodeNDim>();
		for (Host h : getAllNodes().keySet()) {
			OverlayNode node = h.getOverlay(IDONodeNDim.class);

			if (node != null) {
				IDONodeNDim idoNode = (IDONodeNDim) node;
				if (idoNode.isPresent()) {
					result.put(h, idoNode);
				}
			}
		}
		return result;
	}

	/**
	 * Gets a map of all {@link IDONode}s, which are created in this simulation.
	 * 
	 * @return A map of {@link IDONode}.
	 */
	private static LinkedHashMap<Host, IDONodeNDim> getAllNodes() {

		LinkedHashMap<Host, IDONodeNDim> result = new LinkedHashMap<Host, IDONodeNDim>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode node = h.getOverlay(IDONodeNDim.class);

			if (node != null) {
				IDONodeNDim idoNode = (IDONodeNDim) node;
				if (!StatisticGenerationEvent.hostIDs.containsKey(h)) {
					StatisticGenerationEvent.hostIDs.put(
							idoNode.getOverlayID(),
							StatisticGenerationEvent.hostIDs.size());
				}

				result.put(h, idoNode);
			}
		}
		return result;
	}

	@Override
	public void start() {
		if (measurementInterval == -1) {
			throw new RuntimeException("measurementInterval is not set!");
		}

		active = true;
		// schedule immediatly
		Simulator.scheduleEvent(this, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		active = false;
		try {
			statWriter.flush();
			statWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
