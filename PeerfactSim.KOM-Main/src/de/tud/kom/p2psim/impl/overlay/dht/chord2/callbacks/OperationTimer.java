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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.LookupOperation;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used as a timer for an operation. After a specified period of
 * time, the operation will be notified about the time out event.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class OperationTimer implements SimulationEventHandler {
	final static Logger log = SimLogger.getLogger(OperationTimer.class);

	private OperationCallback<?> operationCallback = null;
	
	private LookupOperation lookupOp = null;

	public OperationTimer(OperationCallback<?> op, long timeOut) {

		this.operationCallback = op;
		scheduleAtTime(Simulator.getCurrentTime() + timeOut);
	}

	public OperationTimer(LookupOperation lookupOp, long timeOut) {
		
		this.lookupOp = lookupOp;
		scheduleAtTime(Simulator.getCurrentTime() + timeOut);
	}
	
	@Override
	public void eventOccurred(SimulationEvent se) {
		
		if(operationCallback != null){
			operationCallback.calledOperationFailed(null);	
		}
		
		if (lookupOp != null) {
			lookupOp.timeoutOccurred();
		}
	}

	protected void scheduleAtTime(long time) {
		time = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, time, this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

}
