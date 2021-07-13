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

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;

/**
 * Modell der ObjectMetricsTable.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ObjectMetricsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8805264033914855097L;

	Vector<BoundMetric> metrics;

	public ObjectMetricsTableModel(Vector<BoundMetric> metrics) {
		this.metrics = metrics;
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
	public String getColumnName(int col) {
		if (col == 0)
			return "Name";
		else
			return "Wert";
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0)
			return metrics.elementAt(row).getName();
		else {
			String mValue = metrics.elementAt(row).getValue();
			if (mValue == null)
				return getNullView();
			return mValue;
		}
	}

	/**
	 * Wird an die Zelle zum Anzeigen zurückgegeben, falls die Metrik keinen
	 * Wert hat.
	 * 
	 * @return
	 */
	private Object getNullView() {
		return "<html><b>---null---</b></html>";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		//Nothing to do
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

}
