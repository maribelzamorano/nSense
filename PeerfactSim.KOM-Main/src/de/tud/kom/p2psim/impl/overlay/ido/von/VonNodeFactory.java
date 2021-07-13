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

package de.tud.kom.p2psim.impl.overlay.ido.von;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;

/**
 * This class is used by the simulator to instantiate a new node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonNodeFactory implements ComponentFactory {

	/**
	 * The port that the VON overlay should communicate over
	 */
	private short port = 1111;

	private static BootstrapManager bootstrap;

	@Override
	public Component createComponent(Host host) {

		VonNode node = new VonNode(host.getTransLayer(), port);
		node.setBootstrapManager(bootstrap);
		return node;
	}

	/**
	 * @param port
	 *            The port, which will be used by a new node
	 */
	public void setPort(int port) {
		this.port = (short) port;
	}

	public void setBootstrapManager(BootstrapManager bootstrap) {
		this.bootstrap = bootstrap;
	}

}
