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


package de.tud.kom.p2psim.impl.application.filesharing2;

import java.util.Iterator;

import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.overlay.gnutella.GnutellaAPI;
import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.Chord2Handler;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.GnutellaHandler;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBRHandler;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.Kademlia2Handler;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.TestOracle;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.AbstractKademliaNode;

/**
 * 
 * Filesharing2 application factory to create new filesharing application layers
 * for different hosts. This class automatically detects the overlay used by the
 * host and creates a new handler appropriate for the overlay used.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class FilesharingApplicationFactory implements ComponentFactory {

	// private static final Logger log =
	// SimLogger.getLogger(FileSharingFactory.class);
	private boolean oracleTest = false;

	@Override
	public FilesharingApplication createComponent(Host host) {

		if (oracleTest)
			return new FilesharingApplication(new TestOracle());
		if (host.getOverlay(de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode.class) != null)
			return new FilesharingApplication(
					new Chord2Handler(
							(de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode) host
									.getOverlay(de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode.class)));
		if (host.getOverlay(AbstractKademliaNode.class) != null)
			return new FilesharingApplication(new Kademlia2Handler(
					(AbstractKademliaNode) host
							.getOverlay(AbstractKademliaNode.class)));
		if (host.getOverlay(GnutellaAPI.class) != null)
			return new FilesharingApplication(new GnutellaHandler(
					(GnutellaAPI) host.getOverlay(GnutellaAPI.class)));
		if (host.getOverlay(KBR.class) != null)
			return new FilesharingApplication(new KBRHandler(
					(KBR) host.getOverlay(KBR.class)));

		Iterator<OverlayNode> olays = host.getOverlays();

		String olaysStr = "";

		while (olays.hasNext()) {
			olaysStr += olays.next().getClass().getSimpleName() + ", ";
		}

		throw new ConfigurationException("The host " + host + ", " + olaysStr
				+ " is not supported by filesharing2");

		/*
		 * DistributionStrategy strategy = (DistributionStrategy)
		 * host.getOverlay(DistributionStrategy.class); FileSharingClient client
		 * = new FileSharingClient(dhtNode, strategy);
		 * log.debug("create napster client " + client); return client;
		 */
	}

	@Override
	public String toString() {
		return "Filesharing2 factory";
	}

	/**
	 * Puts this instance into "oracle" test mode, a mode that automatically
	 * locates a document if it was shared and the sharing host is online. Made
	 * for debugging purposes. Has to be set before the filesharing component is
	 * created, e.g. in the XML config file.
	 * 
	 * @param test
	 */
	public void setOracleTest(boolean test) {
		this.oracleTest = test;
	}

}
