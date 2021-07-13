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


package de.tud.kom.p2psim.impl.vis.util.visualgraph;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashSet;

import de.tud.kom.p2psim.impl.vis.model.ModelIterator;

/**
 * Abstrakter Knoten eines Graphen (bisher ungerichtet)
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TNode>
 * @param <TEdge>
 * @version 05/06/2011
 */
public abstract class Node<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2354593609095590995L;

	private PositionInfo position;

	private VisualGraph<TNode, TEdge> graph;

	public HashSet<Edge<TNode, TEdge>> edges = new HashSet<Edge<TNode, TEdge>>();

	public Node(PositionInfo pos) {
		this.position = pos;
	}

	public void setGraph(VisualGraph<TNode, TEdge> graph) {
		this.graph = graph;
	}

	public void unsetGraph() {
		this.graph = null;
	}

	public VisualGraph<TNode, TEdge> getGraph() {
		return this.graph;
	}

	public void setPos(PositionInfo pos) {
		this.position = pos;
	}

	/**
	 * Topologische Position
	 * 
	 * @return
	 */
	public Coords getTopologicalPosition() {
		// System.out.println(position);
		return this.position.getTopoCoords();
	}

	/**
	 * Schematische Position
	 * 
	 * @return
	 */
	public Coords getSchematicPosition() {
		return this.position.getSchemCoords();
	}

	/**
	 * Positionsinformation
	 * 
	 * @return
	 */
	public PositionInfo getPositionInfo() {
		return this.position;
	}

	public abstract void iterate(ModelIterator it);

	public abstract Color getColor();

}
