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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.UpdateDirectSuccessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to start the events periodically 
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class ChordOperationListener implements OperationCallback<ChordContact> {

	private static Logger log = SimLogger.getLogger(ChordOperationListener.class);
			
	private ChordNode masterNode;
	
	private boolean isActive = true;

	public ChordOperationListener(ChordNode _masterNode) {
		this.masterNode = _masterNode;
	}

	@Override
	public void calledOperationFailed(Operation<ChordContact> op) {
		if(masterNode.isPresent()){
			log.info("calledOperationFailed");
			addNextOperation(op);
		}
	}

	@Override
	public void calledOperationSucceeded(Operation<ChordContact> op) {
		if(masterNode.isPresent()){
			log.debug("Operation Succeeded " + op);
			addNextOperation(op);
		}
	}

	private void addNextOperation(Operation<ChordContact> op){
		
		if (!masterNode.isPresent() || !isActive) {
			return;
		}

		if (op instanceof UpdateDirectSuccessorOperation) {
			long exeTime = Simulator.getCurrentTime()
					- ((UpdateDirectSuccessorOperation) op).getBeginTime();
			exeTime = exeTime % ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL;
			
			new UpdateDirectSuccessorOperation(masterNode, this)
					.scheduleWithDelay(ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL - exeTime);
			
		} else if (op instanceof UpdateFingerPointOperation) {

			long exeTime = Simulator.getCurrentTime()
			- ((UpdateFingerPointOperation) op).getBeginTime();
			exeTime = exeTime % ChordConfiguration.UPDATE_SUCCESSOR_INTERVAL;
	
			new UpdateFingerPointOperation(masterNode, this)
					.scheduleWithDelay(ChordConfiguration.UPDATE_FINGERTABLE_INTERVAL - exeTime);
		}

	}
	
	public void setInactive(){
		this.isActive = false;
	}
}
