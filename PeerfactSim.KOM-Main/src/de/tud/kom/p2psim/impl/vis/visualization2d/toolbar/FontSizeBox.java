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

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class FontSizeBox extends JPanel implements ActionListener{

	public static final String CONF_PATH = "Visualization/FontSize";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2981360948327157441L;
	
	private static final String TOOL_TIP = "Schriftgröße";

	protected JComboBox box;
	
	public FontSizeBox() {
		box = new JComboBox(new Integer[] { 8, 10, 12, 15, 18, 20 });
		box.setEditable(true);
		box.addActionListener(this);
		box.setSelectedItem(Config.getValue(CONF_PATH, "10"));
		
		this.setLayout(new BorderLayout());

		this.add(box, BorderLayout.CENTER);
		this.add(new JLabel(new ImageIcon("images/icons/misc/font_size16_16.png")), BorderLayout.WEST);

		this.setToolTipText(TOOL_TIP);
		box.setToolTipText(TOOL_TIP);	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Integer.valueOf(box.getSelectedItem().toString());
			Config.setValue(CONF_PATH, box.getSelectedItem().toString());
			VisDataModel.needsRefresh();
		} catch (NumberFormatException ex) {
			System.out.println("Schriftgröße keine Zahl");
			box.setSelectedItem(Config.getValue(CONF_PATH, "10"));
		}
	}
	
}
