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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Sucht für einen OverlayNode die passende schematische Position.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.11.2008
 * 
 */
public interface SchematicPositioner {

	/**
	 * Gibt die schematische Position für host, bzw node zurück.
	 * 
	 * Schematische Positionen dürfen nur zwischen 0 und 1 auf beiden
	 * Koordinatenachsen liegen, sonst kann das zu Darstellungsproblemen
	 * führen.
	 * 
	 * @param host
	 * @param node
	 * @return schematische Host-Position
	 */
	public Coords getSchematicHostPosition(Host host, OverlayNode node);

}
