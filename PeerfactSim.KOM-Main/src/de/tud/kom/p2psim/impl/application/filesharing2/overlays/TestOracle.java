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

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.impl.util.MultiSet;

/**
 * This class is for debugging purposes only. It fakes a perfect overlay with global knowledge.
 * Every lookup succeeds iff
 * <ul>
 *  <li> the key was published
 *  <li> the peer that published the document is online.
 * </ul>
 * Query success should always be 100% when using this handler.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class TestOracle extends AbstractOverlayHandler implements ConnectivityListener {

	static final MultiSet<Integer> ranksShared = new MultiSet<Integer>();
	
	final Set<Integer> localRanksShared = new HashSet<Integer>();
	
	static long queryUID = 0;

	//private FilesharingApplication app;
	
	@Override
	public void join() {
		this.getFSApplication().getHost().getNetLayer().addConnectivityListener(this);
	}

	@Override
	public void leave() {
		//Nothing to do
	}

	@Override
	public void lookupResource(int key) {
		if (this.getFSApplication().getHost().getNetLayer().isOffline()) throw new IllegalStateException("Host is offline and wants to lookup.");
		this.lookupStarted(null, queryUID);
		if (ranksShared.containsOccurrence(key)) {
			lookupSucceeded(null, queryUID, 0);
		}
		queryUID++;
	}

	@Override
	public void publishResources(Set<Integer> resources) {
		
		for (int key : resources){
			this.publishStarted(null, key, queryUID);
			localRanksShared.add(key);
			ranksShared.addOccurrence(key);
			publishSucceeded(null, null, key, queryUID);
			queryUID++;
		}
	}
	
	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			//System.out.println("Gone offline.");
			ranksShared.removeOccurrences(localRanksShared);
		} else {
			//System.out.println("Gone online.");
			ranksShared.addOccurrences(localRanksShared);
		}
	}

}
