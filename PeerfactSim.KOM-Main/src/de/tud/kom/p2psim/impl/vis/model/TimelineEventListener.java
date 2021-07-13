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


package de.tud.kom.p2psim.impl.vis.model;

/**
 * Wartet auf Ereignisse der Timeline.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface TimelineEventListener {

	/**
	 * Wird ausgeführt, wenn der Zeitpunkt auf der Timeline sich geändert hat.
	 * 
	 * @param invoker
	 */
	public void actualTimeChanged(EventTimeline invoker);

	/**
	 * Wird ausgeführt, wenn der größte Zeitpunkt auf der Timeline sich
	 * geändert hat.
	 * 
	 * @param invoker
	 */
	public void maxTimeChanged(EventTimeline invoker);

}
