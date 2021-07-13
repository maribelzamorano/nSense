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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Collects data about how long operations take to execute. The analysis can be
 * restricted by indicating a superclass for the operations to consider. The
 * average delay is computed for each implementation of an operation (=class)
 * separately.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class OperationDelayAnalyzer implements OperationAnalyzer {

	private final static Logger log = SimLogger
			.getLogger(OperationDelayAnalyzer.class);

	/**
	 * The initial capacity of the HashMap that contains the currently running
	 * (and analysed) operations.
	 */
	private static final int RUNNING_OPERATIONS_STARTING_CAPACITY = 50;

	/** Whether the analysis has begun. */
	private boolean started = false;

	/** The supertype of all measured Operations. */
	private Class<? extends Operation<?>> superOperation;

	/**
	 * A Map that maps the IDs of Operations to the point in time at which they
	 * have been started.
	 */
	private Map<Integer, Long> startedOperationIDs;

	/**
	 * A Map that stores the current average for each class/type of Operation.
	 */
	private Map<Class<? extends Operation<?>>, AvgAccumulator> runningAverages;

	/**
	 * Constructs a new OperationDelayAnalyser.
	 */
	public OperationDelayAnalyzer() {
		setOperationSupertype(Operation.class);
	}

	/**
	 * Sets a supertype for Operations that are considered in this measurement.
	 * Data will be collected only about Operations that fulfill the
	 * <code>"instanceof superOperationClass"</code> test. Events triggered by
	 * other operations (those that are not a subtype of
	 * <code>superOperationClass</code>) are ignored. If no specific type is
	 * set, the default value (any subtype of Operation will be analysed) will
	 * be used. This method has no effect if start() has already been called.
	 * 
	 * @param <C>
	 *            the type of the <code>superOperation</code> class object.
	 * @param superOperationClass
	 *            a Class object, parameterised with the type of Operation that
	 *            all Operations that will be analysed have to be a subtype of.
	 */
	public final <C extends Operation<?>> void setOperationSupertype(
			final Class<C> superOperationClass) {
		if (!started) {
			superOperation = superOperationClass;
		}
	}

	/**
	 * Sets the supertype of all Operations that will be analysed in this
	 * OperationDelayAnalyser. This method is a convenience method that permits
	 * to set the class name as a String as used in input XML-files. It is
	 * equivalent to {@link #setOperationSupertype(Class)} except that this
	 * method allows to pass the supertype as a String. If this method is not
	 * called, the default of "de.tud.kom.p2psim.api.common.Operation" will be
	 * used.
	 * <p>
	 * If the given class does not exist or is not a subtype of Operation, an
	 * exception will be thrown.
	 * 
	 * @param superOperationClassName
	 *            the class name of the Operation subtype that will be a
	 *            supertype of all Operations analysed here.
	 * @throws ClassNotFoundException
	 *             if the given class does not exist.
	 * @throws ClassCastException
	 *             if the given class name is not a subtype of Operation.
	 */
	@SuppressWarnings("unchecked")
	public final void setRestrictToSubtypesOf(
			final String superOperationClassName) throws ClassNotFoundException {
		Class<? extends Operation<?>> clazz = (Class<? extends Operation<?>>) Class
				.forName(superOperationClassName);
		setOperationSupertype(clazz);
	}

	/**
	 * Called to start the operation latency analysis. Operations that have been
	 * started prior to invoking this method are not taken into account for
	 * analysis (even if they complete after start() has been invoked). Calling
	 * this method starts a <i>fresh</i> measurement. The supertype of
	 * Operations to consider in the measurement has to be set before invoking
	 * this method.
	 */
	@Override
	public final void start() {
		if (!started) {
			started = true;
			startedOperationIDs = new HashMap<Integer, Long>(
					RUNNING_OPERATIONS_STARTING_CAPACITY);
			runningAverages = new HashMap<Class<? extends Operation<?>>, AvgAccumulator>();
		}
	}

	/**
	 * This method should be called whenever an operation has been triggered.
	 * (It is assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has been triggered.
	 */
	@Override
	public final void operationInitiated(final Operation<?> op) {
		if (!started) {
			return;
		}
		// check type of Operation
		if (superOperation.isInstance(op)) {
			startedOperationIDs.put(op.getOperationID(), Simulator
					.getCurrentTime());
		}
	}

	/**
	 * This method should be called whenever an operation has completed. (It is
	 * assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has completed.
	 */
	@Override
	public final void operationFinished(final Operation<?> op) {
		if (!started) {
			return;
		}
		final Long startingTime = startedOperationIDs.remove(op
				.getOperationID());
		if (startingTime == null) {
			// operation unknown
			return;
		}
		final Long duration = Simulator.getCurrentTime() - startingTime;
		AvgAccumulator avg = runningAverages.get(op.getClass());
		if (avg == null) {
			// first operation of that type
			avg = new AvgAccumulator();
			putAvgAcc(op.getClass(), avg);
		}
		avg.addToTotal(duration);
	}

	// necessary because of generics (?)
	private <C extends Operation<?>> void putAvgAcc(Class<C> cl,
			AvgAccumulator acc) {
		runningAverages.put(cl, acc);
	}

	/**
	 * Called to stop the operation latency analysis.
	 * 
	 * @param output
	 *            a Writer on which a textual representation of the measurements
	 *            will be written.
	 */
	@Override
	public final void stop(final Writer output) {
		long sum = 0;

		if (!started) {
			return;
		}
		started = false;

		try {
			output.write("\n******************** ");
			output.write("Operation Duration Statistics ");
			output.write("********************\n");
			output
					.write(" \t\tLATENCY (sec)\t\t(sim. units)\t\tCOUNT\tOPERATION TYPE\n");

			for (Map.Entry<Class<? extends Operation<?>>, AvgAccumulator> entry : runningAverages
					.entrySet()) {
				output.write(String.format(
						" AVERAGE: \t%1$15.14g \t%2$15.14g \t%3$d \t%4$s\n",
						entry.getValue().getAverage() / Simulator.SECOND_UNIT,
						entry.getValue().getAverage(), entry.getValue()
								.getCount(), entry.getKey().getName()));
				output.write(String.format(" MIN: \t\t%1$15.14g \t%2$15.14g\n",
						entry.getValue().getMin() / Simulator.SECOND_UNIT,
						entry.getValue().getMin()));
				output.write(String.format(" MAX: \t\t%1$15.14g \t%2$15.14g\n",
						entry.getValue().getMax() / Simulator.SECOND_UNIT,
						entry.getValue().getMax()));
				sum += entry.getValue().getCount();
			}

			output.write(" (" + sum + " operations analysed; further "
					+ startedOperationIDs.size()
					+ " operations are still running)\n");
			output
					.write("****************** Operation Duration Statistics End ******************\n");
			output.flush();
		} catch (IOException ex) {
			log
					.error(
							"Operation delay measurement could not be written to output.",
							ex);
		}
	}

	/**
	 * @return a Map that contains all encountered Operation types as keys and
	 *         the average duration of their respective execution as values.
	 */
	public final Map<Class<? extends Operation<?>>, Double> getAvgOperationLatencies() {
		final Map<Class<? extends Operation<?>>, Double> result;
		result = new HashMap<Class<? extends Operation<?>>, Double>(
				runningAverages.size(), 1.0f);
		if (runningAverages == null) {
			return result; // empty
		}

		for (final Map.Entry<Class<? extends Operation<?>>, AvgAccumulator> entry : runningAverages
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getAverage());
		}

		return result;
	}

}
