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

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.controller.commands.Command;
import de.tud.kom.p2psim.impl.vis.ui.common.config.ConfigWindow;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConfigButton extends SimpleToolbarButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9023226573863235349L;

	static ImageIcon icon = new ImageIcon("images/icons/ConfigButton.png");

	static String tooltip = "Einstellungen...";

	public ConfigButton() {
		super();
		this.setIcon(icon);
		this.setToolTipText(tooltip);
		this.addCommand(new Command() {

			@Override
			public void execute() {
				new ConfigWindow().setVisible(true);
			}

		});
	}
}
