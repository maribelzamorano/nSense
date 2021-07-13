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


package de.tud.kom.p2psim.impl.vis;

import java.io.File;
import java.io.IOException;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;

/**
 * Startet die Anwendung im Standalone-Modus, z.B. zum Abspielen von
 * Aufzeichnungen o.Ä.
 * 
 * 
 * See <a href="http://www.student.informatik.tu-darmstadt.de/~l_nobach/docs/howto-visualization.pdf"
 * >PeerfactSim.KOM Visualization HOWTO</a> on how to use the visualization.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RunStandalone {

	/**
	 * Startet die Anwendung im Standalone-Modus, z.B. zum Abspielen von
	 * Aufzeichnungen o.Ä.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Controller.init();
		if (args.length > 0) {
			System.out.println("Lade im Argument übergebene Datei: \""
					+ args[0] + "\"...");
			try {
				File f = new File(args[0]);
				Controller.loadModelFrontend(VisDataModel.fromFile(f));
				Controller.getModel().setName(f.getName());
			} catch (IOException e) {
				System.out.println("Datei ließ sich nicht laden: "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
