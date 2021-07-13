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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.api.service.skynet.QueryAnalyzerInterface;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetMonitor;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.analyzing.AbstractSkyNetAnalyzer;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.postProcessing.QueryPostProcessor;
import de.tud.kom.p2psim.impl.skynet.queries.Query;
import de.tud.kom.p2psim.impl.skynet.queries.QueryAddend;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements <code>QueryAnalyzerInterface</code> and is used to
 * gather all collected information in one sink. To avoid, that the collected
 * data consumes too much memory of the PC, the data is written to files through
 * serialization. <code>DATA_PATH</code> defines the path for the files, while
 * <code>writeIntervall</code> determines the interval between the periodical
 * serialization. For the serialization of the data, we utilize the following
 * order of writing objects:<br>
 * <li><code>startedQueries</code> <li><code>failedQueries</code> <li>
 * <code>unsolvedQueries</code> <li><code>solvedQueries</code><br>
 * It is important to maintain this order, as changing this order would cause
 * the corresponding post-processing-class {@link QueryPostProcessor} to throw
 * an exception.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class QueryAnalyzer extends AbstractSkyNetAnalyzer implements
		Configurable, QueryAnalyzerInterface {

	private static Logger log = SimLogger.getLogger(QueryAnalyzer.class);

	private static String DATA_PATH = Constants.TMP_DIR + File.separator
			+ "queryData";

	private long writeIntervall = Simulator.MINUTE_UNIT * 5;

	private static QueryAnalyzer analyzer;

	private ChurnStatisticsAnalyzer csAnalyzer;

	// collections for storing the queries
	private HashMap<BigDecimal, HashMap<Integer, Query>> startedQueries;

	private int numberOfStartedQueries;

	private HashMap<BigDecimal, HashMap<Integer, Query>> failedQueries;

	private int numberOfFailedQueries;

	private HashMap<BigDecimal, HashMap<Integer, Query>> unsolvedQueries;

	private int numberOfUnsolvedQueries;

	private HashMap<BigDecimal, HashMap<Integer, Query>> solvedQueries;

	private int numberOfSolvedQueries;

	public static QueryAnalyzer getInstance() {
		return analyzer;
	}

	public QueryAnalyzer() {
		super();
		analyzer = this;
		SkyNetMonitor monitor = (SkyNetMonitor) Simulator.getMonitor();
		csAnalyzer = (ChurnStatisticsAnalyzer) monitor
				.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.MONITOR_START)) {
			start();
		} else if (se.getType().equals(SimulationEvent.Type.MONITOR_STOP)) {
			stop(null);
		} else if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			// writing down the data-Maps
			long time = Simulator.getCurrentTime();
			long delta = System.currentTimeMillis();
			File f = new File(DATA_PATH + File.separatorChar + "temp-"
					+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(f));
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Started to write the query-maps");
				oos.writeObject(startedQueries);
				oos.writeObject(failedQueries);
				oos.writeObject(unsolvedQueries);
				oos.writeObject(solvedQueries);
				oos.close();
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Finished to write the query-maps in "
						+ (System.currentTimeMillis() - delta) + "ms");
				startedQueries.clear();
				failedQueries.clear();
				unsolvedQueries.clear();
				solvedQueries.clear();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Simulator.scheduleEvent(null, writeIntervall + time, this,
					SimulationEvent.Type.STATUS);
		}
	}

	public void queryStarted(Query query) {
		if (runningAnalyzer) {
			numberOfStartedQueries++;
			HashMap<Integer, Query> queries = startedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new HashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			startedQueries.put(
					query.getQueryOriginator().getSkyNetID().getID(), queries);
		}
	}

	public void queryLost(Query query) {
		if (runningAnalyzer) {
			numberOfFailedQueries++;
			HashMap<Integer, Query> queries = failedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new HashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			failedQueries.put(query.getQueryOriginator().getSkyNetID().getID(),
					queries);
		}
	}

	public void unsolvedQueryReceived(Query query) {
		if (runningAnalyzer) {
			numberOfUnsolvedQueries++;
			HashMap<Integer, Query> queries = unsolvedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new HashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			unsolvedQueries.put(query.getQueryOriginator().getSkyNetID()
					.getID(), queries);
		}
	}

	public void solvedQueryReceived(Query query) {
		if (runningAnalyzer) {
			numberOfSolvedQueries++;
			float quality = determineAnswerQuality(query);
			query.setAnswerQuality(quality);
			HashMap<Integer, Query> queries = solvedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new HashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			solvedQueries.put(query.getQueryOriginator().getSkyNetID().getID(),
					queries);
		}
	}

	private float determineAnswerQuality(Query query) {
		QueryAddend addend = query.getAddend(query.getIndexOfSolvedAddend());
		Vector<SkyNetNodeInfo> matches = addend.getMatches();
		int onlineMatches = 0;
		SkyNetNodeInfo node = null;
		for (int i = 0; i < matches.size(); i++) {
			node = matches.get(i);
			if (csAnalyzer.isPeerPresent(node.getTransInfo().getNetId())) {
				onlineMatches++;
			}
		}
		return ((float) onlineMatches) / ((float) matches.size());
	}

	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_START);
	}

	@Override
	protected void initialize() {
		initWriteDirectory(DATA_PATH, true);
		Simulator
				.scheduleEvent(null, writeIntervall
						+ Simulator.getCurrentTime(), this,
						SimulationEvent.Type.STATUS);
	}

	public void setStop(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_STOP);
	}

	@Override
	protected void finish() {
		log.fatal("Started queries = " + numberOfStartedQueries);
		log.fatal("Failed queries = " + numberOfFailedQueries);
		log.fatal("Unsolved queries = " + numberOfUnsolvedQueries);
		log.fatal("Solved queries = " + numberOfSolvedQueries);
		// writing down the data-Maps
		long time = Simulator.getCurrentTime();
		long delta = System.currentTimeMillis();
		File f = new File(DATA_PATH + File.separatorChar + "temp-"
				+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");

		File stats = new File(DATA_PATH + File.separatorChar + "stats.dat");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Started to write the query-maps");
			oos.writeObject(startedQueries);
			oos.writeObject(failedQueries);
			oos.writeObject(unsolvedQueries);
			oos.writeObject(solvedQueries);
			oos.close();
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Finished to write the query-maps in "
					+ (System.currentTimeMillis() - delta) + "ms");
			startedQueries.clear();
			failedQueries.clear();
			unsolvedQueries.clear();
			solvedQueries.clear();

			oos = new ObjectOutputStream(new FileOutputStream(stats));
			oos.writeInt(numberOfStartedQueries);
			oos.writeInt(numberOfFailedQueries);
			oos.writeInt(numberOfUnsolvedQueries);
			oos.writeInt(numberOfSolvedQueries);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);
		startedQueries = new HashMap<BigDecimal, HashMap<Integer, Query>>(
				(int) capacity);
		numberOfStartedQueries = 0;
		failedQueries = new HashMap<BigDecimal, HashMap<Integer, Query>>(
				(int) capacity);
		numberOfFailedQueries = 0;
		unsolvedQueries = new HashMap<BigDecimal, HashMap<Integer, Query>>(
				(int) capacity);
		numberOfUnsolvedQueries = 0;
		solvedQueries = new HashMap<BigDecimal, HashMap<Integer, Query>>(
				(int) capacity);
		numberOfSolvedQueries = 0;
	}

}
