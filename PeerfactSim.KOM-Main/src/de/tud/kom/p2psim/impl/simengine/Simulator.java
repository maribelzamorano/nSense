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


package de.tud.kom.p2psim.impl.simengine;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Monitor;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.api.scenario.Configurator;
import de.tud.kom.p2psim.api.scenario.Scenario;
import de.tud.kom.p2psim.api.scenario.ScenarioFactory;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.common.DefaultMonitor;
import de.tud.kom.p2psim.impl.scenario.DefaultConfigurator;
import de.tud.kom.p2psim.impl.skynet.SkyNetBatchSimulator;
import de.tud.kom.p2psim.impl.skynet.analyzing.writers.AttributeWriter;
import de.tud.kom.p2psim.impl.skynet.analyzing.writers.MetricsWriter;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Concrete implementation of a simulator which can be used to run a simulation
 * by calling the main method in the SimulatorRunner class.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public class Simulator implements Configurable {

	private final static Logger log = SimLogger.getLogger(Simulator.class);

	/**
	 * These constant should be ALWAYS used for virtual time calculations.
	 */
	public final static long MICROSECOND_UNIT = 1l;

	/**
	 * These constant should be ALWAYS used for virtual time calculations.
	 */
	public final static long MILLISECOND_UNIT = 1000l * MICROSECOND_UNIT;

	/**
	 * These constant should be ALWAYS used for virtual time calculations.
	 */
	public final static long SECOND_UNIT = 1000l * MILLISECOND_UNIT;

	/**
	 * These constant should be ALWAYS used for virtual time calculations.
	 */
	public final static long MINUTE_UNIT = 60l * SECOND_UNIT;

	/**
	 * These constant should be ALWAYS used for virtual time calculations.
	 */
	public final static long HOUR_UNIT = 60l * MINUTE_UNIT;

	/**
	 * Scenario holding all the information about the current simulation run.
	 */
	private Scenario scenario;

	/**
	 * Singleton instance of default simulator.
	 */
	private static Simulator singleton;

	/**
	 * Configurator instance is used to initialize the scenario.
	 */
	private DefaultConfigurator defaultConfigurator;

	private boolean running;

	private static long seed;

	private static Scheduler scheduler;

	private static RandomGenerator randomGen = new JDKRandomGenerator();

	private static Monitor monitor;

	private static boolean finishedWithoutError = false;

	/**
	 * This class is singleton, so use getInstance() method to obtain a
	 * reference to it.
	 * 
	 */
	private Simulator() {
		singleton = this;
		scheduler = new Scheduler(true);
		monitor = new DefaultMonitor();
	}

	/**
	 * Returns the single instance of the SimulationFramework
	 * 
	 * @return the SimulationFramework
	 */
	public static Simulator getInstance() {
		if (singleton == null)
			singleton = new Simulator();
		return singleton;
	}

	/**
	 * Set the scenario (protocol stack, network topology etc.) which will be
	 * used to run the simulation.
	 * 
	 * @param scenario
	 *            simulation scenario to be used
	 */
	public void setScenario(Scenario scenario) {
		checkRunning();
		this.scenario = scenario;
	}

	/**
	 * Returns the scenario used to run the simulation.
	 * 
	 * @return
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * This method will run the simulation using the previously set scenario
	 * data.
	 * 
	 */
	public void start(boolean throwExceptions) {
		checkRunning();
		log.info("Prepare Scenario ...");
		this.scenario.prepare();

		log.info("Running Scenario with seed=" + getSeed());
		long startTime = System.currentTimeMillis();

		log.info("Simulation started...");
		this.running = true;
		Exception reason = null;
		try {

			scheduler.start();
			finishedWithoutError = true;

		} catch (RuntimeException e) {
			finishedWithoutError = false;
			if (throwExceptions) {
				throw e;
			} else {
				log.error("Simulator run stopped because of error", e);
				reason = e;
			}
		} finally {
			this.running = false;
			// After a simulation start the mechanisms, which
			// finalize a simulation
			shutdownSimulation(reason, startTime);
		}
	}

	private void shutdownSimulation(Exception reason, long startTime) {
		this.running = false;
		if (finishedWithoutError) {
			log.info("Simulation successfully finished.");
		} else {
			log.error("Simulation finished with the error = " + reason);
		}
		long runTime = System.currentTimeMillis() - startTime;
		long minutes = (long) Math.floor((runTime) / 60000);
		long secs = (runTime % 60000) / 1000;
		log.info("Realtime Duration of experiment (m:s) " + minutes + ":"
				+ secs);
		log.info("Simulated time is " + getSimulatedRealtime());
		if (AttributeWriter.hasInstance()) {
			AttributeWriter.getInstance().closeWriter();
		}
		if (MetricsWriter.hasInstance()) {
			MetricsWriter.getInstance().closeWriter();
		}
		if (SkyNetBatchSimulator.hasInstance()) {
			SkyNetBatchSimulator.getInstance().finish(finishedWithoutError);
		}
	}

	/**
	 * Configure simulation from an XML file.
	 * 
	 * @param configFile
	 *            XML file with the configuration data.
	 * @param variables
	 *            the variables which are specified in the XML file with the
	 *            configuarion data.
	 */
	public void configure(String configFile, Map<String, String> variables) {
		// TODO create a class, that contains general informations of the
		// simulation, which can be accessed from every component during a
		// simulation. This can be seen as an alternative to implementing the
		// Composable interface
		this.defaultConfigurator = new DefaultConfigurator(configFile);
		defaultConfigurator.setVariables(variables);
		this.defaultConfigurator.register(Configurator.CORE, this);
		Collection<Configurable> components = this.defaultConfigurator
				.configureAll();
		log.debug("components " + components);
		ScenarioFactory scenarioBuilder = (ScenarioFactory) this.defaultConfigurator
				.getConfigurable(Configurator.SCENARIO_TAG);
		if (scenarioBuilder == null)
			throw new ConfigurationException(
					"No scenario builder specified in the configuration file. Nothing to do.");
		Scenario scenario = scenarioBuilder.createScenario();
		setScenario(scenario);

	}

	/**
	 * Returns the seed used within a simulation run.
	 * 
	 * @return the predefined seed
	 * 
	 */
	public static long getSeed() {
		return seed;
	}

	/**
	 * This method sets the seed of the global random generator which can be
	 * obtained using the static getRandom()-method.
	 * 
	 * @param seed
	 *            the seed to configure the global random generator
	 */
	public void setSeed(long seed) {
		checkRunning();
		this.seed = seed;
		randomGen.setSeed(seed);
	}

	/**
	 * Returns the global monitor which can be used to delegate
	 * information/occured events to a specific analyzer/s.
	 * 
	 * @return the global monitor
	 */
	public static Monitor getMonitor() {
		if (monitor == null)
			monitor = new DefaultMonitor();
		return monitor;
	}

	/**
	 * Assure that the set methods are not called after the simulation has
	 * started.
	 */
	private void checkRunning() {
		if (this.running)
			throw new IllegalStateException("Simulator is already running.");
	}

	/**
	 * This method provides the global random generator which has to be used
	 * within the simulation framework to generate reproducable results
	 * depending on a predefined seed.
	 * 
	 * @return the global random generator with a predefined seed.
	 */
	public static RandomGenerator getRandom() {
		return randomGen;
	}

	/**
	 * Sets the monitor which has to be configured by using the XML file with
	 * the configuration data.
	 * 
	 * @param monitor
	 *            monitor predefined in the XML file with the configuration
	 *            data.
	 */
	public void setMonitor(Monitor monitor) {
		checkRunning();
		this.monitor = monitor;
	}

	/**
	 * Returns the current simulation unit value.
	 * 
	 * @return the current simulation unit value
	 */
	public static long getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	/**
	 * Returns the end time of the simulation.
	 * 
	 * @return
	 */
	public static long getEndTime() {
		return scheduler.getEndTime();
	}

	/**
	 * Returns the simulated realtime in the format (Hours:Minutes:Seconds).
	 * 
	 * @return the simulated realtime
	 */
	public static String getSimulatedRealtime() {
		return scheduler.getSimulatedRealtime();
	}

	/**
	 * Inserts new event in queue
	 * 
	 * @param content
	 *            the content of the event
	 * @param simulationTime
	 *            time to schedule the event
	 * @param handler
	 *            handler which will receive this event
	 * @param eventType
	 *            specific simulation event
	 */
	public static void scheduleEvent(Object content, long simulationTime,
			SimulationEventHandler handler, SimulationEvent.Type eventType) {
		scheduler.scheduleEvent(content, simulationTime, handler, eventType);
	}

	/**
	 * Reset the simulator, so that it can be configured again for another
	 * simulation run without to restart the Java Virtual Machine. This is
	 * especially usefull for JUnit tests.
	 * 
	 */
	void reset() {
		checkRunning();
		monitor = new DefaultMonitor();
		scenario = null;
		scheduler = new Scheduler(true);
		seed = 0;
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Sets the end time at which the simulation framework will finish at the
	 * latest the simulation , irrespective if there are still unprocessed
	 * events in the event queue.
	 * 
	 * @param endTime
	 *            point in time at which the simular will finish at the latest
	 */
	public void setFinishAt(long endTime) {
		checkRunning();
		this.scheduler.setFinishAt(endTime);
	}

	static boolean isFinishedWithoutError() {
		return finishedWithoutError;
	}

	/**
	 * Can be used to format the absolute simulation time (current, past or
	 * future) into human-readable format: (h:m:s:ms).
	 * 
	 * @param time
	 *            - absolute simulation time like the one obtained via
	 *            getCurrentTime();
	 * @return human-readable representation of the given simulation time
	 */
	public static String getFormattedTime(long time) {
		return scheduler.getFormattedTime(time);
	}

	/**
	 * Specifies how often the scheduler will printout the current simulation
	 * time.
	 * 
	 * @param time
	 */
	public void setStatusInterval(long time) {
		scheduler.setStatusInterval(time);
	}

	public void setRealTime(boolean realTime) {
		scheduler.setRealTime(realTime);
	}

	public void setTimeSkew(double timeSkew) {
		scheduler.setTimeSkew(timeSkew);
	}

	public DefaultConfigurator getConfigurator() {
		return defaultConfigurator;
	}

}
