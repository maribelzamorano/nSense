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

package de.tu.darmstadt.dvs.nSense.overlay;

import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;

/**
 * This class is used by the simulator to instantiate a new node.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 * 
 */
public class NSenseNodeFactory implements ComponentFactory {
	/**
	 * The port that the nSense overlay should communicate over
	 */
	private short port = Configuration.PORT;

	private static NSenseBootstrapManager bootstrap;

	@Override
	public Component createComponent(Host host) {
		NSenseNode node = new NSenseNode(host.getTransLayer(), port, bootstrap);
		return node;
	}

	/**
	 * @param port
	 *            The port, which will be used by a new node
	 */
	public void setPort(int port) {
		this.port = (short) port;
	}

	public void setProperties(String propertiesPath) {
		Configuration.setProperties(propertiesPath);
	}

	public void setBootstrapManager(NSenseBootstrapManager bootstrap) {
		this.bootstrap = bootstrap;
	}
}
