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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.application.NamedDistribution;
import de.tud.kom.p2psim.impl.scenario.XMLConfigurableConstructor;
import de.tud.kom.p2psim.impl.service.aggr.AggregationToolkit;
import de.tud.kom.p2psim.impl.service.aggr.oracle.AggregationServiceOracle;
import de.tud.kom.p2psim.impl.service.aggr.oracle.OracleUniverse;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.stat.distributions.Distribution;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SimpleAggregationAppFactory implements ComponentFactory {

	File outputFile = null;
	BufferedWriter output = null;
	Map<String, Distribution> distributions = null;
	static final Logger log = SimLogger.getLogger(SimpleAggregationAppFactory.class);
	OracleUniverse orUniv = new OracleUniverse();
	
	@XMLConfigurableConstructor({"outputFile"})
	public SimpleAggregationAppFactory(String outputFile) {
		this.outputFile = new File(outputFile);
		distributions = new HashMap<String, Distribution>();
	}
	
	@Override
	public Component createComponent(Host host) {
		if (output == null) {
			try {
				output = prepareOutputStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new SimpleAggregationApp(host, distributions, output, new AggregationServiceOracle(orUniv));
	}
	
	private BufferedWriter prepareOutputStream() throws IOException {
		outputFile.getParentFile().mkdirs();
		FileWriter wr = new FileWriter(outputFile);
		BufferedWriter buf = new BufferedWriter(wr);
		buf.write(AggregationToolkit.printDescLineCSV() + "\t" + AggregationToolkit.printDescLineCSV() + "\n");
		buf.flush();
		return buf;
	}
	
	public void setDistribution(NamedDistribution dist) {
		distributions.put(dist.getName(), dist.getValue());
		log.debug("Distribution set " + dist);
	}
	
	

}
