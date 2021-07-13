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


package de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.MetricsCollectorDelegator;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsAggregate;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class AbstractMetricsCollectorDelegator implements
		MetricsCollectorDelegator {

	protected SkyNetNodeInterface skyNetNode;

	public void setSkyNetNode(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	protected MetricsAggregate createAggregate(String name, double value,
			double intervalLength) {
		double mean = value / intervalLength;
		return new MetricsAggregate(name, mean, mean, mean, mean * mean, 1,
				Simulator.getCurrentTime(), Simulator.getCurrentTime(),
				Simulator.getCurrentTime());
	}

	protected MetricsAggregate createStubAggregate(String name) {
		return new MetricsAggregate(name, Simulator.getCurrentTime());
	}
}
