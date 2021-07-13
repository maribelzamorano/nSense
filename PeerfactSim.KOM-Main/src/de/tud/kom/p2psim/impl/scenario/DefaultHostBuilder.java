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



package de.tud.kom.p2psim.impl.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import de.tud.kom.p2psim.api.application.Application;
import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.scenario.Builder;
import de.tud.kom.p2psim.api.scenario.Composable;
import de.tud.kom.p2psim.api.scenario.Configurator;
import de.tud.kom.p2psim.api.scenario.HostBuilder;
import de.tud.kom.p2psim.api.storage.ContentStorage;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.common.DefaultHost;
import de.tud.kom.p2psim.impl.common.DefaultHostProperties;
import de.tud.kom.p2psim.impl.storage.DefaultContentStorage;
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
public class DefaultHostBuilder implements HostBuilder, Composable, Builder {
	/**
	 * XML attribute with this name specifies the size of the group.
	 */
	public static final String GROUP_SIZE_TAG = "size";

	/**
	 * XML element with this name specifies a group of hosts.
	 */
	public static final String GROUP_TAG = "Group";

	/**
	 * XML element with this name specifies a single host and behaves equivalent
	 * to an element with the name = GROUP_TAG value and group size of 1.
	 */
	public static final String HOST_TAG = "Host";

	/**
	 * XML attribute with this name specifies the id of the group, which is used
	 * to refer to this group lateron, e.g. when you specify scenario actions.
	 */
	public static final String GROUP_ID_TAG = "groupID";

	private static final Logger log = SimLogger
			.getLogger(DefaultHostBuilder.class);

	/**
	 * Groups of hosts indexed by group ids.
	 */
	protected Map<String, List<Host>> groups;

	protected int experimentSize;

	/**
	 * Flat list of all hosts.
	 */
	protected final List<Host> hosts = new LinkedList<Host>();

	/**
	 * Will be called by the configurator.
	 * 
	 * @param size
	 *            total number of hosts in the simulator TODO we could remove
	 *            this or force its correctness...
	 */
	public void setExperimentSize(int size) {
		groups = new HashMap<String, List<Host>>(size);
		this.experimentSize = size;
	}

	public void compose(Configurator config) {
		// unused
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.scenario.HostBuilder#getAllHostsWithGroupIDs()
	 */
	public Map<String, List<Host>> getAllHostsWithGroupIDs() {
		Map<String, List<Host>> hosts = new HashMap<String, List<Host>>(groups);
		return hosts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.scenario.HostBuilder#getAllHosts()
	 */
	public List<Host> getAllHosts() {
		return hosts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tud.kom.p2psim.api.scenario.HostBuilder#getHosts(java.lang.String)
	 */
	public List<Host> getHosts(String groupId) {
		return groups.get(groupId);
	}

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

				DefaultHost host = new DefaultHost();

				// initialize properties
				DefaultHostProperties hostProperties = new DefaultHostProperties();
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
						Component comp = layer.createComponent(host);

						setComponent(host, comp);
					}
				}
				group.add(host);
			}
			log.debug("Created a group with " + group.size() + " hosts");
			hosts.addAll(group);
			groups.put(groupID, group);
		}
		log.info("CREATED " + hosts.size() + " hosts");
		if (hosts.size() != experimentSize) {
			log
					.warn("Only "
							+ hosts.size()
							+ " hosts were specified, though the experiment size was set to "
							+ experimentSize);
		}
	}

	protected void setComponent(DefaultHost host, Component comp) {
		// TODO setComponent method in the host?
		if (comp instanceof NetLayer) {
			host.setNetwork((NetLayer) comp);
		} else if (comp instanceof TransLayer) {
			host.setTransport((TransLayer) comp);
		} else if (comp instanceof OverlayNode) {
			host.setOverlayNode((OverlayNode) comp);
		} else if (comp instanceof Application) {
			host.setApplication((Application) comp);
		} else if (comp instanceof ContentStorage) {
			host.setContentStorage((DefaultContentStorage) comp);
		}
	}

}
