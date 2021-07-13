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


package de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation;

import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;

/**
 * Event dispatcher that forwards events that occured in a Gnutella overlay
 * to its listeners. The dispatcher is a listener itself and must be connected
 * to an overlay node before in order to receive events.
 *
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaEvents implements IGnutellaEventListener {

	private static GnutellaEvents inst;

	List<IGnutellaEventListener> listeners = new LinkedList<IGnutellaEventListener>();

	/**
	 * There is a global event dispatcher. This method returns it.
	 * @return
	 */
	public static GnutellaEvents getGlobal() {
		if (inst == null)
			inst = new GnutellaEvents();
		return inst;
	}

	/**
	 * Creates a new event dispatcher. Note that there is a global one
	 * that can be accessed through <b>GnutellaEvents.getGlobal()</b>
	 */
	public GnutellaEvents() {
		// Nothing to do
	}

	/**
	 * Adds a Gnutella event listener to this dispatcher
	 * @param l
	 */
	public void addListener(IGnutellaEventListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a Gnutella event listener from this dispatcher
	 * @param l
	 */
	public void removeListener(IGnutellaEventListener l) {
		listeners.remove(l);
	}

	@Override
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		for (IGnutellaEventListener l : listeners)
			l.connectionStarted(invoker, receiver, connectionUID);
	}

	@Override
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		for (IGnutellaEventListener l : listeners)
			l.connectionSucceeded(invoker, receiver, connectionUID);
	}

	@Override
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause) {
		for (IGnutellaEventListener l : listeners)
			l.connectionFailed(invoker, receiver, connectionUID, cause);
	}

	@Override
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver) {
		for (IGnutellaEventListener l : listeners)
			l.pingTimeouted(invoker, receiver);
	}

	@Override
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query) {
		for (IGnutellaEventListener l : listeners)
			l.queryStarted(initiator, query);
	}

	@Override
	public void querySucceeded(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		for (IGnutellaEventListener l : listeners)
			l.querySucceeded(initiator, query, hits);
	}

	@Override
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		for (IGnutellaEventListener l : listeners)
			l.queryFailed(initiator, query, hits);
	}

	@Override
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact ownContact) {
		for (IGnutellaEventListener l : listeners)
			l.queryMadeHop(queryUID, ownContact);
	}

	@Override
	public void reBootstrapped(GnutellaLikeOverlayContact c) {
		for (IGnutellaEventListener l : listeners)
			l.reBootstrapped(c);
	}

	@Override
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode,
			GnutellaLikeOverlayContact opponent, ConnBreakCause cause) {
		for (IGnutellaEventListener l : listeners)
			l.connectionBreak(notifiedNode, opponent, cause);
	}

}
