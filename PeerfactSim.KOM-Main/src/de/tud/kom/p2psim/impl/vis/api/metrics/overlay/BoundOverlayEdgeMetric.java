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


package de.tud.kom.p2psim.impl.vis.api.metrics.overlay;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;

/**
 * Adapter/Decorator, mit dem eine Metrik an eine Kante gebunden wird. Damit ist
 * es dann nicht mehr nötig, eine Kante zu übergeben, da die Metrik bereits an
 * eine Kante gebunden ist.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class BoundOverlayEdgeMetric extends BoundMetric {

	VisOverlayEdge e;

	OverlayEdgeMetric m;

	public BoundOverlayEdgeMetric(VisOverlayEdge e, OverlayEdgeMetric m) {
		super(m);
		this.m = m;
		this.e = e;
	}

	@Override
	public String getValue() {
		return m.getValue(e);
	}

}
