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


package de.tud.kom.p2psim.impl.vis.controller.commands;

import javax.swing.JOptionPane;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.ui.common.gnuplot.GnuplotMainWindow;

/**
 * Öffnet das Gnuplot-Dialogfenster.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class ExportGnuplot implements Command{

	@Override
	public void execute() {
		
		if (Controller.getModel() == null) {
		
			JOptionPane.showMessageDialog(Controller.getUIMainWindow(), "Es ist keine Aufzeichnung vorhanden. Bitte laden Sie zunächst eine Aufzeichnung", 
				"Gnuplot-Export", JOptionPane.ERROR_MESSAGE);
		} else startNormal();

	}

	private void startNormal() {
		try {
			
			/*
			 * FIXME: warum keine Err-Ausgabe? 
			 */
			
			//Controller.getUIMainWindow().setEnabled(false);
			new GnuplotMainWindow().setVisible(true);
					
			//test1();
		
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
		
}
