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

package de.tud.kom.p2psim.impl.overlay.dht;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class KBRForwardInformationImpl implements KBRForwardInformation {

	private Message msg;
	private OverlayContact nextHopAgent;
	private OverlayKey key;
	
	public KBRForwardInformationImpl(OverlayKey key, Message msg, OverlayContact nextHopAgent) {
		this.msg = msg;
		this.key = key;
		this.nextHopAgent = nextHopAgent;
	}
	
	@Override
	public Message getMessage() {
		return msg;
	}

	@Override
	public OverlayContact getNextHopAgent() {
		return nextHopAgent;
	}

	@Override
	public OverlayKey getKey() {
		return key;
	}

	@Override
	public void setMessage(Message msg) {
		this.msg = msg;
	}

	@Override
	public void setNextHopAgent(OverlayContact nextHopAgent) {
		this.nextHopAgent = nextHopAgent;
	}

	@Override
	public void setKey(OverlayKey key) {
		this.key = key;
	}

}
