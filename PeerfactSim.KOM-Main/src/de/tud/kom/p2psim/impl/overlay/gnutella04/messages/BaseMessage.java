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


package de.tud.kom.p2psim.impl.overlay.gnutella04.messages;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class BaseMessage extends
		AbstractOverlayMessage<GnutellaOverlayID> {

	private int ttl;

	private int hops;

	private BigInteger descriptor;

	private final static long GNUTELLA_BASE_MESSAGE_SIZE = 23;

	public BaseMessage(GnutellaOverlayID sender, GnutellaOverlayID receiver,
			int ttl, int hops, BigInteger descriptor) {
		super(sender, receiver);
		this.ttl = ttl;
		this.hops = hops;
		this.descriptor = descriptor;
	}

	public Message getPayload() {
		return this;
	}

	public Integer getTTL() {
		return ttl;
	}

	public Integer getHops() {
		return hops;
	}

	public BigInteger getDescriptor() {
		return descriptor;
	}

	public long getSize() {
		return GNUTELLA_BASE_MESSAGE_SIZE;
	}

}
