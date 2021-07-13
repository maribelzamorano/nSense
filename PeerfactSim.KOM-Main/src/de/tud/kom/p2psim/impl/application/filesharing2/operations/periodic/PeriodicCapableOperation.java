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


package de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.common.SupportOperations;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FilesharingOperation;
import de.tud.kom.p2psim.impl.common.AbstractOperation;

/**
 * An operation capable of periodic rescheduling.
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class PeriodicCapableOperation<T extends SupportOperations, S extends Object>
		extends AbstractOperation<T, S> implements FilesharingOperation {

	boolean reschedule = false;

	boolean stopped = false;

	long meanInterval;

	private IntervalModel mdl;

	public PeriodicCapableOperation(T component, OperationCallback<S> callback) {
		super(component, callback);
	}

	@Override
	public void execute() {
		if (!stopped)
			executeOnce();
		if (reschedule)
			reschedule();
	}

	/**
	 * Called on each rescheduling attempt. Here, you should implement the
	 * functionality of the periodic operation that is executed on each period.
	 */
	protected abstract void executeOnce();

	public void schedulePeriodically(IntervalModel mdl) {
		this.mdl = mdl;
		reschedule = true;
		reschedule();

	}

	/**
	 * If called, the periodic rescheduling stops.
	 */
	public void stop() {
		reschedule = false;
		stopped = true;
	}

	private void reschedule() {
		this.scheduleWithDelay(mdl.getNewDelay());
	}

	/**
	 * Returns interval delays according to a specified model.
	 * 
	 * @author
	 * 
	 */
	public interface IntervalModel {

		/**
		 * Returns a new delay in simulation time units.
		 * 
		 * @return
		 */
		public long getNewDelay();
	}

}
