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


package de.tud.kom.p2psim.impl.service.aggr.gossip;

import java.util.Iterator;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.overlay.JoinLeaveOverlayNode;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Default factory of the Gossiping Aggregation Service.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GossipingAggregationServiceFactory implements ComponentFactory {

	short port = 4000;
	
	public GossipingAggregationServiceFactory() {
		Monitoring.register();
	}
	
	@Override
	public Component createComponent(Host host) {
		JoinLeaveOverlayNode nd = getOverlay(host);
		return new GossipingAggregationService(host, nd, getNeighborDeterminationStrategyFor(nd), port, getConfig(), Simulator.getRandom().nextInt());
	}
	
	private JoinLeaveOverlayNode getOverlay(Host host) {
		Iterator<OverlayNode> ols = host.getOverlays();
		OverlayNode nd;
		while (ols.hasNext()) {
			nd = ols.next();
			if (nd instanceof JoinLeaveOverlayNode) return (JoinLeaveOverlayNode)nd;
		}
		throw new ConfigurationException("There are no overlays registered at host " + host + " of type JoinLeaveOverlayNode");
	}

	private IConfiguration getConfig() {
		return new DefaultConfig();
	}

	public INeighborDeterminator getNeighborDeterminationStrategyFor(JoinLeaveOverlayNode nd) {
		INeighborDeterminator str = nd.getNeighbors();
		if (str == null) throw new ConfigurationException("The overlay " + nd + " is not able to return its neighbors (returned null on request via getNeighbors())");
		return str;
	}
	
	public void setPort(short port) {
		this.port = port;
	}

}
