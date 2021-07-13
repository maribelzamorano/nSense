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


package de.tud.kom.p2psim.impl.util.guirunner.progress;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;


/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ProgressValuesListModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4835422753977045344L;

	List<ProgressValue> progValues;

	public ProgressValuesListModel(List<ProgressValue> progValues) {
		this.progValues = progValues;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return progValues.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0)
			return progValues.get(row).getName();
		else
			return progValues.get(row).getValue();
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "";
		else
			return "Wert";
	}

	@Override
	public Class<? extends Object> getColumnClass(int c) {
		return String.class;
	}
}
