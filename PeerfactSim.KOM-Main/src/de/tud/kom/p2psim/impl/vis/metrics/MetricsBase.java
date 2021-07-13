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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsPack.MetricsPackPart;
import de.tud.kom.p2psim.impl.vis.metrics.overlay.OverlayEdgeMetrics;
import de.tud.kom.p2psim.impl.vis.metrics.overlay.OverlayNodeMetrics;
import de.tud.kom.p2psim.impl.vis.metrics.overlay.OverlayUniverseMetrics;

/**
 * Verwaltet und initialisiert alle Metriken, bietet diverse Filtermechanismen
 * an.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TMetric>
 * @version 05/06/2011
 */
public abstract class MetricsBase<TMetric extends Metric> {

	// Statischer Singleton-Teil -------------------------------

	private static MetricsPack dynMetrics = new MetricsPack();

	private static OverlayNodeMetrics overlay_node = new OverlayNodeMetrics();

	private static OverlayEdgeMetrics overlay_edge = new OverlayEdgeMetrics();

	private static OverlayUniverseMetrics overlay_univ = new OverlayUniverseMetrics();

	/*
	 * private static NetworkNodeMetrics network_node = new
	 * NetworkNodeMetrics(); private static NetworkEdgeMetrics network_edge =
	 * new NetworkEdgeMetrics();
	 */

	private static Vector<MetricsBase<? extends Metric>> all_metrics = new Vector<MetricsBase<?>>();

	static {
		setDynamicMetricsPack(new MetricsPack());
	}

	public static void init() {
		all_metrics.add(overlay_node);
		all_metrics.add(overlay_edge);
		all_metrics.add(overlay_univ);
		// setDynamicMetricsPack(new MetricsPack());
	}

	public static List<Metric> getAllMetrics() {
		Vector<Metric> res = new Vector<Metric>();

		for (MetricsBase<? extends Metric> mb : all_metrics) {
			for (Metric m : mb.getListOfAllMetrics()) {
				res.add(m);
			}
		}

		return res;
	}

	public static void saveSettings() {
		for (MetricsBase<? extends Metric> mb : all_metrics) {
			for (Metric m : mb.getListOfAllMetrics()) {
				m.saveSettings();
			}
		}
	}

	public static MetricsPack getDynamicMetricsPack() {
		return MetricsBase.dynMetrics;
	}

	public static void setDynamicMetricsPack(MetricsPack dyn_metrics) {
		MetricsBase.dynMetrics = dyn_metrics;
		MetricsBase.forOverlayEdges().setDynamicMetrics(dyn_metrics.getEdges());
		MetricsBase.forOverlayNodes().setDynamicMetrics(dyn_metrics.getNodes());
		MetricsBase.forOverlayUniverse().setDynamicMetrics(
				dyn_metrics.getUniverse());
	}

	public static OverlayNodeMetrics forOverlayNodes() {
		return overlay_node;
	}

	public static OverlayEdgeMetrics forOverlayEdges() {
		return overlay_edge;
	}

	public static OverlayUniverseMetrics forOverlayUniverse() {
		return overlay_univ;
	}

	// Objekt-Teil -------------------------------

	public MetricsPackPart<TMetric> dynamicMetrics;

	public Set<TMetric> metrics = new HashSet<TMetric>();

	public HashSet<TMetric> metrics_activated = new HashSet<TMetric>();

	public MetricsBase() {
		//Nothing to do
	}

	public MetricsBase(MetricsPackPart<TMetric> dynamic_metrics) {
		setDynamicMetrics(dynamic_metrics);
	}

	/**
	 * Fügt eine Metrik der Base hinzu. Sollte nur beim Start aufgerufen
	 * werden, vor jeglichem Abspielen.
	 * 
	 * @param m
	 */
	public void addMetric(TMetric metric) {
		metrics.add(metric);
	}

	/**
	 * eine Liste aller in dieser Base definierten Metriken
	 * 
	 * @return
	 */
	public Vector<TMetric> getListOfAllMetrics() {

		Vector<TMetric> result = new Vector<TMetric>(metrics);
		result.addAll(getDynamicMetrics().getMetrics());
		return result;
	}

	/**
	 * eine Liste der aktivierten Metriken dieser Base
	 * 
	 * @return
	 */
	public Collection<TMetric> getListOfActivatedMetrics() {
		Vector<TMetric> activated = new Vector<TMetric>();
		for (TMetric m : getListOfAllMetrics()) {
			if (m.isActivated())
				activated.add(m);
		}
		return activated;
	}

	public abstract String toString();

	public MetricsPackPart<TMetric> getDynamicMetrics() {
		return dynamicMetrics;
	}

	public void setDynamicMetrics(MetricsPackPart<TMetric> dynamicMetrics) {
		this.dynamicMetrics = dynamicMetrics;
	}

}
