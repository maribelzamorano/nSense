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


package de.tud.kom.p2psim.impl.vis.metrics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayUniverseMetric;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricsPack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6782000462960510486L;

	protected MetricsPackPart<OverlayNodeMetric> nodes = new MetricsPackPart<OverlayNodeMetric>();

	protected MetricsPackPart<OverlayEdgeMetric> edges = new MetricsPackPart<OverlayEdgeMetric>();

	protected MetricsPackPart<OverlayUniverseMetric> universe = new MetricsPackPart<OverlayUniverseMetric>();

	/**
	 * Entfernt alle Instanzen der enthaltenen Metriken. Werden beim nächsten
	 * Aufruf neu instanziiert.
	 */
	public void clearInstances() {
		nodes.clearInstances();
		edges.clearInstances();
		universe.clearInstances();
	}

	public MetricsPackPart<OverlayNodeMetric> getNodes() {
		return nodes;
	}

	public MetricsPackPart<OverlayEdgeMetric> getEdges() {
		return edges;
	}

	public MetricsPackPart<OverlayUniverseMetric> getUniverse() {
		return universe;
	}

	public class MetricsPackPart<M extends Metric> implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6094347201051011597L;

		Set<Class<? extends M>> metrics = new HashSet<Class<? extends M>>();

		Set<M> metricsInst = null;

		public void addMetric(Class<? extends M> m) {
			metrics.add(m);
			if (metricsInst != null)
				try {
					metricsInst.add(m.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		public void clearInstances() {
			this.metricsInst = null;
			System.gc();
		}

		public Set<M> getMetrics() {
			if (metricsInst == null) {
				metricsInst = new HashSet<M>();
				try {
					for (Class<? extends M> m : metrics)
						metricsInst.add(m.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return metricsInst;

		}

	}

}
