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


package de.tud.kom.p2psim.impl.skynet.analyzing;

import java.io.File;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class AbstractSkyNetWriter implements SimulationEventHandler {

	private static Logger log = SimLogger.getLogger(AbstractSkyNetWriter.class);

	protected void initWriteDirectory(String dataPath, boolean clean) {
		File dir = new File(dataPath);
		if (dir.mkdir()) {
			log.warn("Created Directory " + dir.getName());
		} else if (clean) {
			String[] list = dir.list();
			File f;
			for (int i = 0; i < list.length; i++) {
				f = new File(dir.getPath() + File.separatorChar + list[i]);
				f.delete();
			}
			log.warn("Cleaned Directory " + dir.getName()
					+ " for new simulation.");
			if (dir.list().length != 0) {
				log.error("Directory " + dir.getName() + " is not emtpy.");
			}
		} else {
			log.warn("Directory " + dir.getName()
					+ " already exists and needs not be cleaned.");
		}
	}

}
