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

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.overlay.FlashOverlayEdge;

/**
 * Eine Verbindung tritt kurz auf und verschwindet dann wieder.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 19.10.2008
 * 
 */
public class EdgeFlashing extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 111039272563104948L;

	private FlashOverlayEdge edge;

	public EdgeFlashing(FlashOverlayEdge edge) {
		this.edge = edge;
	}

	@Override
	public void makeHappen() {
		Controller.getVisApi().queueFlashEvent(edge);
	}

	@Override
	public void undoMakeHappen() {
		// eventuell noch einmal:
		// Controller.getModel().getOverlayGraph().addEdge(edge);
		// ist Geschmackssache, was besser aussieht.
		// Wäre sinnvoll, wenn Rückwärtslauf implementiert wird.
	}

	public String toString() {
		return "EdgeFlashing: " + edge.toString();
	}

}
