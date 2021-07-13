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


package de.tud.kom.p2psim.impl.vis.model.flashevents;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Queue;

import de.tud.kom.p2psim.impl.vis.model.ModelFilter;
import de.tud.kom.p2psim.impl.vis.model.ModelIterator;

/**
 * Behandelt FlashEvents, z.B. iteriert über sie.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 21.10.2008
 * 
 */
public class FlashEventHandler {

	Queue<FlashEvent> notPainted = new LinkedList<FlashEvent>();

	Queue<FlashEvent> painted_events = new LinkedList<FlashEvent>();

	private ModelFilter filter = null;

	public void addFlashEvent(FlashEvent e) {
		notPainted.add(e);
	}

	/**
	 * Setzt alle Einträge zurück.
	 */
	public void reset() {
		notPainted.clear();
		painted_events.clear();
	}

	/**
	 * Setzt alle Objekte als gezeichnet und iteriert dann über sie. Dies hat
	 * den Vorteil, dass der iterierende Thread nicht mit
	 * addFlashEvent()-Aufrufen in die Quere kommt und eine
	 * ConcurrentModificationException wirft.
	 * 
	 * @see ConcurrentModificationException
	 * @param it
	 */
	public void iterateAndSetPainted(ModelIterator it) {

		synchronized (painted_events) {
			painted_events.clear();
		}

		while (!notPainted.isEmpty()) {
			FlashEvent e = notPainted.remove();
			if (filter == null || filter.typeActivated(e)) {
				e.iterate(it);
				synchronized (painted_events) {
					painted_events.add(e);
				}
			}
		}
	}

	/**
	 * Iteriert it über alle Events, die momentan auf dem Bildschirm gezeichnet
	 * sind.
	 * 
	 * @param it
	 */
	public void iteratePaintedEvents(ModelIterator it) {
		synchronized (painted_events) {
			for (FlashEvent e : painted_events) {
				if (filter == null || filter.typeActivated(e)) {
					e.iterate(it);
				}
			}
		}
	}

	public void setEventsPainted() {
		synchronized (painted_events) {
			painted_events = notPainted;
		}
		notPainted = new LinkedList<FlashEvent>();
	}

	public void setFilter(ModelFilter filter) {
		this.filter = filter;
	}

}
