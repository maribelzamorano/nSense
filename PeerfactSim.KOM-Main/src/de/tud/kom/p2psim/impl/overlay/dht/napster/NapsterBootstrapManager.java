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

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterServerNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterBootstrapManager implements BootstrapManager {

	private static Logger log = SimLogger
			.getLogger(NapsterBootstrapManager.class);

	private OverlayNode bootstrapNode;

	public List<TransInfo> getBootstrapInfo() {
		if (bootstrapNode == null) {
			log.error("There is actually no BootstrapNode registered");
			return null;
		} else {
			TransInfo t = bootstrapNode.getHost().getTransLayer()
					.getLocalTransInfo(bootstrapNode.getPort());
			log.debug("BootstrapNode returns TransInfo " + t.toString());
			return Collections.singletonList(t);
		}
	}

	public void registerNode(OverlayNode node) {
		NapsterServerNode server = (NapsterServerNode) node;
		TransInfo t = server.getTransLayer()
				.getLocalTransInfo(server.getPort());
		log.debug("Registering node with TransInfo " + t.toString());
		bootstrapNode = server;
	}

	public void unregisterNode(OverlayNode node) {
		if (bootstrapNode == node) {
			TransInfo t = bootstrapNode.getHost().getTransLayer()
					.getLocalTransInfo(bootstrapNode.getPort());
			log.debug("Unregistering node with TransInfo " + t.toString());
			bootstrapNode = null;
		} else if (bootstrapNode == null) {
			log.error("There is actually no BootstrapNode registered");
		} else {
			TransInfo t = bootstrapNode.getHost().getTransLayer()
					.getLocalTransInfo(bootstrapNode.getPort());
			log.warn("This BootstrapNode has not the TransInfo " + t.toString()
					+ " and cannot be unregistered");
		}
	}
}
