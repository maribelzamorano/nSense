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


package de.tud.kom.p2psim.impl.vis.model.events;

import java.io.Serializable;

import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Eine Message wird gesendet.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageSent extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4524337256193794212L;

	private VisOverlayNode from;

	private VisOverlayNode to;

	private String messageType;

	public MessageSent(VisOverlayNode from, VisOverlayNode to,
			String messageType) {
		this.from = from;
		this.to = to;
		this.messageType = messageType;
	}

	@Override
	public void makeHappen() {
		// TODO Was passiert auf der GUI, wenn Message gesendet wird? Alles
		// über Metriken lösen?
	}

	@Override
	public void undoMakeHappen() {
		// TODO Was passiert auf der GUI, wenn Message gesendet wird? Alles
		// über Metriken lösen?

	}

	public VisOverlayNode getFrom() {
		return from;
	}

	public VisOverlayNode getTo() {
		return to;
	}

	public String getMessageType() {
		return messageType;
	}

	public String toString() {
		return "Message sent: " + getMessageType();
	}

}
