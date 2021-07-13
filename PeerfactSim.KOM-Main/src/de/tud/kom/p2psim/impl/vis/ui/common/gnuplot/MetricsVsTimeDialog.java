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
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.gnuplot.GnuplotExporter;
import de.tud.kom.p2psim.impl.vis.gnuplot.ResultTable;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricsVsTimeDialog extends MetricVsTimeBasisPanel {

	private static final long serialVersionUID = 8884409185702564261L;

	// private JPanel checkBoxPanel;

	private Collection<BoundMetric> m = new Vector<BoundMetric>();

	private ArrayList<JCheckBox> ListOfCheckBox;

	private static final String MANUAL_TXT = "Stellt ausgewählte Metriken für das gerade ausgewählte Objekt in einem Graphen dar."
			+ "Wählen Sie die gewünschten Metriken, Start- und "
			+ "Endzeitpunkt, und den Intervall, in dem die Metrik abgetastet werden soll.";

	public MetricsVsTimeDialog() {
		for (BoundMetric metr : Controller.getVisApi().getSelectedObject()
				.getBoundMetrics()) {
			if (metr.isNumeric())
				m.add(metr);
		}
		createContentPanel();

		JTextArea manual = new JTextArea(MANUAL_TXT);
		manual.setLineWrap(true);
		manual.setWrapStyleWord(true);
		manual.setOpaque(false);
		this.add(manual, BorderLayout.NORTH);
	}

	public void createContentPanel() {
		createCheckBox();
	}

	private void createCheckBox() {
		/*
		 * checkBoxPanel = new JPanel(); checkBoxPanel.setLayout(null);
		 * checkBoxPanel.setBounds(55, 20, 200, 200);
		 */

		ListOfCheckBox = new ArrayList<JCheckBox>();

		JPanel checkBox = new JPanel();
		// checkBox.setBorder(BorderFactory.createLoweredBevelBorder());
		checkBox.setBackground(Color.WHITE);
		checkBox.setPreferredSize(new Dimension(150, 150));
		checkBox.setBounds(50, 50, 200, 200);
		checkBox.setLayout(new BoxLayout(checkBox, BoxLayout.PAGE_AXIS));

		for (BoundMetric boundMetric : m) {
			JCheckBox box = new JCheckBox(boundMetric.getName());
			ListOfCheckBox.add(box);
			checkBox.add(box);
		}

		this.add(new JScrollPane(checkBox), BorderLayout.CENTER);
	}

	protected ResultTable createTable(long[] values) {
		// << das sind die in der CheckBox ausgewählten.
		Collection<BoundMetric> selectedMetrics = new Vector<BoundMetric>();
		for (JCheckBox checbox : ListOfCheckBox)
			if (checbox.isSelected()) {
				String metric = checbox.getText();
				for (BoundMetric boundMetric : m)
					if (boundMetric.getName().equals(metric)) {
						selectedMetrics.add(boundMetric);
						break;
					}
			}
		return new GnuplotExporter().generateResultTable(selectedMetrics,
				values[0], values[1], values[2]);
	}

}
