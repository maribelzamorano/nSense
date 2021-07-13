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


package de.tud.kom.p2psim.impl.vis.ui.common.toolbar.elements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class LoopButton extends JToggleButton implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5186748047384510370L;

	static final ImageIcon icon = new ImageIcon("images/icons/LoopButton.png");

	static final String tooltip = "Abspielen wiederholen";

	static final String CONF_PATH = "Player/Loop";

	public LoopButton() {
		super();
		this.setIcon(icon);
		this.setToolTipText(tooltip);
		this.addActionListener(this);

		this.getModel().setSelected((Config.getValue(CONF_PATH, 0)) != 0);

		Controller.getPlayer().setLooping(this.getModel().isSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Controller.getPlayer().setLooping(this.getModel().isSelected());

		if (this.getModel().isSelected())
			Config.setValue(CONF_PATH, 1);
		else
			Config.setValue(CONF_PATH, 0);
	}

}
