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


package de.tud.kom.p2psim.impl.vis.util.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/**
 * Ein Tabellenmodell, in dem verschiedene Objekte ausgewählt werden können, und
 * eine Liste dieser Objekte zurückgegeben werden kann.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TObj>
 * @version 05/06/2011
 */
public class IconObjectTableModel<TObj extends IconObject> extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7789895021882856840L;

	List<TObj> objects;

	Set<TObj> selected = new HashSet<TObj>();

	String caption;

	public IconObjectTableModel(List<TObj> objects, String caption) {
		this.objects = objects;
		this.caption = caption;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "";
		else if (col == 1)
			return caption;
		else
			return "";
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return objects.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0)
			return objects.get(row).getRepresentingIcon();
		else if (col == 1)
			return objects.get(row).toString();
		else {
			return selected.contains(objects.get(row));
		}
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 2) {
			if ((Boolean) value)
				selected.add(objects.get(row));
			else
				selected.remove(objects.get(row));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
		if (c == 0)
			return ImageIcon.class;
		if (c == 1)
			return String.class;
		else
			return Boolean.class;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return (col == 2);
	}

	public List<TObj> getListOfSelectedObjects() {
		return new ArrayList<TObj>(selected);

	}

	/**
	 * Wählt entweder alle Einträge der Liste, oder wählt alle ab.
	 * 
	 * @param value
	 */
	public void setAllSelected(boolean value) {
		if (!value) {
			selected.clear();
		} else {
			selected.addAll(objects);
		}
	}

}
