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



package de.tud.kom.p2psim.impl.skynet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.scenario.Configurator;
import de.tud.kom.p2psim.impl.common.DefaultHost;
import de.tud.kom.p2psim.impl.scenario.DefaultConfigurator;
import de.tud.kom.p2psim.impl.scenario.DefaultHostBuilder;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.ChurnStatisticsAnalyzer;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This builder will parse an XML subtree and create hosts as specified there.
 * It expects a tree which looks as follows: <code>
 * &lt;HostBuilder&gt;
 * 	  &lt;Host groupID="..."&gt;...
 *   &lt;Group size="..." groupID="..."&gt;...
 * &lt;HostBuilder/&gt;
 * </code>
 * 
 * The exact values for XML tags are specified as constants in this class (see
 * below).
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 29.11.2007
 * 
 */
public class SkyNetHostBuilder extends DefaultHostBuilder {

	private static final Logger log = SimLogger
			.getLogger(SkyNetHostBuilder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.scenario.HostBuilder#parse(org.dom4j.Element,
	 * de.tud.kom.p2psim.api.scenario.Configurator)
	 */
	public void parse(Element elem, Configurator config) {
		DefaultConfigurator defaultConfigurator = (DefaultConfigurator) config;

		// create groups
		for (Iterator iter = elem.elementIterator(); iter.hasNext();) {
			Element groupElem = (Element) iter.next();
			String groupID = groupElem.attributeValue(GROUP_ID_TAG);
			if (groupID == null) {
				throw new IllegalArgumentException("Id of host/group "
						+ groupElem.asXML() + " must not be null");
			}

			// either a group of hosts or a single host (=group with size 1)
			int groupSize;
			if (groupElem.getName().equals(HOST_TAG)) {
				groupSize = 1;
			} else if (groupElem.getName().equals(GROUP_TAG)) {
				String attributeValue = config.parseValue(groupElem
						.attributeValue(GROUP_SIZE_TAG));
				groupSize = Integer.parseInt(attributeValue);
			} else {
				throw new IllegalArgumentException("Unexpected tag "
						+ groupElem.getName());
			}
			List<Host> group = new ArrayList<Host>(groupSize);

			// create hosts and instances of specified components for each host
			for (int i = 0; i < groupSize; i++) {
				log
						.info((i + 1) + ". host of Group " + groupID
								+ " is created");
				DefaultHost host = new DefaultHost();

				// initialize properties
				// Changed the constructor to SkyNetHostProperties to add some
				// additional properties
				SkyNetHostProperties hostProperties = new SkyNetHostProperties();
				host.setProperties(hostProperties);
				// minimal information for host properties is the group id
				hostProperties.setGroupID(groupID);

				// initialize layers and properties
				for (Iterator layers = groupElem.elementIterator(); layers
						.hasNext();) {
					Element layerElem = (Element) layers.next();
					if (layerElem.getName().equals(
							Configurator.HOST_PROPERTIES_TAG)) {
						defaultConfigurator.configureAttributes(hostProperties,
								layerElem);
					} else {
						// layer component factory
						ComponentFactory layer = (ComponentFactory) defaultConfigurator
								.configureComponent(layerElem);
						log.debug("Factory is " + layer + " for wanted elem "
								+ layerElem.asXML());
						Component comp = layer.createComponent(host);

						setComponent(host, comp);
					}
				}
				group.add(host);
				log.info("->" + host.toString());
			}
			log.info("------Created group " + groupID + " with " + group.size()
					+ " hosts------");
			hosts.addAll(group);
			groups.put(groupID, group);
		}
		log.info("******CREATION OF HOSTS IS FINISHED. CREATED " + hosts.size()
				+ " HOSTS******");
		if (ChurnStatisticsAnalyzer.isActivated()) {
			ChurnStatisticsAnalyzer.setCreatedHost(getAllHosts());
		}
		if (hosts.size() != experimentSize) {
			log
					.warn("Only "
							+ hosts.size()
							+ " hosts were specified, though the experiment size was set to "
							+ experimentSize);
		}
	}

}
