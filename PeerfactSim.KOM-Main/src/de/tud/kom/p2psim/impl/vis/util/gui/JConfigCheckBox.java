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

import javax.swing.JCheckBox;

import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Wie JCheckBox, nur dass der Wert dieser CheckBox in der XML-Config
 * gespeichert wird, unter configPath
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JConfigCheckBox extends JCheckBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4395710891110224842L;

	public String configPath;

	public JConfigCheckBox(String caption, String configPath) {
		super(caption);
		create(configPath);
	}

	public JConfigCheckBox(String configPath) {
		create(configPath);
	}

	/**
	 * Pseudokonstruktor
	 * 
	 * @param Pfad
	 *            zur Config
	 */
	public void create(String configPath) {
		this.configPath = configPath;
		if (Config.getValue(configPath, 0) == 1) {
			this.setSelected(true);
		}
	}

	public void saveSettings() {
		int value = 0;
		if (this.isSelected())
			value = 1;

		Config.setValue(configPath, value);
	}
}
