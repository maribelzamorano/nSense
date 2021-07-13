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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages;

import org.apache.commons.math.random.RandomGenerator;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * All classes in this package are only for use by the KBR application. Abstract
 * KBR filesharing message.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class FilesharingMessage implements Message {

	private static RandomGenerator rGen = Simulator.getRandom();

	/**
	 * Generates a new Query UID
	 * 
	 * @return
	 */
	public static long generateQueryUID() {
		return rGen.nextInt() << 32 + rGen.nextInt();
	}

}
