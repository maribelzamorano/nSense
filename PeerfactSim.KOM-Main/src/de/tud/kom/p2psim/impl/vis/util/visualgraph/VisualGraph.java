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

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import de.tud.kom.p2psim.impl.vis.model.MetricObject;
import de.tud.kom.p2psim.impl.vis.model.ModelIterator;

/**
 * <b>"Hypergenerischer" Graph.</b>
 * 
 * Die übergebenen Typen müssen die abstrakten Klassen Node und Edge
 * erweitern. Der Vorteil bei der Verwendung dieses Graphen ist, dass eigentlich
 * nie gecastet werden muss.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TNode>
 * @param <TEdge>
 * 
 * @version 05/06/2011
 */

public class VisualGraph<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2796546862703465617L;

	public Vector<Node<TNode, TEdge>> nodes = new Vector<Node<TNode, TEdge>>();

	public Vector<Edge<TNode, TEdge>> edges = new Vector<Edge<TNode, TEdge>>();

	public Vector<VisRectangle> rectangles = new Vector<VisRectangle>();

	public void addNode(Node<TNode, TEdge> node) {
		nodes.add(node);
		node.setGraph(this);
	}

	public void addEdge(Edge<TNode, TEdge> edge) {
		synchronized (edges) {
			edges.add(edge);
			edge.setGraph(this);
		}
	}

	public void removeNode(Node<TNode, TEdge> node) {

		synchronized (nodes) {
			synchronized (edges) {
				// alle Kanten entfernen, die mit dem Knoten zusammenhängen
				Collection<Edge<TNode, TEdge>> tmpEdges = new Vector<Edge<TNode, TEdge>>();
				tmpEdges.addAll(node.edges);

				for (Edge<TNode, TEdge> e : tmpEdges) {
					removeEdge(e);
				}
				nodes.remove(node);
				node.unsetGraph();
			}
		}
	}

	public void removeEdge(Edge<TNode, TEdge> edge) {
		synchronized (edges) {
			edges.remove(edge);
			edge.unsetGraph();
		}
	}

	public void addRectangle(VisRectangle rect) {
		rectangles.add(rect);
	}

	public void removeRectangle(VisRectangle rect) {
		rectangles.remove(rect);
	}

	/**
	 * Iteriert über die Elemente des Graphen Erst Nodes, dann Edges.
	 * 
	 * @param it
	 */

	public void iterate(ModelIterator it, MetricObject selectedObject) {
		this.iterateRectangles(it);
		if (!it.shallStop())
			this.iterateNodes(it);
		if (!it.shallStop())
			this.iterateEdges(it);
	}

	/**
	 * Iteriert über die Elemente des Graphen Erst Edges, dann Nodes.
	 * 
	 * @param it
	 */

	public void iterateBottomTop(ModelIterator it) {
		this.iterateRectangles(it);
		if (!it.shallStop())
			this.iterateEdges(it);
		if (!it.shallStop())
			this.iterateNodes(it);
	}

	/**
	 * Iteriert über alle Edges
	 * 
	 * @param it
	 */
	public void iterateEdges(ModelIterator it) {

		synchronized (edges) {
			for (Edge<TNode, TEdge> e : edges) {
				e.iterate(it);
				if (it.shallStop())
					break;
			}
		}

	}

	/**
	 * Iteriert über alle Nodes
	 * 
	 * @param it
	 */
	public synchronized void iterateNodes(ModelIterator it) {
		synchronized (nodes) {
			for (Node<TNode, TEdge> n : nodes) {
				n.iterate(it);
				if (it.shallStop())
					break;
			}
		}
	}

	/**
	 * Iteriert über alle Rectangles
	 * 
	 * @param it
	 */
	public synchronized void iterateRectangles(ModelIterator it) {
		for (VisRectangle rect : rectangles) {
			rect.iterate(it);
			if (it.shallStop())
				break;
		}
	}

	/**
	 * Setzt den Graphen und seine Elemente zurück.
	 */
	public synchronized void reset() {
		for (Node<TNode, TEdge> n : nodes) {
			n.edges.clear();
		}
		nodes.clear();
		edges.clear();
	}

}
