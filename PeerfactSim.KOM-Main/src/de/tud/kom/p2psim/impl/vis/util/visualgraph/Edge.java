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

import de.tud.kom.p2psim.impl.vis.model.ModelIterator;

/**
 * Abstrakte Kante eines Graphen (bisher ungerichtet)
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TNode>
 * @param <TEdge>
 * @version 05/06/2011
 */
public abstract class Edge<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3009908846848647477L;

	/**
	 * Farbe der Kante
	 */
	protected static final java.awt.Color edgecolor = java.awt.Color.RED;

	protected TNode node1;

	protected TNode node2;

	private VisualGraph<TNode, TEdge> graph;

	private int priority = 10;

	public Edge(TNode a, TNode b) {
		this.node1 = a;
		this.node2 = b;
	}

	public void setGraph(VisualGraph<TNode, TEdge> graph) {
		this.graph = graph;
		node1.edges.add(this);
		node2.edges.add(this);
	}

	public void unsetGraph() {
		this.graph = null;
		node1.edges.remove(this);
		node2.edges.remove(this);
	}

	public VisualGraph<TNode, TEdge> getGraph() {
		return this.graph;
	}

	public void setNodeA(TNode a) {
		this.node1 = a;
	}

	public void setNodeB(TNode b) {
		this.node2 = b;
	}

	public Node<TNode, TEdge> getNodeA() {
		return node1;
	}

	public Node<TNode, TEdge> getNodeB() {
		return node2;
	}

	public abstract void iterate(ModelIterator it);

	public abstract Color getColor();

	/**
	 * Gibt an, ob die Kante beim nächsten Cleanup-Prozess entfernt werden soll,
	 * 
	 * @see Kommentar in Simple2DVisualization.paintDataModel
	 * @return
	 */
	public boolean markedAsRemovable() {
		return false;
	}

	/**
	 * Gibt die Priorität der Kante zurück. Kanten mit <b> niedrigerer</b>
	 * Priorität werden bevorzugt gezeichnet gegenüber Kanten mit höherer
	 * Priorität. Standardwert ist 10.
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Setzt die Priorität der Kante. Kanten mit <b> niedrigerer</b> Priorität
	 * werden bevorzugt gezeichnet gegenüber Kanten mit höherer Priorität.
	 * Standardwert ist 10.
	 * 
	 * @return
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Gibt zurück, ob die Priorität dieser Kante höher ist als die der Kante e.
	 * 
	 * @param e
	 * @return
	 */
	public boolean higherPriorityThan(Edge<TNode, TEdge> e) {
		return this.getPriority() > e.getPriority();
	}

	/*
	 * @Override public boolean equals(Object o) { if (o instanceof Edge) { Edge
	 * e = (Edge)o;
	 * 
	 * boolean equal = (e.getNodeA() == this.getNodeA() && e.getNodeB() ==
	 * this.getNodeB() || e.getNodeB() == this.getNodeA() && e.getNodeA() ==
	 * this.getNodeB());
	 * 
	 * //Eine Kante ist gleich einer anderen, wenn sie die gleichen Knoten
	 * verwendet. //Nur für das Zeichnen zu verwenden.
	 * 
	 * return equal; } return false; }
	 * 
	 * @Override public int hashCode() {
	 * 
	 * if (node1 == null || node2 == null) return super.hashCode(); return
	 * node1.hashCode() + node2.hashCode(); //konsistent mit equals, da A+B =
	 * B+A und damit auch umgekehrte Kanten den gleichen HashCode haben. }
	 */
}
