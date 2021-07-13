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


/**
 * 
 */
package de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;

/**
 * Gnutella module for live monitoring of what is going on in the Gnutella overlay.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GnutellaLiveEvents implements IGnutellaEventListener {

	int reBootstraps = 0;
	
	public GnutellaLiveEvents() {
		LiveMonitoring.addProgressValue(new ReBootstraps());
	}
	
	@Override
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact hopContact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void querySucceeded(GnutellaLikeOverlayContact initiator,
			Query query, int hits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reBootstrapped(GnutellaLikeOverlayContact c) {
		reBootstraps++;
	}
	
	class ReBootstraps implements ProgressValue {

		@Override
		public String getName() {
			return "Gnutella Re-Bootstraps";
		}

		@Override
		public String getValue() {
			return String.valueOf(reBootstraps);
		}
		
	}

	@Override
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode,
			GnutellaLikeOverlayContact opponent, ConnBreakCause cause) {
		// TODO Auto-generated method stub
		
	}

}
