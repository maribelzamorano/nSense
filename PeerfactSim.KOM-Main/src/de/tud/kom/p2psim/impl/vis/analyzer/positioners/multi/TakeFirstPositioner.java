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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners.multi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.MultiPositioner;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.SchematicPositioner;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Ein MultiPositioner, der die OverlayAdapter der Reihe nach abgeht und den
 * ersten, der eine Position für host hat, verwendet um die Position zu
 * bestimmen.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.11.2008
 * 
 */
public class TakeFirstPositioner extends MultiPositioner {

	Map<OverlayAdapter, SchematicPositioner> loadedPositioners = new HashMap<OverlayAdapter, SchematicPositioner>();

	@Override
	public Coords getSchematicHostPosition(Host host) {

		Iterator<OverlayNode> it = host.getOverlays();

		while (it.hasNext()) {
			OverlayNode node = it.next();
			for (OverlayAdapter adapter : this.getAllAdapters()) {
				if (adapter.isDedicatedOverlayImplFor(node.getClass())) {

					SchematicPositioner positioner = loadedPositioners
							.get(adapter);
					if (positioner == null) {
						positioner = adapter.getNewPositioner();
						loadedPositioners.put(adapter, positioner);
					}

					Coords pos = positioner
							.getSchematicHostPosition(host, node);
					if (pos != null)
						return pos;
				}
			}
		}
		return null; // Es gibt keinen Adapter, der eine Position für den
		// Knoten hat.
	}

}
