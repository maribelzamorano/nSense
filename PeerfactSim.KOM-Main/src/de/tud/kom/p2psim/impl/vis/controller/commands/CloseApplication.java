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
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Schließt die Anwendung
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */

public class CloseApplication implements Command{
	
	public static final String CONF_PATH = "UI/MainWindow/ForceClose";
	
	@Override
	public void execute() {
		
		if (! forceClose()) {
		
			if (Controller.getModel() != null && Controller.getModel().isUnsaved()) {
				switch (JOptionPane.showConfirmDialog(Controller.getUIMainWindow(), 
						"Die aktuellen Visualisierungsaufzeichnungen wurden noch nicht gespeichert. Sollen sie jetzt gespeichert werden?", 
						"Beenden", JOptionPane.YES_NO_CANCEL_OPTION)) {
				case JOptionPane.CANCEL_OPTION : return;
				case JOptionPane.NO_OPTION : break;
				case JOptionPane.YES_OPTION : new SaveToFile().execute();
				
				}
			}
			
			
			/*	//"Wirklich beenden?" ist zu blöd. Gerade wenn man öfters seine Impl. testet.
			
			if (JOptionPane.showConfirmDialog(Controller.getUIMainWindow(), 
					"Wirklich beenden?", 
					"Beenden", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) Controller.deinit();
		
			*/
			
		}
		
		Controller.deinit();
		
	}

	private boolean forceClose() {
		return Config.getValue(CONF_PATH, false);
	}
}
