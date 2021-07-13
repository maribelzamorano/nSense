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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays;

import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.api.overlay.gnutella.GnutellaAPI;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IResource;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.RankResource;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.IGnutellaEventListener;

/**
 * Filesharing2 overlay handler for my Gnutella06 implementation.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaHandler extends AbstractOverlayHandler implements
		IGnutellaEventListener {

	
	private static final int HITS_WANTED = 1;
	private GnutellaAPI node;

	public GnutellaHandler(GnutellaAPI node) {
		this.node = node;
		node.getLocalEventDispatcher().addListener(this);
	}

	@Override
	public void join() {
		node.join(Operations.getEmptyCallback());
	}

	@Override
	public void leave() {
		node.leave(Operations.getEmptyCallback());
	}

	@Override
	public void lookupResource(int key) {
		node.queryRank(key, HITS_WANTED);
	}

	@Override
	public void publishResources(Set<Integer> resources) {

		Set<IResource> res = new HashSet<IResource>();

		for (Integer rank : resources) {
			res.add(new RankResource(rank));
		}

		node.publishSet(res);
	}

	@Override
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause) {
		// Nothing to do
	}

	@Override
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// Nothing to do
	}

	@Override
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// Nothing to do
	}

	@Override
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver) {
		// Nothing to do
	}

	@Override
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		// Nothing to do
	}

	@Override
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact hopContact) {
		this.lookupMadeHop(queryUID, hopContact);
	}

	@Override
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query) {
		this.lookupStarted(initiator, query.getQueryUID());
	}

	@Override
	public void querySucceeded(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		this.lookupSucceeded(initiator, query.getQueryUID(), 0);
	}

	@Override
	public void reBootstrapped(GnutellaLikeOverlayContact c) {
		//Nothing to do
	}

	@Override
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode,
			GnutellaLikeOverlayContact opponent, ConnBreakCause cause) {
		// Nothing to do
		
	}

}
