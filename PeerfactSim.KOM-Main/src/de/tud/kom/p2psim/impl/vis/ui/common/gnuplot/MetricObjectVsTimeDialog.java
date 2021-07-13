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
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.util.gui.IconObject;
import de.tud.kom.p2psim.impl.vis.util.gui.IconObjectTableModel;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class MetricObjectVsTimeDialog<TMetric extends Metric, TObject extends IconObject>
		extends MetricVsTimeBasisPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7946040448823203271L;

	JTable objects_table;

	JTable metrics_table;

	List<TMetric> metrics;

	IconObjectTableModel<TObject> model;

	PopupMenu selectAllMenu;

	public MetricObjectVsTimeDialog() {

		this.metrics = getMetrics();
		model = new IconObjectTableModel<TObject>(getObjects(), "Objekt");
		objects_table = new JTable(model);

		setUpContextMenu();

		objects_table.getColumnModel().getColumn(0).setPreferredWidth(25);
		objects_table.getColumnModel().getColumn(0).setMaxWidth(35);
		objects_table.getColumnModel().getColumn(1).setPreferredWidth(15);
		objects_table.getColumnModel().getColumn(2).setPreferredWidth(25);
		objects_table.getColumnModel().getColumn(2).setMaxWidth(35);

		metrics_table = new JTable(new MetricTableModel<TMetric>(metrics));
		metrics_table.getColumnModel().getColumn(0).setPreferredWidth(25);
		metrics_table.getColumnModel().getColumn(0).setMaxWidth(35);
		metrics_table.getColumnModel().getColumn(1).setPreferredWidth(15);

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(objects_table), new JScrollPane(metrics_table));
		sp.setDividerLocation(200);

		this.add(sp, BorderLayout.CENTER);
	}

	private void setUpContextMenu() {

		selectAllMenu = new SelectAllPopupMenu();
		objects_table.add(selectAllMenu);

		objects_table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handleContextMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				handleContextMenu(e);
			}
		});
	}

	void handleContextMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			selectAllMenu.show(objects_table, e.getX(), e.getY());
		}
	}

	public TMetric getSelectedMetric() {
		return metrics.get(metrics_table.getSelectedRow());
	}

	public abstract List<TMetric> getMetrics();

	/**
	 * Gibt eine Liste aller auszuwählenden Objekte zurück.
	 * 
	 * @return
	 */
	public abstract List<TObject> getObjects();

	public List<TObject> getListOfSelectedObjects() {
		return model.getListOfSelectedObjects();
	}

	public class SelectAllPopupMenu extends PopupMenu implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7917024670304660576L;

		MenuItem selectAll;

		MenuItem selectNone;

		public SelectAllPopupMenu() {
			super();

			selectAll = new MenuItem("Alle auswählen");
			selectNone = new MenuItem("Alle abwählen");
			selectAll.addActionListener(this);
			selectNone.addActionListener(this);
			this.add(selectAll);
			this.add(selectNone);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == selectAll)
				model.setAllSelected(true);
			else if (e.getSource() == selectNone)
				model.setAllSelected(false);
			objects_table.repaint();
		}

	}
}
