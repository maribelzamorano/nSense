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


package de.tud.kom.p2psim.impl.application.simpleaggr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.service.aggr.IAggregationService;
import de.tud.kom.p2psim.api.service.aggr.NoSuchValueException;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.service.aggr.AggregationToolkit;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.stat.distributions.Distribution;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SimpleAggregationApp extends AbstractApplication {

	private IAggregationService srvc = null;
	private Map<String, Distribution> distributions;
	BufferedWriter dumpStr;
	private IAggregationService service2compare;
	
	Logger log = SimLogger.getLogger(SimpleAggregationApp.class);

	public SimpleAggregationApp(Host host, Map<String, Distribution> distributions, 
			BufferedWriter dumpStr, IAggregationService service2compare) {
		this.setHost(host);
		this.distributions = distributions;
		this.dumpStr = dumpStr;
		this.service2compare = service2compare;
	}
	
	public void setLocalValueDist(String identifier, String distribution) {
		Distribution dist = distributions.get(distribution);
		if (dist == null) throw new IllegalArgumentException("The distribution is not registered in the config: " + distribution);
		setLocalValue(identifier, dist.returnValue());
	}
	
	public void setLocalValue(String identifier, double value) {
		try {
			getService().setLocalValue(identifier, value);
			service2compare.setLocalValue(identifier, value);
		} catch (NoSuchValueException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void dumpAggregationResult(final String identifier) {
		final long startTime = Simulator.getCurrentTime();
		
		try {
			service2compare.getAggregationResult(identifier, new OperationCallback<IAggregationResult>() {

				@Override
				public void calledOperationFailed(
						Operation<IAggregationResult> op) {
					log.error("Failed to gather result of the service to compare to.");
				}

				@Override
				public void calledOperationSucceeded(
						Operation<IAggregationResult> op) {
					
					final IAggregationResult result2compare = op.getResult();
					
					try {
						getService().getAggregationResult(identifier, new OperationCallback<IAggregationResult>() {

							@Override
							public void calledOperationFailed(Operation<IAggregationResult> op) {
								long currentTime = Simulator.getCurrentTime();
								try {
									dumpStr.write(AggregationToolkit.printResultFailedCSV(getHost(), startTime, currentTime - startTime)
											+ "\t" + AggregationToolkit.printResultFailedCSV(getHost(), startTime, currentTime - startTime) + "\n");
									System.out.println("Getting the result failed.");
									dumpStr.flush();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}

							@Override
							public void calledOperationSucceeded(Operation<IAggregationResult> op) {
								IAggregationResult res = op.getResult();
								long currentTime = Simulator.getCurrentTime();
								try {
									String result = AggregationToolkit.printResultCSV(getHost(), startTime, currentTime - startTime, res)
										+ "\t" + AggregationToolkit.printResultCSV(getHost(), startTime, currentTime - startTime, result2compare) + "\n";
									System.out.println("Result: " + result);
									dumpStr.write(result);
									dumpStr.flush();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
							
						});
					} catch (NoSuchValueException e) {
						throw new RuntimeException(e);
					}
				}
				
			});
		} catch (NoSuchValueException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected IAggregationService getService() {
		if (srvc == null) {
			srvc = determineCorrectAggregationService(getHost());
		}
		return srvc;
		
	}
	
	private IAggregationService determineCorrectAggregationService(Host host) {
		IAggregationService comp = host.getComponent(IAggregationService.class);
		if (comp == null) throw new ConfigurationException("There is no aggregation service (IAggregationService) registered at the current node that could be used by the application.");
		return comp;
	}
	
	public void join() {
		getService().join(Operations.getEmptyCallback());
	}

	public void leave() {
		getService().leave(Operations.getEmptyCallback());
	}

}






