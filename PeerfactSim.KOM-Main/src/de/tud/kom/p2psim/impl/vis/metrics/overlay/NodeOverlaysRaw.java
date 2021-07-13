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

/**
 * Klassenname des Overlays/der Overlays, die der Knoten benutzt. (Benutzt
 * Knotenattribut "overlay")
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.10.2008
 * 
 */
public class NodeOverlaysRaw extends NodeOverlays {

	public NodeOverlaysRaw() {
		this.setColor(new Color(150, 200, 0));
	}

	protected String getAttrKeyName() {
		return "overlay_raw";
	}

	@Override
	public String getName() {
		return "Overlays, Klassennamen";
	}

}
