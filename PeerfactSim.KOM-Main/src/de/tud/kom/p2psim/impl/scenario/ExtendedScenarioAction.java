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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.scenario.ScenarioAction;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Specifies which action and when should take place in the simulator. The
 * method is specified and called via reflection.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 13.12.2007
 * 
 */
public final class ExtendedScenarioAction implements ScenarioAction {
	final static Logger log = SimLogger.getLogger(ExtendedScenarioAction.class);

	// Operation operation;
	/**
	 * Method which will be called upon execute.
	 */
	Method method;

	long offset;

	Component target;

	Object[] params;

	/**
	 * Creates a new scenario action.
	 * 
	 * @param target
	 *            - where to invoke
	 * @param method
	 *            - what to invoke
	 * @param offset
	 *            - after which time (TODO absolute time or relative? now
	 *            relative)
	 * @param params
	 *            - with which parameters
	 */
	protected ExtendedScenarioAction(Component target, Method method,
			long offset, Object[] params) {
		assert method != null;
		assert offset >= 0;
		this.target = target;
		this.method = method;
		this.params = params;
		this.offset = offset;
	}

	long getOffset() {
		return offset;
	}

	public void schedule() {
		log.debug("Schedule action for time " + offset);
		Simulator.scheduleEvent(this, offset, new SimulationEventHandler() {
			public void eventOccurred(SimulationEvent se) {
				try {
					method.invoke(target, params);
				} catch (Exception e) {
					log.error("Failed to execute action.", e);
					throw new RuntimeException("Failed to execute action.", e);
				}
			}
		}, SimulationEvent.Type.SCENARIO_ACTION);
	}

	@Override
	public String toString() {
		return "OperationBasedScenarioAction(" + method + ", " + offset + ")";
	}

}
