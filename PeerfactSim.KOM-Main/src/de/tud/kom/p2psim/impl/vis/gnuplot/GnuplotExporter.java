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


package de.tud.kom.p2psim.impl.vis.gnuplot;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.controller.player.Player;
import de.tud.kom.p2psim.impl.vis.model.EventTimeline;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Generiert Ergebnistabellen aus dem Datenmodell.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnuplotExporter {

	/**
	 * Generiert eine Ergebnistabelle für die in metrics angegebenen gebundenen
	 * Metriken gegen Zeit. Dabei bestimmen start und end den Start und
	 * Endzeitpunkt, interval das Intervall der Abtastung für die gewählten
	 * Metriken.
	 * 
	 * @param metrics
	 * @param start
	 * @param end
	 * @param interval
	 * @return
	 */
	public ResultTable generateResultTable(Collection<BoundMetric> metrics,
			long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying())
			pl.pause();

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(metrics);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			// System.out.println("Position: " + pos);
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (BoundMetric m : metrics) {

				String mValue = m.getValue();
				if (mValue == null)
					mValue = "0";

				values.setValueForAt(m, i, mValue);
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

	/**
	 * Generiert eine Ergebnistabelle für die in nodes angegebenen Knoten gegen
	 * Zeit bzgl der Knotenmetrik metric. Dabei bestimmen start und end den
	 * Start und Endzeitpunkt, interval das Intervall der Abtastung für die
	 * gewählten Metriken.
	 * 
	 * @param nodes
	 * @param metric
	 * @param start
	 * @param end
	 * @param interval
	 * @return
	 */
	public ResultTable generateOneMetricPeersVsTime(List<VisOverlayNode> nodes,
			OverlayNodeMetric metric, long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying())
			pl.pause();

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(nodes);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (VisOverlayNode n : nodes) {
				String mValue = metric.getValue(n);

				if (mValue == null)
					mValue = "0"; // Verhalten, wenn ein Wert nicht definiert
									// ist: er wird 0 gesetzt.

				values.setValueForAt(n, i, metric.getValue(n));
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

	/**
	 * Generiert eine Ergebnistabelle für die in edges angegebenen Kanten gegen
	 * Zeit bzgl der Kantenmetrik metric. Dabei bestimmen start und end den
	 * Start und Endzeitpunkt, interval das Intervall der Abtastung für die
	 * gewählten Metriken.
	 * 
	 * @param edges
	 * @param metric
	 * @param start
	 * @param end
	 * @param interval
	 * @return
	 */
	public ResultTable generateOneMetricConnVsTime(List<VisOverlayEdge> edges,
			OverlayEdgeMetric metric, long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying())
			pl.pause();

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(edges);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (VisOverlayEdge e : edges) {

				String mValue = metric.getValue(e);

				if (mValue == null)
					mValue = "0"; // Verhalten, wenn ein Wert nicht definiert
									// ist: er wird 0 gesetzt.

				values.setValueForAt(e, i, mValue);
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

}
