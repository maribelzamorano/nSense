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


package de.tud.kom.p2psim.impl.skynet.analyzing.analyzers;

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.analyzing.AbstractSkyNetAnalyzer;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements {@link OperationAnalyzer} and is therefore responsible
 * of monitoring the started and finished operations. Besides this
 * monitoring-functionality, <code>OPAnalyzer</code> allows every host to inform
 * itself about the completed operations, that it started. This information
 * comprises the number of finished operations, the duration to finish the
 * operations as well as the information if the operation was successfully
 * finished or not.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class OPAnalyzer extends AbstractSkyNetAnalyzer implements
		OperationAnalyzer {

	private static Logger log = SimLogger.getLogger(OPAnalyzer.class);

	private HashMap<Integer, Long> startedOperations;

	private HashMap<Long, Vector<OPAnalyzerEntry>> completedOperations;

	// private HashMap<Long, Long> succeededOperations;

	// private HashMap<Long, Long> failedOperations;

	private long succeededOps;

	private long failedOps;

	private static IPv4NetID serverNetId;

	public OPAnalyzer() {
		super();
		succeededOps = 0;
		failedOps = 0;
	}

	@Override
	protected void initialize() {
		// not needed within this analyzer
	}

	@Override
	protected void finish() {
		log.fatal("Unfinished Operations:" + startedOperations.size());
		/*
		 * Iterator<Long> iter = succeededOperations.keySet().iterator(); long
		 * counter = 0; while (iter.hasNext()) { counter = counter +
		 * succeededOperations.get(iter.next()); }
		 */
		log.fatal("Succeeded Operations: " + succeededOps);
		log.fatal("Failed Operations: " + failedOps);
	}

	public void operationFinished(Operation<?> op) {
		if (runningAnalyzer) {
			long time = Simulator.getCurrentTime();
			Long startTime = startedOperations.remove(op.getOperationID());

			if (startTime == null)
				return;

			IPv4NetID ip = (IPv4NetID) op.getComponent().getHost()
					.getNetLayer().getNetID();
			if (compareIPWithServerIP(ip)) {
				if (op.isSuccessful()) {
					if (completedOperations.containsKey(ip.getID())) {
						Vector<OPAnalyzerEntry> vec = completedOperations
								.remove(ip.getID());
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										true));
						completedOperations.put(ip.getID(), vec);
					} else {
						Vector<OPAnalyzerEntry> vec = new Vector<OPAnalyzerEntry>();
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										true));
						completedOperations.put(ip.getID(), vec);
					}
					/*
					 * Long temp = succeededOperations.remove(ip.getID()); if
					 * (temp == null) { succeededOperations.put(ip.getID(), 1l);
					 * } else { succeededOperations.put(ip.getID(),
					 * temp.longValue() + 1); }
					 */
					succeededOps++;
				} else {
					if (completedOperations.containsKey(ip.getID())) {
						Vector<OPAnalyzerEntry> vec = completedOperations
								.remove(ip.getID());
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										false));
						completedOperations.put(ip.getID(), vec);
					} else {
						Vector<OPAnalyzerEntry> vec = new Vector<OPAnalyzerEntry>();
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										false));
						completedOperations.put(ip.getID(), vec);
					}
					/*
					 * Long temp = failedOperations.remove(ip.getID()); if (temp
					 * == null) { failedOperations.put(ip.getID(), 1l); } else {
					 * failedOperations.put(ip.getID(), temp.longValue() + 1); }
					 */
					failedOps++;
				}
			} else {
				log.warn("ServerIP-hit");
			}
		}
	}

	public void operationInitiated(Operation<?> op) {
		if (runningAnalyzer) {
			if (startedOperations.containsKey(op.getOperationID())) {
				log.fatal("Should not happen");
			} else {
				startedOperations.put(op.getOperationID(), Simulator
						.getCurrentTime());
			}
		}

	}

	// ----------------------------------------------------------------------
	// methods for getting the collected data of this analyzer
	// ----------------------------------------------------------------------

	/**
	 * This method returns all finished operations, which the specified host
	 * started during a predefined interval.
	 * 
	 * @param id
	 *            contains the {@link NetID} of the host
	 * @return a <code>Vector</code> with all finished operations
	 */
	public Vector<OPAnalyzerEntry> getCompletedOperations(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		return completedOperations.remove(ip.getID());
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);
		startedOperations = new HashMap<Integer, Long>();
		completedOperations = new HashMap<Long, Vector<OPAnalyzerEntry>>(
				(int) capacity);
		// succeededOperations = new HashMap<Long, Long>((int) capacity);
		// failedOperations = new HashMap<Long, Long>((int) capacity);
	}

	public static void setServerNetId(IPv4NetID serverID) {
		serverNetId = serverID;
	}

	private boolean compareIPWithServerIP(NetID id) {
		if (serverNetId != null) {
			if (((IPv4NetID) id).getID() != serverNetId.getID()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		// not needed within this analyzer
	}

}
