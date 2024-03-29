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



package de.tud.kom.p2psim.impl.scenario;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.api.scenario.Scenario;
import de.tud.kom.p2psim.api.scenario.ScenarioAction;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Concrete CSV (Comma Separated Values) based implementation of the scenario
 * interface.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 13.12.2007
 * 
 */
class ExtendedScenario implements Scenario, Configurable {
	private final static Logger log = SimLogger
			.getLogger(ExtendedScenario.class);

	/**
	 * contains the created hosts, indexed by their IDs, which start at 1
	 */
	Map<String, List<Host>> groups;

	Class componentClass;

	List<ExtendedScenarioAction> actions;

	Map<Class, Parser> paramParsers;

	private List<Class> registeredComponentClasses;

	ExtendedScenario(Class defaultCompClass, List<Class> additionalCompClasses,
			List<Parser> paramParser) {
		super();
		actions = new LinkedList<ExtendedScenarioAction>();
		paramParsers = new HashMap<Class, Parser>();
		this.componentClass = defaultCompClass;
		log.info("ComponentClass is " + defaultCompClass);
		if (defaultCompClass == null)
			throw new IllegalArgumentException("Component class was not set!");
		registeredComponentClasses = new LinkedList<Class>();
		registeredComponentClasses.add(defaultCompClass);
		for (Parser parser : paramParser) {
			this.paramParsers.put(parser.getType(), parser);
		}
		registeredComponentClasses.addAll(additionalCompClasses);
	}

	/**
	 * Initialize the scenario with the available hosts. Must be called
	 * <b>before</b> the actions can be parsed.
	 * 
	 * @param groups
	 *            map of groupID->hostGroup mappings
	 */
	public void setHosts(Map<String, List<Host>> groups) {
		this.groups = groups;
		List<Host> hosts = new LinkedList<Host>();
		for (List<Host> group : groups.values()) {
			hosts.addAll(group);
		}
		log.info("Total number: " + hosts.size() + " hosts in " + groups.size()
				+ " groups");
	}

	void createAction(Host host, Class componentClass, long time,
			String methodName, String[] paramStrings) {
		Component target = host.getComponent(componentClass);
		if (target == null)
			throw new IllegalStateException("Cannot find target implementing "
					+ componentClass + " in host " + host);

		Method method = null;

		for (int i = 0; i < target.getClass().getMethods().length; i++) {
			if (target.getClass().getMethods()[i].getName().equals(methodName)) {
				method = target.getClass().getMethods()[i];
			}
		}
		if (method == null)
			throw new IllegalStateException("Unknown method " + methodName
					+ " for target " + target);

		// convert params
		Class[] paramTypes = method.getParameterTypes();
		if (paramTypes.length != paramStrings.length) {
			throw new IllegalStateException("Number of provided params "
					+ Arrays.asList(paramStrings)
					+ " do not match the signature of " + method);
		}
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {

			String string = paramStrings[i];
			Class type = paramTypes[i];
			params[i] = convertType(string, type);
		}

		ExtendedScenarioAction action = new ExtendedScenarioAction(target,
				method, time, params);
		this.actions.add(action);
		// ((DefaultHost) host).addAction(action);
	}

