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


package de.tud.kom.p2psim.impl.overlay.dht.napster;

import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterServerNode;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.NetLayerAnalyzer;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.OPAnalyzer;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterClientServerFactory implements ComponentFactory {

	private short commonPort;

	private boolean isServer;

	private NapsterBootstrapManager bootstrap;

	private NapsterServerNode server;

	public NapsterClientServerFactory() {
		bootstrap = new NapsterBootstrapManager();
	}

	// the factory-method for a NapsterServerNode and a NapsterClientNode
	public OverlayNode createComponent(Host host) {

		OverlayNode node;
		if (isServer) {
			node = createServer(host);
			server = (NapsterServerNode) node;
		} else {
			node = createClient(host);
		}
		return node;
	}

	// method for creating the server
	private NapsterServerNode createServer(Host host) {
		NapsterServerNode server;
		IPv4NetID ip = (IPv4NetID) host.getNetLayer().getNetID();
//		OPAnalyzer.setServerNetId(ip);
		NetLayerAnalyzer.setServerNetId(ip);
		server = new NapsterServerNode(new NapsterOverlayID(ip), commonPort,
				host.getTransLayer(), bootstrap);
		return server;
	}

	// method for creating the client
	private NapsterClientNode createClient(Host host) {
		NapsterClientNode client;
		NapsterOverlayID id = new NapsterOverlayID(host.getNetLayer()
				.getNetID());
		TransInfo t = host.getTransLayer().getLocalTransInfo(commonPort);
		NapsterOverlayContact c = new NapsterOverlayContact(id, t);
		client = new NapsterClientNode(commonPort, host.getTransLayer(),
				server, bootstrap, c);
		return client;
	}

	// method to set the port-parameter, defined in the xml-file
	public void setPort(long port) {
		this.commonPort = (short) port;
	}

	// method to set the isServer-parameter, defined in the xml-file
	public void setIsServer(boolean isServer) {
		this.isServer = isServer;
	}

}
