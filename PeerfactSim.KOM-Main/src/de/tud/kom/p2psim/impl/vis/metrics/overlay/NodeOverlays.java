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
import java.util.List;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Name des Overlays/der Overlays, die der Knoten benutzt. und die visualisiert
 * werden. (Benutzt Knotenattribut "overlay")
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.10.2008
 * 
 */
public class NodeOverlays extends OverlayNodeMetric {

	public NodeOverlays() {
		this.setColor(new Color(100, 200, 0));
	}

	protected String getAttrKeyName() {
		return "overlay";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getValue(VisOverlayNode node) {
		Object attr = node.getAttribute(getAttrKeyName());

		if (attr != null && attr instanceof List) {
			return printCommaSeparated((List<Object>) attr);
		} else if (attr != null)
			return attr.toString();
		else
			return null;
	}

	/**
	 * Gibt eine Liste als String aus, wobei die Elemente durch ein Komma
	 * getrennt sind. Bei Objekten wird dafür toString() verwendet.
	 * 
	 * @param attr
	 * @return
	 */
	private String printCommaSeparated(List<Object> attr) {
		String result = "";
		boolean setComma = false;
		for (Object o : attr) {
			if (setComma)
				result += ", ";
			result += o.toString();
			setComma = true;
		}
		return result;
	}

	@Override
	public String getName() {
		return "Benutzte Overlays";
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
