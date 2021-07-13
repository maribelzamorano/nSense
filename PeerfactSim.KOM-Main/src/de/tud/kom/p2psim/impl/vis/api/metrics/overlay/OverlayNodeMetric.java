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

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Metrik, von einem Knoten abhängig
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class OverlayNodeMetric extends Metric {

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			"images/icons/model/OverlayNode16_16.png");

	public abstract String getValue(VisOverlayNode node);

	public BoundMetric getBoundTo(VisOverlayNode n) {
		return new BoundOverlayNodeMetric(n, this);
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

}
