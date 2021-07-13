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


package de.tud.kom.p2psim.impl.common;

import de.tud.kom.p2psim.api.common.SupportOperations;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.stat.distributions.Distribution;

/**
 * An operation that is executed periodically after scheduling its first
 * execution.
 * 
 * Please implement the method executeOnce() to call all stuff the operation will execute every time,
 * instead of execute().
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @param <T>
 * @param <S>
 * @version 05/06/2011
 */
public abstract class PeriodicOperation<T extends SupportOperations, S> extends AbstractOperation<T, S> {

	boolean stopped;

	/**
	 * Creates a periodic operation
	 * @param component , the parent component
	 * @param interval , the execution interval of executeOnce();
	 */
	protected PeriodicOperation(T component) {
		super(component);
	}

	@Override
	protected final void execute() {
		this.executeOnce();
		if (!stopped) this.scheduleWithDelay(getInterval());
	}
	
	protected abstract long getInterval();
	
	/**
	 * Stops the periodic execution. Any subsequent calls
	 * of executeOnce() will not be made, unless the operation
	 * is rescheduled.
	 */
	public void stop() {
		this.stopped = true;
	}
	
	public void scheduleAtTime(long time) {
		this.stopped = false;
		super.scheduleAtTime(time);
	}
	
	protected abstract void executeOnce();

	public static abstract class StaticPeriodicOperation<T extends SupportOperations, S> extends PeriodicOperation<T, S> {

		private long interval;

		protected StaticPeriodicOperation(T component, long interval) {
			super(component);
			this.interval = interval;
		}
		
		protected long getInterval() {
			return interval;
		}
		
	}
	
	public static abstract class RandomIntervalPeriodicOperation<T extends SupportOperations, S> extends PeriodicOperation<T, S> {

		private Distribution intervalDist;
		
		/**
		 * @param component
		 * @param intervalDist	returning a random interval in SECONDS!
		 */
		protected RandomIntervalPeriodicOperation(T component, Distribution intervalDist) {
			super(component);
			this.intervalDist = intervalDist;
		}
		
		protected long getInterval() {
			return Math.round(intervalDist.returnValue()*Simulator.SECOND_UNIT);
		}
		
	}
	
}
