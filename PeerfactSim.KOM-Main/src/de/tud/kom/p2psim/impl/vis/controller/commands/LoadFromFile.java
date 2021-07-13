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
import de.tud.kom.p2psim.impl.vis.ui.common.dialogs.RecordFileChooser;

/**
 * Öffnet eine Datei
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LoadFromFile implements Command {

	@Override
	public void execute() {

		if (Controller.getModel() != null && Controller.getModel().isUnsaved()) {
			switch (JOptionPane
					.showConfirmDialog(
							Controller.getUIMainWindow(),
							"Die aktuellen Visualisierungsaufzeichnungen wurden noch nicht gespeichert. Sollen sie jetzt gespeichert werden?",
							"Laden einer Aufzeichnung",
							JOptionPane.YES_NO_CANCEL_OPTION)) {
			case JOptionPane.CANCEL_OPTION:
				return;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.YES_OPTION:
				new SaveToFile().execute();

			}
		}

		RecordFileChooser fc = new RecordFileChooser();
		if (fc.askForOpen()) {

			Controller.getModel().setUnsaved(false);
		}

	}

}