	private Object convertType(String string, Class type) {
		if (paramParsers.containsKey(type)) {
			return paramParsers.get(type).parse(string);
		} else if (type == OperationCallback.class) {
			return Operations.EMPTY_CALLBACK;
		} else {
			return DefaultConfigurator.convertValue(string, type);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.scenario.Scenario#createActions(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String[])
	 */
	public int createActions(String hostIds, String timeInterval,
			String methodName, String[] params) {
		// calculate time
		List<Host> hosts = groups.get(hostIds);
		if (hosts == null)
			throw new IllegalArgumentException("Unknown host/group id: "
					+ hostIds);
		long[] time = createTimePoints(hosts.size(), timeInterval);

		// select component
		Class selectedClass = null;
		if (methodName.contains(":")) {
			String[] split = methodName.split(":");
			String simpleName = split[0];
			methodName = split[1];
			log.info("Search for " + simpleName);
			// search for the appropriate class
			for (Class type : registeredComponentClasses) {
				Class[] interfaces = type.getInterfaces();
				log.info("for type " + type);
				// try itself
				if (type.getSimpleName().equals(simpleName)) {
					if (selectedClass != null)
						throw new IllegalArgumentException("Component Class "
								+ simpleName + " is ambiguous as both "
								+ selectedClass + " and " + type + " match it");
					selectedClass = type;
					continue;
				}

				// try interfaces
				for (int i = 0; i < interfaces.length; i++) {
					Class interfaceType = interfaces[i];
					log.info("for interface " + interfaceType);
					if (interfaceType.getSimpleName().equals(simpleName)) {
						if (selectedClass != null)
							throw new IllegalArgumentException(
									"Component Class " + simpleName
											+ " is ambiguous as both "
											+ selectedClass + " and " + type
											+ " match it");
						selectedClass = type;
					}
				}
			}
			if (selectedClass == null)
				throw new IllegalArgumentException(
						"Unknown component class with simple name="
								+ simpleName);
			log.info("Found " + selectedClass);
		} else {
			// use default class
			selectedClass = this.componentClass;
		}
		// create actions
		for (int i = 0; i < hosts.size(); i++) {
			createAction(hosts.get(i), selectedClass, time[i], methodName,
					params);
			log.debug("Created actions " + methodName + " and time offset="
					+ time[i] + "and params=" + Arrays.asList(params));
		}
		// int hostId = Integer.parseInt(tokens[0]);
		return hosts.size();
	}

	public void prepare() {
		// for (Host host : hosts) {
		// ((DefaultHost) host).scheduleEvents();
		// }
		for (ScenarioAction action : actions) {
			action.schedule();
		}
		log.info("Scheduled " + actions.size() + " actions ");
	}

	/**
	 * Create a list of timepoints within specific bounds.
	 * 
	 * @param size
	 *            number of time points desired
	 * @param boundsAsString
	 *            time interval with format:
	 *            (timepoint)|(start-end)|(start**end)
	 * @return array of time points with length of the <code>size</code>
	 *         parameter
	 */
	long[] createTimePoints(int size, String boundsAsString) {
		long[] times = new long[size];
		long lower;
		long upper;
		String[] timeBounds = boundsAsString.split("-|\\*\\*");
		if (timeBounds.length == 1) {
			lower = upper = DefaultConfigurator.parseTime(timeBounds[0]);
		} else if (timeBounds.length == 2) {
			lower = DefaultConfigurator.parseTime(timeBounds[0]);
			upper = DefaultConfigurator.parseTime(timeBounds[1]);
		} else {
			throw new IllegalArgumentException(
					"bad format for operations' time points " + boundsAsString);
		}
		if (timeBounds.length == 1) {
			for (int i = 0; i < times.length; i++) {
				times[i] = upper;
			}
		} else if (boundsAsString.contains("-")) {
			times = linearTimePoints(size, lower, upper);
		} else if (boundsAsString.contains("**")) {
			times = exponentialTimePoints(size, lower, upper);
		}

		return times;
	}

	/**
	 * Derive time points in uniformly distribution between lower and upper time
	 * bound.
	 * 
	 * @param size
	 *            The number of time points between the interval [lower; upper]
	 * @param lower
	 *            The lower bound of the time.
	 * @param upper
	 *            The upper bound of the time.
	 * @return An array with time points with the length of the
	 *         <code>size</code>, which are uniformly distributed on the lower
	 *         and upper interval.
	 */
	private long[] linearTimePoints(int size, long lower, long upper) {
		long[] times = new long[size];
		if (size == 1) {// special case: a group contains only one host, then
			// the time point is the middle of the interval!
			times[0] = (upper + lower) / 2;
		} else {
			for (int i = 0; i < times.length; i++) {
				times[i] = lower + ((upper - lower) * i) / (size - 1);
			}
		}
		return times;
	}

	/**
	 * Derive the time points in exponential intervals between lower and upper
	 * time. At first will be greater intervals and then will be smaller the
	 * intervals between the timePoints.
	 * 
	 * @param size
	 *            The number of time points between the interval [lower; upper]
	 * @param lower
	 *            The lower bound of the time.
	 * @param upper
	 *            The upper bound of the time.
	 * @return An array with time points with the length of the
	 *         <code>size</code>, which are exponential distributed on the lower
	 *         and upper interval.
	 */
	private long[] exponentialTimePoints(int size, long lower, long upper) {
		long[] times = new long[size];
		if (size == 1) {// special case: a group contains only one host, then
			// the time point is the middle of the interval!
			times[0] = (upper + lower) / 2;
		} else {
			long deltaTime = upper - lower;
			for (int i = 0; i < times.length; i++) {
				times[i] = lower + exp(i + 1, times.length, deltaTime);
			}
		}
		return times;
	}

	/**
	 * Exponential function for time. Intern derive it the lambda, which is
	 * used. It compute for y the timepoint.
	 * 
	 * @param y
	 *            Describes the number of actions. An integer between [1 and
	 *            maxY].
	 * @param maxY
	 *            The maximal number of actions
	 * @param maxTime
	 *            The span of time for exponential actions.
	 * @return A time between [0 and maxTime].
	 */
	private long exp(int y, int maxY, long maxTime) {
		double lambda = Math.log(maxY) / maxTime;
		long time = (long) (Math.log(y) / lambda);
		return time;
	}

	@Override
	public Map<String, List<Host>> getHosts() {
		return groups;
	}

}
