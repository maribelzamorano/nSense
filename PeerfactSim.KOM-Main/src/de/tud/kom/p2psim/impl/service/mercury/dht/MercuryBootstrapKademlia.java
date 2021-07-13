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

package de.tud.kom.p2psim.impl.service.mercury.dht;

import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;
import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;
import de.tud.kom.p2psim.impl.service.mercury.MercuryService;

/**
 * Bootstrapper for Kademlia and Mercury
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryBootstrapKademlia implements MercuryBootstrap {

	public MercuryBootstrapKademlia() {
		// intentionally left blank
	}

	@Override
	public void setAttributes(List<MercuryAttributePrimitive> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public MercuryBootstrapInfo getBootstrapInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MercuryContact> getRandomContactForEachAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DHTNode createOverlayNode(MercuryBootstrapInfo bsInfo, Host host,
			short port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void callbackOverlayID(MercuryService service) {
		// TODO Auto-generated method stub

	}

	@Override
	public MercuryIDMapping getIDMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OverlayID[] getRange(INeighborDeterminator neighbors,
			MercuryService service) {
		// TODO Auto-generated method stub
		return null;
	}

}
