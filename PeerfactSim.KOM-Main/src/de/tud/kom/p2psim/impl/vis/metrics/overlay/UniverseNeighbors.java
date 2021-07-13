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


package de.tud.kom.p2psim.impl.vis.metrics.overlay;

import java.text.DecimalFormat;
import java.util.List;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayUniverseMetric;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Node;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class UniverseNeighbors extends OverlayUniverseMetric {

	@Override
	public String getValue() {

		List<Node<VisOverlayNode, VisOverlayEdge>> nodes = Controller
				.getModel().getOverlayGraph().nodes;

		int totalNeighbors = 0;

		for (Node<VisOverlayNode, VisOverlayEdge> node : nodes) {

			totalNeighbors += node.edges.size();
		}

		if (nodes.size() == 0)
			return null;

		double result = (double) totalNeighbors / (double) nodes.size();

		return new DecimalFormat("#.0").format(result).toString();

	}

	@Override
	public String getName() {
		return "Durchschnittliche Anzahl Nachbarn";
	}

	@Override
	public String getUnit() {
		return null;
	}

	@Override
	public boolean isNumeric() {
		return true;
	}

}
