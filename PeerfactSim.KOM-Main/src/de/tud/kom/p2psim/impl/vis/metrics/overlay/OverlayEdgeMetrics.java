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

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;

/**
 * Initialisiert und verwaltet alle Kantenmetriken.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class OverlayEdgeMetrics extends MetricsBase<OverlayEdgeMetric> {

	public OverlayEdgeMetrics() {

		this.addMetric(new EdgeType());
		this.addMetric(new EdgeOverlay());
		this.addMetric(new EdgeMessagesPerSecond());
		this.addMetric(new EdgeClassName());

		/*
		 * EdgeTestMetric2 bps = new EdgeTestMetric2(); this.addMetric(bps);
		 * 
		 * EdgeTBPerSec tbps = new EdgeTBPerSec(); this.addMetric(tbps);
		 */

	}

	public String toString() {
		return "Overlay, Kanten";
	}

}
