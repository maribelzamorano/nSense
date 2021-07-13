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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners.generic;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.SchematicPositioner;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Ein Positioner, der seine Knoten auf einem Ring ablegt.
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.11.2008
 *
 */
public abstract class RingPositioner implements SchematicPositioner{


	public static final float RING_RADIUS = 0.4f;
	public static final float offset_x = 0.5f;
	public static final float offset_y = 0.5f;

	
	@Override
	public Coords getSchematicHostPosition(Host host, OverlayNode nd) {
		
		double div = getPositionOnRing(nd);
		
		float x = RING_RADIUS*(float)Math.sin(2*Math.PI*div);
		float y = RING_RADIUS*(float)Math.cos(2*Math.PI*div);
		
		return new Coords(offset_x + x, offset_y + y);

	}

	/**
	 * Gibt die Position auf dem Ring zurück. Hierbei
	 * ist 0 = 1 = ganz oben auf dem Ring, und z.B. 0.5 ganz unten
	 * 0.25 ganz rechts und 0.75 ganz links
	 * @param nd 
	 * @return
	 */
	protected abstract double getPositionOnRing(OverlayNode nd);
	
}
