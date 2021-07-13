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
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Der Server sitzt in der Mitte, die Clients sind um ihn rum angesiedelt.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.11.2008
 * 
 */
public abstract class ClientServerPartitionRingPositioner extends
		FCFSPartitionRingPositioner {

	@Override
	public Coords getSchematicHostPosition(Host host, OverlayNode nd) {

		if (isServer(host, nd)) {
			return new Coords(RingPositioner.offset_x, RingPositioner.offset_y);
		} else {
			return super.getSchematicHostPosition(host, nd);
		}

	}

	/**
	 * Ob der angegebene Host, die angegebene OverlayNode der Server ist
	 * 
	 * @param host
	 * @param nd
	 * @return
	 */
	public abstract boolean isServer(Host host, OverlayNode nd);

}
