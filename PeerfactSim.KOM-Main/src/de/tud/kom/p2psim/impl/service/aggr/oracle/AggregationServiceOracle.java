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


package de.tud.kom.p2psim.impl.service.aggr.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.aggr.IAggregationMap;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.service.aggr.IAggregationService;
import de.tud.kom.p2psim.api.service.aggr.NoSuchValueException;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.common.Operations;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class AggregationServiceOracle extends AbstractApplication implements
		IAggregationService<Object> {

	private OracleUniverse universe;

	Map<Object, Double> localVals;

	public AggregationServiceOracle(OracleUniverse universe) {
		this.universe = universe;
		localVals = new HashMap<Object, Double>();
		universe.add(this);
	}

	@Override
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException {
		Double result = localVals.put(identifier, value);
		if (result == null)
			return Double.NaN;
		return result;
	}

	@Override
	public double getLocalValue(Object identifier) throws NoSuchValueException {
		Double result = localVals.get(identifier);
		if (result == null)
			throw new NoSuchValueException(identifier);
		return result;
	}

	@Override
	public void join(OperationCallback<Object> cb) {
		Operations.createEmptyOperation(this, cb).scheduleImmediately();
	}

	@Override
	public void leave(OperationCallback<Object> cb) {
		Operations.createEmptyOperation(this, cb).scheduleImmediately();
	}

	@Override
	public int getAggregationResult(Object identifier,
			OperationCallback<IAggregationResult> callback)
			throws NoSuchValueException {
		Operation op = Operations.createEmptyOperationResult(this, callback,
				universe.getAggregationResult(identifier));
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int getAggregationResultMap(
			OperationCallback<IAggregationMap<Object>> callback) {
		// FIXME Implement me
		return 0;
	}

	@Override
	public List<Object> getIdentifiers() {
		List<Object> result = new Vector<Object>();
		if (localVals != null) {
			result.addAll(localVals.keySet());
		}
		return result;
	}

	@Override
	public IAggregationResult getStoredAggregationResult(Object identifier) {
		return universe.getAggregationResult(identifier);
	}

	@Override
	public long getGlobalAggregationReceivingTime(Object identifier) {
		// is 0 because it will be derive at call.
		return 0;
	}

	@Override
	public int getNumberOfMonitoredAttributes() {
		return 0;
	}

}
