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


package de.tud.kom.p2psim.impl.vis.visualization2d.toolbar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.visualization2d.Simple2DVisualization;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class NodeSizeMetricBox extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7507793063049512488L;

	private static final String TOOLTIP = "Durch Knotengröße darstellen";

	private static final String NULL_OBJECT = "--konstant--";

	private static final ImageIcon ICON = new ImageIcon(
			"images/icons/misc/node_size16_16.png");

	Simple2DVisualization vis;

	JComboBox box;

	public NodeSizeMetricBox(Simple2DVisualization vis) {
		box = new JComboBox(appendNull());
		box.setToolTipText(TOOLTIP);
		box.addActionListener(this);
		// this.setEditable(true);
		this.vis = vis;

		this.setToolTipText(TOOLTIP);
		this.setLayout(new BorderLayout());
		this.add(box, BorderLayout.CENTER);
		this.add(new JLabel(ICON), BorderLayout.WEST);

	}

	private static Vector<Object> appendNull() {
		Vector<Object> v = new Vector<Object>();
		v.add(NULL_OBJECT);
		for (OverlayNodeMetric m : MetricsBase.forOverlayNodes()
				.getListOfAllMetrics())
			if (m.isNumeric())
				v.add(m);
		return v;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (box.getSelectedItem() != NULL_OBJECT)
			vis.setNodeSizeMetric((OverlayNodeMetric) box.getSelectedItem());
		else
			vis.setNodeSizeMetric(null);
		VisDataModel.needsRefresh();
	}

}
