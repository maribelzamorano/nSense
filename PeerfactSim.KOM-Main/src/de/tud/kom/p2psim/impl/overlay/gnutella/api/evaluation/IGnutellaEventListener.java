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

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;

/**
 * Listens to events of the Gnutella06v2 overlay, either globally or not, depending on the
 * event listener instance that is used.
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public interface IGnutellaEventListener {

	/**
	 * The cause of a connection fail.
	 * @author 
	 *
	 */
	public enum FailCause {
		Denied, Timeout;
	}
	
	public enum ConnBreakCause {
		Cancel, Timeout;
	}

	/**
	 * The node invoker started a connection attempt to the node receiver. This
	 * event should be followed by connectionSucceeded(...) or connectionFailed(...).
	 * connectionUID can be used to identify the whole connection operation and is equal
	 * to the UIDs of the reply.
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 */
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID);

	/**
	 * The node receiver has accepted the connection from the node invoker.
	 * @see connectionStarted
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 */
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID);

	/**
	 * The node receiver has denied the connection from the node invoker.
	 * The cause for the denial is given in cause.
	 * @see connectionStarted
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 * @param cause
	 */
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause);
	
	/**
	 * Called whenever a connection has broken and a peer was notified
	 * about it.
	 * @param notifiedNode
	 * @param opponent
	 * @param cause
	 */
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode, 
			GnutellaLikeOverlayContact opponent, 
			ConnBreakCause cause);

	/**
	 * The ping attempt for receiver has timed out. invoker has waited 
	 * too long for a reply.
	 * @param invoker
	 * @param receiver
	 */
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver);

	/**
	 * A query was started at initiator.
	 * @param initiator
	 * @param query
	 */
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query);

	/**
	 * A query has received correct and enough replies (according to the configuration).
	 * hits returns the amount of hits that were received.
	 * @param initiator
	 * @param query
	 * @param hits
	 */
	public void querySucceeded(GnutellaLikeOverlayContact initiator, Query query,
			int hits);

	/**
	 * A query has not received enough replies or incorrect ones.
	 * hits returns the amount of hits that were received.
	 * @param initiator
	 * @param query
	 * @param hits
	 */
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits);

	/**
	 * A query was received by a node. If a query is received by a node multiple times, this
	 * method is called multiple times, too.
	 * 
	 * @param queryUID
	 * @param hopContact
	 */
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact hopContact);
	
	/**
	 * A peer is bootstrapping again, because it has lost any connectivity to other peers.
	 * Does NOT include bootstraps that were explicitly invoked by join() or by a 
	 * connectivityChanged event.
	 * 
	 * @param c
	 */
	public void reBootstrapped(GnutellaLikeOverlayContact c);

}



