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

import java.awt.Color;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;

/**
 * Art der Beziehung, die durch die Kante ausgedrückt wird.
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.10.2008
 *
 */
public class EdgeType extends OverlayEdgeMetric {

	public EdgeType() {
		this.setColor(new Color (200, 0, 100));
	}
	
	@Override
	public String getValue(VisOverlayEdge edge){
		Object attr = edge.getAttribute("type");

		if (attr != null) return attr.toString();
		else return null;
	}

	@Override
	public String getName() {
		return "Typ";
	}

	@Override
	public String getUnit() {
		return "";
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	
}
