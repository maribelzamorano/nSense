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



package de.tud.kom.p2psim.api.simengine;

import de.tud.kom.p2psim.impl.simengine.SimulationEvent;


/**
 * An SimulationEventHandler waits for incoming <code>SimulationEvents</code>
 * events (acts as a listener) and performs various actions depending on the
 * type of the occurred event.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public interface SimulationEventHandler {
	/**
	 * This method passes an occurred SimulationEvent to its appropriate
	 * EventHandler
	 * 
	 * @param se
	 *            the SimulationEvent to handle
	 */
	public void eventOccurred(SimulationEvent se);
}
