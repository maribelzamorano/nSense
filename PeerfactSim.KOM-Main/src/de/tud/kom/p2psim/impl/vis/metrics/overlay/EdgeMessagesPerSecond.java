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


package de.tud.kom.p2psim.impl.vis.metrics.overlay;

import java.awt.Color;
import java.util.ArrayList;

import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.controller.player.Player;
import de.tud.kom.p2psim.impl.vis.model.events.Event;
import de.tud.kom.p2psim.impl.vis.model.events.MessageSent;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Messages pro Sekunde für Kanten, ermittelt mit einer Look-Back-Strategie.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class EdgeMessagesPerSecond extends OverlayEdgeMetric{

	protected final String look_back_path =  base_path + "/SecondsLookBack";
	
	/**
	 * Sekunden, die in die Vergangenheit geschaut wird, um die Anzahl
	 * vergangener Messages zu berechnen.
	 */
	final int SECONDS_LOOK_BACK = Config.getValue(look_back_path, 50);
	
	public EdgeMessagesPerSecond() {
		this.setColor(new Color (0, 128, 0));
	}
	
	@Override
	public String getValue(VisOverlayEdge edge) {
		long t = Controller.getTimeline().getActualTime();
		
		return String.valueOf((float)this.getEventCount(t - Player.TIME_UNIT_MULTIPLICATOR*SECONDS_LOOK_BACK
					, t, edge)/(float)SECONDS_LOOK_BACK);
		
	}

	protected long getEventCount(long begin, long end, VisOverlayEdge edge) {
		
		int count = 0;
		
		for (ArrayList<Event> l : Controller.getTimeline().getMappedEventsBetween(begin, end).values()) {
			for (Event e : l) {
				if (e instanceof MessageSent) {
					MessageSent ms = (MessageSent)e;
					if (ms.getFrom() == edge.getNodeA() && ms.getTo() == edge.getNodeB()) {
						count++;
					} else if (ms.getTo() == edge.getNodeA() && ms.getFrom() == edge.getNodeB()) {
						count++;
					}
				}
			}
		}
		
		return count;
	}
	
	@Override
	public String getName() {
		return "Messages pro Sekunde";
	}

	public String toString() {
		return getName();
	}

	@Override
	public String getUnit() {
		return "msg/s";
	}

	@Override
	public boolean isNumeric() {
		return true;
	}
}
