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


package de.tud.kom.p2psim.impl.vis.util.visualgraph;

import java.io.Serializable;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Coords implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3304956527008531448L;

	public Coords(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float x;

	public float y;

	/**
	 * Skaliert die Koordinaten um scaleFactor
	 * 
	 * @param scaleFactor
	 * @return
	 */
	public Coords scaleTo(float scaleFactor) {
		return new Coords(x * scaleFactor, y * scaleFactor);
	}

	/**
	 * Ersetzt die Koordinaten dorch c für alle Referenzen.
	 * 
	 * @param c
	 */
	public void replaceWith(Coords c) {
		this.x = c.x;
		this.y = c.y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
