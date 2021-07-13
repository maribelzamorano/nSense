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


package de.tud.kom.p2psim.impl.overlay.dht.napster;

import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.impl.overlay.dht.AbstractRequestMsg;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public abstract class AbstractNapsterRequestMsg<S extends Object> extends
		AbstractRequestMsg<NapsterOverlayID> {

	private int opID;

	private long size;

	public AbstractNapsterRequestMsg(NapsterOverlayID sender,
			NapsterOverlayID receiver, S content, int opID) {
		super(sender, receiver, content);
		this.opID = opID;
		this.size = 2 * SkyNetConstants.OVERLAY_ID_SIZE
				+ SkyNetConstants.INT_SIZE;
	}

	public int getOpID() {
		return opID;
	}

	public S getCastedContent() {
		return (S) getContent();
	}

	@Override
	public long getSize() {
		return size;
	}

}
