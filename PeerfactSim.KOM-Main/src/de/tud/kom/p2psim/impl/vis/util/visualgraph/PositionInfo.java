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
 * Positionsinfos, die für Objekte in der Visualisierung von Bedeutung sind.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class PositionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3828228790124277563L;

	Coords topo_coords;

	Coords schem_coords;

	/**
	 * Konstruktor ohne schematische Koordinaten
	 * 
	 * @param topo_coords
	 */
	public PositionInfo(Coords topo_coords) {
		this.topo_coords = topo_coords;
		this.schem_coords = null;
	}

	/**
	 * Standard-Konstruktor.
	 * 
	 * @param topo_coords
	 * @param schem_coords
	 */
	public PositionInfo(Coords topo_coords, Coords schem_coords) {
		this.topo_coords = topo_coords;
		this.schem_coords = schem_coords;
	}

	/**
	 * Gibt die topologischen Koordinaten zurück
	 * 
	 * @return
	 */
	public Coords getTopoCoords() {
		return topo_coords;
	}

	/**
	 * Setzt die topologischen Koordinaten
	 * 
	 * @param topo_coords
	 */
	public void setTopoCoords(Coords topo_coords) {
		this.topo_coords = topo_coords;
	}

	/**
	 * Gibt die schematischen Koordinaten eines Knoten zurück. Wenn nicht
	 * existent, die topologischen Koordinaten.
	 * 
	 * @return
	 */
	public Coords getSchemCoords() {
		if (schem_coords == null)
			return topo_coords;
		return schem_coords;
	}

	/**
	 * Setzt die schematischen Koordinaten
	 * 
	 * @param schem_coords
	 */
	public void setSchemCoords(Coords schem_coords) {
		this.schem_coords = schem_coords;
	}

}
