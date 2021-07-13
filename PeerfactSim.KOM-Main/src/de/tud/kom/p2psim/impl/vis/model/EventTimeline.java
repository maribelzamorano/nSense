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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;

import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.vis.model.events.EdgeFlashing;
import de.tud.kom.p2psim.impl.vis.model.events.Event;

/**
 * Event-Zeitlinie. Trägt alle Events, macht sie zu ihrem Zeitpunkt geschehen
 * und rückgängig. Wird vom Player gesteuert, kann aber auch beliebig per Hand
 * gesteuert werden
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class EventTimeline implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5906861906836376909L;

	public static ArrayList<TimelineEventListener> listeners = new ArrayList<TimelineEventListener>();

	private long maxTime;

	/**
	 * Aktueller virtueller Zeitpunkt
	 */
	private long actTime;

	protected TreeMap<Long, ArrayList<Event>> timeline = new TreeMap<Long, ArrayList<Event>>();

	/**
	 * EventTimeline mit Maximalzeit 0
	 */
	public EventTimeline() {
		this(0);
	}

	/**
	 * EventTimeline mit Maximalzeit maxTime
	 * 
	 * @param maxTime
	 */
	public EventTimeline(int maxTime) {
		this.maxTime = maxTime;
	}

	public long getActualTime() {
		return this.actTime;
	}

	/**
	 * Gibt alle Events in einem langen Array zurück, die von <b>begin</b>
	 * (inklusiv) bis <b>end</b> (exklusiv) auftreten.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public ArrayList<Event> getEventsBetween(long begin, long end) {

		synchronized (this) {

			ArrayList<Event> result = new ArrayList<Event>();

			for (ArrayList<Event> l : this.getMappedEventsBetween(begin, end)
					.values()) {
				for (Event e : l) {
					result.add(e);
				}
			}

			return result;

		}
	}

	/**
	 * Gibt alle Events zurück, die von <b>begin</b> (inklusiv) bis <b>end</b>
	 * (exklusiv) auftreten.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public SortedMap<Long, ArrayList<Event>> getMappedEventsBetween(long begin,
			long end) {

		return timeline.subMap(begin, end);
	}

	/**
	 * Springt zu einem Zeitpunkt
	 * 
	 * @param time
	 */
	public synchronized void jumpToTime(long newTime) {

		synchronized (this) {

			if (newTime == actTime)
				return;

			if (newTime > maxTime)
				newTime = maxTime;
			else if (newTime < 0)
				newTime = 0;

			boolean reverse = newTime < actTime;

			long stepWide = Math.abs(newTime - actTime);

			SortedMap<Long, ArrayList<Event>> betwElem;
			if (!reverse)
				betwElem = timeline.subMap(actTime + 1, newTime + 1);
			else
				betwElem = timeline.subMap(newTime + 1, actTime + 1);

			if (!reverse) {
				for (ArrayList<Event> events : betwElem.values()) {

					for (Event e : events) {
						/*
						 * Leave out flash events since they tend to overload
						 * the visualization upon larger jumps on the time-line
						 */
						if (!(e instanceof EdgeFlashing))
							e.makeHappen();
						else if (stepWide < Simulator.SECOND_UNIT * 10) {
							/*
							 * make flashing edges only happen if the step wide
							 * is smaller than 10 seconds.
							 */
							e.makeHappen();
						}
					}
				}
			} else {

				/*
				 * Rückwärts iterieren ist schwierig und mit Aufwand verbunden,
				 * da die Blätter im TreeSet nicht rückwärts verkettet werden.
				 */
				ArrayList<ArrayList<Event>> it_list = new ArrayList<ArrayList<Event>>(
						betwElem.values());

				ListIterator<ArrayList<Event>> it = it_list
						.listIterator(it_list.size());
				while (it.hasPrevious()) {

					ArrayList<Event> events = it.previous();

					ListIterator<Event> it2 = events
							.listIterator(events.size());
					while (it2.hasPrevious()) {
						Event e = it2.previous();

						e.undoMakeHappen();
					}
				}
			}

			this.actTime = newTime;
		}

		notifyActualTimeChanged(this);

		VisDataModel.needsRefresh();
	}

	/**
	 * Setzt ein Ereignis e am Zeitpunkt t ein.
	 * 
	 * @param e
	 * @param t
	 */
	public void insertEvent(Event e, long t) {

		synchronized (this) {

			ArrayList<Event> actual_tl = timeline.get(t);

			if (actual_tl == null) {

				ArrayList<Event> te = new ArrayList<Event>();
				te.add(e);

				timeline.put(t, te);

			} else {
				actual_tl.add(e);
			}
			if (t > maxTime) {
				maxTime = t;
				notifyMaxTimeChanged(this);
			}

		}

	}

	/**
	 * Noch nicht fertig!
	 * 
	 * @param e
	 */
	public void removeEvent(Event e) {
		// TODO
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	@Override
	public String toString() {
		return this.timeline.toString();
	}

	public void reset() {
		this.actTime = -1;
		notifyActualTimeChanged(this);
	}

	public static void addEventListener(TimelineEventListener l) {
		listeners.add(l);
	}

	protected static void notifyActualTimeChanged(EventTimeline tl) {
		for (TimelineEventListener l : listeners)
			l.actualTimeChanged(tl);
	}

	protected static void notifyMaxTimeChanged(EventTimeline tl) {
		for (TimelineEventListener l : listeners)
			l.maxTimeChanged(tl);
	}

}
