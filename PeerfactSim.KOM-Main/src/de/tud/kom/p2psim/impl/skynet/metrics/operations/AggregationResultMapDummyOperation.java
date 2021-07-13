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

package de.tud.kom.p2psim.impl.skynet.metrics.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.aggr.IAggregationMap;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class AggregationResultMapDummyOperation extends
		AbstractOperation<SkyNetNode, IAggregationMap<String>> {

	IAggregationMap<String> result;

	public AggregationResultMapDummyOperation(SkyNetNode component,
			OperationCallback<IAggregationMap<String>> callback,
			IAggregationMap<String> result) {
		super(component, callback);
		this.result = result;
	}

	@Override
	protected void execute() {
		operationFinished(true);

	}

	@Override
	public IAggregationMap<String> getResult() {
		return result;
	}

}
