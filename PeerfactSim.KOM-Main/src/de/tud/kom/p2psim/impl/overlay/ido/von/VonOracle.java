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

package de.tud.kom.p2psim.impl.overlay.ido.von;

import java.util.List;
import java.util.Vector;

import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.IDOOracle;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.ido.von.voronoi.Voronoi;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class VonOracle implements IDOOracle {

	private Voronoi globalVoronoi;

	public VonOracle() {
		this.globalVoronoi = new Voronoi(VonID.EMPTY_ID);
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos) {
		for (IDONodeInfo info : nodeInfos) {
			VonNodeInfo temp = new VonNodeInfo(new VonContact(
					(VonID) info.getID(), null), info.getPosition(),
					info.getAoiRadius());
			globalVoronoi.insert(temp, 0l);
		}

	}

	@Override
	public void reset() {
		globalVoronoi = new Voronoi(VonID.EMPTY_ID);
	}

	@Override
	public List<IDONodeInfo> getAllNeighbors(OverlayID id, int aoi) {
		VonNodeInfo[] neighbors = globalVoronoi
				.getVonNeighbors((VonID) id, aoi);
		List<IDONodeInfo> result = new Vector<IDONodeInfo>(neighbors.length);
		for (VonNodeInfo vonNodeInfo : neighbors) {
			result.add(vonNodeInfo);
		}
		return result;
	}
}
