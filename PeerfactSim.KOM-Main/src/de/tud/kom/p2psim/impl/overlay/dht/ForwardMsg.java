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
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;

/**
 * this message is used to transport "forward" instruction to the next node
 * 
 * @author Yue Sheng (edited by Julius Rueckert) <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class ForwardMsg extends AbstractOverlayMessage<OverlayID> {

	private final OverlayKey key;

	private final Message msg;

	private int hops;

	/**
	 * @param sender
	 * @param receiver
	 * @param key
	 * @param msg
	 * @param numberOfHops
	 */
	public ForwardMsg(OverlayID sender, OverlayID receiver, OverlayKey key,
			Message msg, int numberOfHops) {
		super(sender, receiver);
		this.key = key;
		this.msg = msg;
		this.hops = numberOfHops;
	}

	/**
	 * @param sender
	 * @param receiver
	 * @param key
	 * @param msg
	 */
	public ForwardMsg(OverlayID sender, OverlayID receiver, OverlayKey key,
			Message msg) {
		super(sender, receiver);
		this.key = key;
		this.msg = msg;
		this.hops = 0;
	}

	@Override
	public long getSize() {
		return msg.getSize();
	}

	public OverlayKey getKey() {
		return key;
	}

	@Override
	public Message getPayload() {
		return msg;
	}

	/**
	 * @return the current number of hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Increases the number of hops by one
	 */
	public void incHops() {
		hops++;
	}

	@Override
	public String toString() {
		return "ForwardMsg[key=" + key + ", msq=" + msg + ", hops=" + hops
				+ "]";
	}

}
