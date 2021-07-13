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
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;

/**
 * Eine Verbindung tritt dem Szenario bei.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class EdgeAdded extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1878312595608244036L;

	protected VisOverlayEdge edge;

	public EdgeAdded(VisOverlayEdge edge) {
		this.edge = edge;
	}

	@Override
	public void makeHappen() {
		Controller.getModel().getOverlayGraph().addEdge(edge);
	}

	@Override
	public void undoMakeHappen() {
		Controller.getModel().getOverlayGraph().removeEdge(edge);

	}

	public String toString() {
		return "EdgeAdded: " + edge.toString();
	}

}
