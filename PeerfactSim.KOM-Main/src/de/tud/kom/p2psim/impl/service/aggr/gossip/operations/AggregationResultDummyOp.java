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


package de.tud.kom.p2psim.impl.service.aggr.gossip.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.service.aggr.gossip.GossipingAggregationService;

/**
 * Operation that immediately finishes and returns the aggregation result given
 * in the constructor.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class AggregationResultDummyOp extends AbstractOperation<GossipingAggregationService, IAggregationResult> {

	private IAggregationResult result;

	/**
	 * Default constructor
	 * @param component , the component that executes the operation
	 * @param callback , the operation callback that will be called when this operation
	 * finishes, i.e. immediately.
	 * @param result , the result that shall be handed over to the callback.
	 */
	public AggregationResultDummyOp(GossipingAggregationService component,
			OperationCallback<IAggregationResult> callback, IAggregationResult result) {
		super(component, callback);
		this.result = result;
	}

	@Override
	protected void execute() {
		this.operationFinished(true);
	}

	@Override
	public IAggregationResult getResult() {
		return result;
	}

}
