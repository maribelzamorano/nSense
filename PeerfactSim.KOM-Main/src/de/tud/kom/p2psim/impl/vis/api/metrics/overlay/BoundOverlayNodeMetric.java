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
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Adapter/Decorator, mit dem eine Metrik an einen Knoten gebunden wird. Damit
 * ist es dann nicht mehr nötig, einen Knoten zu übergeben, da die Metrik
 * bereits an einen Knoten gebunden ist.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class BoundOverlayNodeMetric extends BoundMetric {

	OverlayNodeMetric m;

	VisOverlayNode n;

	public BoundOverlayNodeMetric(VisOverlayNode n, OverlayNodeMetric m) {
		super(m);
		this.m = m;
		this.n = n;
	}

	@Override
	public String getValue() {
		return m.getValue(n);
	}

}
