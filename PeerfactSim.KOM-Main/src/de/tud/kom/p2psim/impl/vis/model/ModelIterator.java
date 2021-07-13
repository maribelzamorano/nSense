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


package de.tud.kom.p2psim.impl.vis.model;

import de.tud.kom.p2psim.impl.vis.model.overlay.FlashOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Edge;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Node;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.VisRectangle;

/**
 * Lässt sich über alle Elemente des Datenmodells iterieren, erst über Knoten,
 * dann über Kanten. Kann die Iteration stoppen.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface ModelIterator<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>, TFlashEdge extends Edge<TNode, TEdge>> {

	/**
	 * Gibt zurück, ob der Iterator bei der nächsten Gelegenheit gestoppt werden
	 * soll.
	 * 
	 * @return
	 */
	public boolean shallStop();

	/**
	 * Soll nur über die Knoten und Kanten iteriert werden, die die höchste
	 * Priorität haben?
	 * 
	 * @see VisOverlayEdge
	 * @return
	 */
	public boolean onlyHighestPrio();

	public void overlayEdgeVisited(TEdge edge);

	public void overlayNodeVisited(TNode node);

	public void rectangleVisited(VisRectangle rect);

	/**
	 * Aufruf bei Besuch einer FlashOverlayEdge
	 * 
	 * @see FlashOverlayEdge
	 * @param e
	 */
	public void flashOverlayEdgeVisited(TFlashEdge e);

}
