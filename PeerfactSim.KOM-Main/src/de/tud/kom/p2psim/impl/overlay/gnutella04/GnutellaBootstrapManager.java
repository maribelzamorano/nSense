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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaBootstrapManager implements BootstrapManager {

	private List<GnutellaOverlayNode> activeNodes = new LinkedList<GnutellaOverlayNode>();

	private List<GnutellaOverlayNode> activePeer = new LinkedList<GnutellaOverlayNode>();

	private static GnutellaBootstrapManager singletonInstance;

	public static GnutellaBootstrapManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GnutellaBootstrapManager();
		}
		return singletonInstance;
	}

	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> bootstrapInfo = new LinkedList<TransInfo>();
		for (GnutellaOverlayNode activeNode : activeNodes) {
			bootstrapInfo.add(activeNode.getTransLayer().getLocalTransInfo(
					activeNode.getPort()));
		}
		return bootstrapInfo;
	}

	public void registerNode(OverlayNode node) {
		activeNodes.add((GnutellaOverlayNode) node);
	}

	public void unregisterNode(OverlayNode node) {
		activeNodes.remove(node);
	}

	public int getSize() {
		return activePeer.size();
	}

	public void registerPeer(OverlayNode node) {
		if (!activePeer.contains(node)) {
			activePeer.add((GnutellaOverlayNode) node);
		}
	}

	public void unregisterPeer(OverlayNode node) {
		if (activePeer.contains(node)) {
			activePeer.remove(node);
		}
	}
}
