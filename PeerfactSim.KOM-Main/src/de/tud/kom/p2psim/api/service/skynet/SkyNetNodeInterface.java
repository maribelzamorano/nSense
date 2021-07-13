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


package de.tud.kom.p2psim.api.service.skynet;

import de.tud.kom.p2psim.impl.skynet.attributes.AttributeInputStrategy;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeUpdateStrategy;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetMessageHandler;
import de.tud.kom.p2psim.impl.skynet.components.TreeHandler;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricInputStrategy;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricUpdateStrategy;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsInterpretation;
import de.tud.kom.p2psim.impl.skynet.queries.QueryHandler;

/**
 * This interface defines the methods for a SkyNet-node, which plays its role as
 * Coordinator. Since we divided the complete SkyNet-node into two parts, this
 * part describes the functionality, which only a Coordinator can employ. In
 * detail, the listed methods appoint the components of a SkyNet-node, which the
 * Coordinator may access.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetNodeInterface extends SkyNetLayer {

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>SkyNetMessageHandler</code>
	 * 
	 * @return the reference of the <code>SkyNetMessageHandler</code>-object
	 */
	public SkyNetMessageHandler getSkyNetMessageHandler();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>TreeHandler</code>
	 * 
	 * @return the reference of the <code>TreeHandler</code>-object
	 */
	public TreeHandler getTreeHandler();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>MetricInputStrategy</code>
	 * 
	 * @return the reference of the <code>MetricInputStrategy</code>-object
	 */
	public MetricInputStrategy getMetricInputStrategy();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>MetricUpdateStrategy</code>
	 * 
	 * @return the reference of the <code>MetricUpdateStrategy</code>-object
	 */
	public MetricUpdateStrategy getMetricUpdateStrategy();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>QueryHandler</code>
	 * 
	 * @return the reference of the <code>QueryHandler</code>-object
	 */
	public QueryHandler getQueryHandler();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>AttributeInputStrategy</code>
	 * 
	 * @return the reference of the <code>AttributeInputStrategy</code>-object
	 */
	public AttributeInputStrategy getAttributeInputStrategy();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>AttributeUpdateStrategy</code>
	 * 
	 * @return the reference of the <code>AttributeUpdateStrategy</code>-object
	 */
	public AttributeUpdateStrategy getAttributeUpdateStrategy();

	/**
	 * This method allows the calling Coordinator to access the functionality of
	 * the <code>MetricsInterpretation</code>
	 * 
	 * @return the reference of the <code>MetricsInterpretation</code>-object
	 */
	public MetricsInterpretation getMetricsInterpretation();
}
