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


package de.tud.kom.p2psim.impl.overlay.ido.visualization;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/**
 * This class create a {@link JList}, that can contain checkboxes.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 02/03/2011
 * 
 */
public class CheckBoxList extends JList {

	/**
	 * Constructor for the {@link CheckBoxList}. To fill the list with data, use
	 * the setModel method. Please fill only
	 */
	public CheckBoxList() {

		setCellRenderer(new CellRenderer());
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(
							index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public void setModel(ListModel model) {
		for (int i = 0; i < model.getSize(); i++) {
			if (!(model.getElementAt(i) instanceof JCheckBox)) {
				throw new IllegalArgumentException(
						"The model has a data field with no instance of a JCheckBox!");
			}
		}
		super.setModel(model);
	}

	/**
	 * The CellRender is used by the {@link CheckBoxList} to render the cell
	 * from the list. The data of the list should be only JCheckBoxes.
	 * 
	 * @author Christoph Münker
	 */
	protected class CellRenderer implements ListCellRenderer {

		/**
		 * How to paint the checkboxes in a cell of a JList.
		 * 
		 * @param list
		 *            The JList we're painting.
		 * @param value
		 *            The value returned by list.getModel().getElementAt(index).
		 *            It should be a {@link JCheckBox}.
		 * @param index
		 *            The cells index.
		 * @param isSelected
		 *            True if the specified cell was selected.
		 * @param cellHasFocus
		 *            True if the specified cell has the focus.
		 * 
		 * @return A component whose paint() method will render the specified
		 *         value.
		 */
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(getBackground());
			checkbox.setForeground(getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(null);
			return checkbox;
		}
	}

}
