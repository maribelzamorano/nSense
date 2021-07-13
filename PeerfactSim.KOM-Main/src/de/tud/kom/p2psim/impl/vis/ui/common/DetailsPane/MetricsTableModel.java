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


package de.tud.kom.p2psim.impl.vis.ui.common.DetailsPane;

import java.awt.Color;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;

/**
 * Modell der allgemeinen Metriktabelle
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MetricsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8805264033914855097L;

	private List<Metric> getMetrics() {
		return MetricsBase.getAllMetrics();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return getMetrics().size();
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "";
		else if (col == 1)
			return "";
		else if (col == 2)
			return "Name";
		else
			return "Zeigen";
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0)
			return getMetrics().get(row).getRepresentingIcon();
		if (col == 1)
			return getMetrics().get(row).getColor();
		else if (col == 2)
			return getMetrics().get(row).getName();
		else
			return Boolean.valueOf(getMetrics().get(row).isActivated());
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0)
			return ImageIcon.class;
		else if (c == 1)
			return Color.class;
		else if (c == 2)
			return String.class;
		else
			return Boolean.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 3) {
			getMetrics().get(row).setActivated((Boolean) value);
			VisDataModel.needsRefresh();
		}

	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 3;
	}

}
