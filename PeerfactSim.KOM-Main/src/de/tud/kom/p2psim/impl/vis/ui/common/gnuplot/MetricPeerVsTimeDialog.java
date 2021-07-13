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


package de.tud.kom.p2psim.impl.vis.ui.common.gnuplot;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextArea;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.gnuplot.GnuplotExporter;
import de.tud.kom.p2psim.impl.vis.gnuplot.ResultTable;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Node;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricPeerVsTimeDialog extends MetricObjectVsTimeDialog<OverlayNodeMetric, VisOverlayNode>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7516896865939958010L;

	private static final String MANUAL_TXT = "Stellt ausgewählte Peers bezüglich einer Metrik in einem Graph gegenüber. " +
	 "Wählen Sie die zu plottenden Peers und die gewünschte Metrik, Start- und " +
	 "Endzeitpunkt, und den Intervall, in dem die Metrik abgetastet werden soll.";

	public MetricPeerVsTimeDialog() {
		JTextArea manual = new JTextArea(MANUAL_TXT);
		manual.setLineWrap(true);
		manual.setWrapStyleWord(true);
		manual.setOpaque(false);
		this.add(manual, BorderLayout.NORTH);
	}
	
	@Override
	public List<OverlayNodeMetric> getMetrics() {
		Vector<OverlayNodeMetric> result = new Vector<OverlayNodeMetric>();
		for (OverlayNodeMetric m : MetricsBase.forOverlayNodes().getListOfAllMetrics()) {
			if (m.isNumeric()) result.add(m);		//Nur Metriken, die numerisch sind, werden verwendet.
		}
		
		return result;
	}

	@Override
	protected ResultTable createTable(long[] values) {
		return new GnuplotExporter().generateOneMetricPeersVsTime(this.getListOfSelectedObjects(), this.getSelectedMetric(), values[0], values[1], values[2]);
	}

	@Override
	public List<VisOverlayNode> getObjects() {
		List<VisOverlayNode> res = new Vector<VisOverlayNode>();
		for (Node<VisOverlayNode, VisOverlayEdge> n : Controller.getModel().getOverlayGraph().nodes) {
			res.add((VisOverlayNode) n);
		}
		return res;
	}

}
