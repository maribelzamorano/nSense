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

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MetricTableModel<TMetric extends Metric> extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7789895021882856840L;

	protected static final ImageIcon metric_icon = new ImageIcon("images/icons/misc/Metric16_16.png");
	
	List<TMetric> metrics;
	
	public MetricTableModel(List<TMetric> metrics) {
		this.metrics = metrics;
	}
	
	@Override
	public String getColumnName(int col) {
		if (col == 0) 
			return "";
		else 
			return "Metrik";
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return metrics.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) return metric_icon;
		else return metrics.get(row).getName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
		if (c == 0) return ImageIcon.class;
		else return String.class;
    }

}
