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

import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.overlay.OverlayNode;

/**
 * Positioniert die Knoten in einem Ring, indem der Ring in zwei Hälften geteilt wird,
 * dessen Hälften wieder in 2 Teile usw.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 15.11.2008
 *
 */
public class FCFSPartitionRingPositioner extends RingPositioner{
	
	Map<OverlayNode, Double> positions = new HashMap<OverlayNode, Double>();
	
	int currentPartExp = 0;
	int currentPartCount = 1;
	
	@Override
	protected double getPositionOnRing(OverlayNode nd) {
		if (positions.containsKey(nd)) return positions.get(nd);
		
		double result = currentPartCount/Math.pow(2, currentPartExp);
		
		currentPartCount = currentPartCount +2;
		if (currentPartCount >= Math.pow(2, currentPartExp)) {
			currentPartExp++;
			currentPartCount = 1;
		}
		
		positions.put(nd, result);
		
		return result;
	}

}
