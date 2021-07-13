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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;

/**
 * 
 * This class is used by Simulator to create ChordNode instance
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordNodeFactory implements ComponentFactory {

	private short port = 123;

	ChordBootstrapManager bootstrap = new ChordBootstrapManager();

	@Override
	public Component createComponent(Host host) {
		ChordNode node = new ChordNode(host.getTransLayer(), port, bootstrap);
		node.setPeerStatus(PeerStatus.TO_JOIN);
		return node;
	}

	/**
	 * @param port
	 *            The port, which will be used by a new node
	 */
	public void setPort(int port) {
		this.port = (short) port;
	}
}
