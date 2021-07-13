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

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class CreateGnuplotFileButtons extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton gnuplotButton;

	public CreateGnuplotFileButtons() {

		this.setLayout(new FlowLayout());
		this.setSize(250, 50);
		gnuplotButton = new JButton("Gnuplot-Graphen exportieren...");
		gnuplotButton.setFont(gnuplotButton.getFont().deriveFont(Font.BOLD));
		this.add(gnuplotButton);
	}

	public JButton getGnuplotButton() {
		return gnuplotButton;
	}

}
