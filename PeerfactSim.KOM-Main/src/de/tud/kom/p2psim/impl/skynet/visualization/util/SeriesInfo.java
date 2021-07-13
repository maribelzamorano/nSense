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


package de.tud.kom.p2psim.impl.skynet.visualization.util;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.data.xy.YIntervalSeries;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SeriesInfo {

	private YIntervalSeries dataSeries;

	private String name;

	private Color color;

	private BasicStroke stroke;

	public SeriesInfo(int maximum, String name, Color color, BasicStroke stroke) {
		super();
		this.dataSeries = new YIntervalSeries(name);
		this.dataSeries.setMaximumItemCount(maximum);
		this.name = name;
		this.color = color;
		this.stroke = stroke;
	}

	public YIntervalSeries getDataSeries() {
		return dataSeries;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public BasicStroke getStroke() {
		return stroke;
	}

}
