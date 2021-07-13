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


package de.tud.kom.p2psim.impl.vis.analyzer;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.EventTimeline;
import de.tud.kom.p2psim.impl.vis.model.ModelFilter;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.model.events.AttributesChanged;
import de.tud.kom.p2psim.impl.vis.model.events.EdgeAdded;
import de.tud.kom.p2psim.impl.vis.model.events.EdgeFlashing;
import de.tud.kom.p2psim.impl.vis.model.events.EdgeRemoved;
import de.tud.kom.p2psim.impl.vis.model.events.Event;
import de.tud.kom.p2psim.impl.vis.model.events.MessageSent;
import de.tud.kom.p2psim.impl.vis.model.events.NodeAdded;
import de.tud.kom.p2psim.impl.vis.model.events.NodeRemoved;
import de.tud.kom.p2psim.impl.vis.model.events.RectangleAdded;
import de.tud.kom.p2psim.impl.vis.model.events.RectangleRemoved;
import de.tud.kom.p2psim.impl.vis.model.overlay.FlashOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;
import de.tud.kom.p2psim.impl.vis.util.MultiMap;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Node;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.PositionInfo;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.VisRectangle;

/**
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @edit Leo Nobach
 * 
 *       Meine Idee: Klasse erhaelt Informationen von Analyzern und gibt sie an
 *       die Timeline weiter
 * 
 *       See also <a href=
 *       "http://www.student.informatik.tu-darmstadt.de/~l_nobach/docs/howto-visualization.pdf"
 *       >PeerfactSim.KOM Visualization HOWTO</a>
 * @version 05/06/2011
 */
public class Translator {

	/**
	 * Ansammlung alle momentan existierenden Visualisierungs-Knoten.
	 * 
	 * Wird benoetigt um beim Wegfall von Knoten anhand der NetID die passende
	 * Instanz von "AOverlayNode" zu bestimmen. Nur mit dieser Instanz kann
	 * Knoten aus Visualisierung wieder entfernt werden.
	 */
	private final HashMap<NetID, VisOverlayNode> visNodes;

	/**
	 * Für jeden Knoten die Kanten, mit denen er verbunden ist.
	 */
	private final MultiMap<Node, VisOverlayEdge> visEdges;

	/**
	 * Ansammlung aller momentan existierenden Rectangles
	 */
	private final HashMap<String, VisRectangle> rectangles;

	/**
	 * Timeline zum Speichern der ausgefuehrten Aktionen
	 */
	private final EventTimeline timeline;

	private final ModelFilter filter;

	public Translator() {

		Controller
				.loadModelBackend(new VisDataModel("Unbenannte Aufzeichnung"));

		// Hole Timeline ueber Controller
		timeline = Controller.getTimeline();
		filter = Controller.getModel().getFilter();

		visNodes = new HashMap<NetID, VisOverlayNode>();

		visEdges = new MultiMap<Node, VisOverlayEdge>();

		rectangles = new HashMap<String, VisRectangle>();
	}

	/**
	 * Setzt obere Grenze fuer auftauchende Koordinaten. Dies ist notwendig um
	 * die Visualisierung richtig zu skalieren.
	 * 
	 * @param maxX
	 * @param maxY
	 */
	public void setUpperBoundForCoordinates(float maxX, float maxY) {
		Coords oldBound = Controller.getModel().getUpperBounds();

		// Bound nur setzen, wenn er sich verändert hat
		if (oldBound.x != maxX || oldBound.y != maxY) {
			Controller.getModel().setUpperBounds(new Coords(maxX, maxY));
		}
	}

	/**
	 * Ermittelt die untere Grenze der auftauchenden Koordinaten der
	 * Visualisierungsoberfläche
	 * 
	 * @return
	 */
	public Coords getLowerBoundForCoordinates() {
		return Controller.getModel().getLowerBounds();
	}

	/**
	 * Setzt untere Grenze fuer auftauchende Koordinaten. Dies ist notwendig um
	 * die Visualisierung richtig zu skalieren.
	 * 
	 * @param maxX
	 * @param maxY
	 */
	public void setLowerBoundForCoordinates(float maxX, float maxY) {
		Coords oldBound = Controller.getModel().getLowerBounds();

		// Bound nur setzen, wenn er sich verändert hat
		if (oldBound.x != maxX || oldBound.y != maxY) {
			Controller.getModel().setLowerBounds(new Coords(maxX, maxY));
		}
	}

	/**
	 * Ermittelt die obere Grenze der auftauchenden Koordinaten der
	 * Visualisierungsoberfläche
	 * 
	 * @return
	 */
	public Coords getUpperBoundForCoordinates() {
		return Controller.getModel().getUpperBounds();
	}

	/**
	 * Lässt eine Kante für kurze Zeit aufblitzen. Die Kante muss danach nicht
	 * wieder entfernt werden.
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeFlash(NetID from, NetID to) {
		overlayEdgeFlash(from, to, Color.red,
				new HashMap<String, Serializable>());
	}

	/**
	 * Lässt eine Kante für kurze Zeit aufblitzen. Die Kante muss danach nicht
	 * wieder entfernt werden. edgeColor legt die Farbe der Kante fest.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 */
	public void overlayEdgeFlash(NetID from, NetID to, Color edgeColor) {
		overlayEdgeFlash(from, to, edgeColor,
				new HashMap<String, Serializable>());
	}

	/**
	 * Lässt eine Kante für kurze Zeit aufblitzen. Die Kante muss danach nicht
	 * wieder entfernt werden. edgeColor legt die Farbe der Kante, und
	 * attributes die Attribute der Kante fest.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @param attributes
	 */
	public void overlayEdgeFlash(NetID from, NetID to, Color edgeColor,
			Map<String, Serializable> attributes) {

		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {

			FlashOverlayEdge edge = new FlashOverlayEdge(visNodes.get(from),
					visNodes.get(to));
			edge.setColor(edgeColor);

			for (String elem : attributes.keySet())
				edge.insertAttribute(elem, attributes.get(elem));

			// Kante beim Filter registrieren

			filter.registerType(edge);

			// Kanteneinfuegung in Timeline speichern
			Event event = new EdgeFlashing(edge);
			timeline.insertEvent(event, Simulator.getCurrentTime());
		}
	}

	/**
	 * Macht das Gleiche wie overlayEdgeAdded(NetID from, NetID to, Color
	 * edgeColor). Einziger Unterschied: Es wird eine Standardfarbe gewählt.
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeAdded(NetID from, NetID to) {

		overlayEdgeAdded(from, to, Color.red,
				new HashMap<String, Serializable>());
	}

	/**
	 * Gibt die Informationen ueber neue Kante an Timeline weiter, mit
	 * Attributen der Kante;
	 * 
	 * @param from
	 * @param to
	 * @param attributes
	 * @return the handle of the added edge
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to,
			Map<String, Serializable> attributes) {
		return overlayEdgeAdded(from, to, Color.red, attributes);
	}

	/**
	 * Gibt die Informationen ueber neue Kante an Timeline weiter, mit der Farbe
	 * der Kante.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @return the handle of the added edge
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to, Color edgeColor) {
		return overlayEdgeAdded(from, to, edgeColor, null);
	}

	/**
	 * Gibt die Informationen ueber neue Kante an Timeline weiter. Kante kann
	 * über Handle gesteuert werden.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @param attributes
	 * @return the handle of the added edge, <code>null</code> indicates that no
	 *         edge was added due to a missing adjacent node
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to, Color edgeColor,
			Map<String, Serializable> attributes) {

		if (attributes == null)
			attributes = new HashMap<String, Serializable>();

		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {
			VisOverlayNode node1 = visNodes.get(from);
			VisOverlayNode node2 = visNodes.get(to);

			VisOverlayEdge edge = new VisOverlayEdge(node1, node2);

			edge.setColor(edgeColor);

			for (String elem : attributes.keySet())
				edge.insertAttribute(elem, attributes.get(elem));

			// Kante beim Filter registrieren
			filter.registerType(edge);

			// Kanteneinfuegung in Timeline speichern
			Event event = new EdgeAdded(edge);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			visEdges.get(node1).add(edge);
			visEdges.get(node2).add(edge);

			return new EdgeHandle(edge, from, to);
		}

		return null;
	}

	/**
	 * Entfernt die angegebene Verbindung
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeRemoved(VisOverlayEdge e) {

		if (e == null)
			return;

		Event event = new EdgeRemoved(e);

		// Kantenentfernung in Timeline speichern
		timeline.insertEvent(event, Simulator.getCurrentTime());

		if (visEdges.contains(e.getNodeA()))
			visEdges.get(e.getNodeA()).remove(e);
		if (visEdges.contains(e.getNodeB()))
			visEdges.get(e.getNodeB()).remove(e);

	}

	/**
	 * Gibt die Informationen ueber neuen Knoten an die Timeline weiter,
	 * inklusiver einer Map an Attributen.
	 * 
	 * @param id
	 * @param overlayGroupName
	 * @param coords
	 */
	public void overlayNodeAdded(NetID id, String overlayGroupName,
			PositionInfo coords) {
		overlayNodeAdded(id, overlayGroupName, coords,
				new HashMap<String, Serializable>());
	}

	/**
	 * Gibt die Informationen ueber neuen Knoten an die Timeline weiter,
	 * inklusiver einer Map an Attributen.
	 * 
	 * @param id
	 * @param overlayGroupName
	 * @param coords
	 * @param attributes
	 */
	public void overlayNodeAdded(NetID id, String overlayGroupName,
			PositionInfo coords, Map<String, Serializable> attributes) {

		if (!visNodes.containsKey(id)) { // Nur einfuegen, wenn noch nicht
			// vorhanden

			// Neuen Knoten fuer Visualisierung erzeugen
			VisOverlayNode node = new VisOverlayNode(coords, overlayGroupName);

			for (String elem : attributes.keySet())
				node.insertAttribute(elem, attributes.get(elem));

			// Attribut fuer Namen des Knotens setzen
			node.insertAttribute("NetID", id.toString()); // geaendert von Leo,
			// da NetID nicht
			// serializable ist,
			// toString aber!

			// Knotenerzeugung in Timeline speichern
			Event event = new NodeAdded(node);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			// Fuege Knoten in Hashmap ein
			visNodes.put(id, node);
		}
	}

	/**
	 * Gibt die Informationen ueber Wegfall eines Knoten an Timeline weiter
	 * 
	 * @param id
	 */
	public void overlayNodeRemoved(NetID id) {
		if (visNodes.containsKey(id)) { // Nur entfernen wenn vorhanden
			VisOverlayNode node = visNodes.get(id);

			// Entferne alle Kanten zu diesem Knoten
			cleanUpEdgesForNodeDeletion(id);

			// Knotenentfernung in Timeline speichern
			Event event = new NodeRemoved(node);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			// Loesche Knoten aus Hashmap
			visNodes.remove(id);

		}
	}

	/**
	 * Entfernt alle Kanten zu einem gegebenen Knoten und leitet diese
	 * Entfernung an die Timeline weiter.
	 * 
	 * Diese Methode ist als Hilfsmethode fuer das Loeschen eines Knotens
	 * entstanden.
	 * 
	 * @param nodeID
	 *            NetID des Knotens, dessen Kanten entfernt werden muessen
	 */
	private void cleanUpEdgesForNodeDeletion(NetID nodeID) {

		Node n = visNodes.get(nodeID);
		if (n == null)
			return;

		for (VisOverlayEdge e : visEdges.get(n)) {
			// Event in Timeline einfuegen
			Event event = new EdgeRemoved(e);
			timeline.insertEvent(event, Simulator.getCurrentTime());
		}
	}

	/**
	 * Gibt die Informatioen über eine gesendete Nachricht an die Timeline
	 * weiter
	 * 
	 * @param from
	 * @param to
	 * @param messageType
	 */
	public void overlayMessageSent(NetID from, NetID to, String messageType) {
		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {
			VisOverlayNode node1 = visNodes.get(from);
			VisOverlayNode node2 = visNodes.get(to);

			timeline.insertEvent(new MessageSent(node1, node2, messageType),
					Simulator.getCurrentTime());
		}
	}

	public void nodeAttributeChanged(NetID node, String key, Serializable value) {
		Map<String, Serializable> attrs = new HashMap<String, Serializable>();
		attrs.put(key, value);
		this.nodeAttributesChanged(node, attrs);
	}

	public void nodeAttributesChanged(NetID node,
			Map<String, Serializable> attrs) {
		VisOverlayNode visNode = visNodes.get(node);
		if (visNode != null)
			timeline.insertEvent(new AttributesChanged(visNode, attrs),
					Simulator.getCurrentTime());
	}

	/**
	 * Draws a rectangle on the visualization. This feature only makes sense
	 * when working with bitmap based visualizations where the zoom is fixed
	 * since the rectangles are painted with absolut pixel positions on the
	 * visualization.
	 * 
	 * In order to be able to remove the rectangle later, you have to provide a
	 * String which identifies the rectangle. This String is needed when calling
	 * the method "removeRectangle(String ID)".
	 * 
	 * @param point1
	 * @param point2
	 * @param color
	 * @param ID
	 */
	public void drawRectangle(Point point1, Point point2, Color color, String ID) {
		VisRectangle rect = new VisRectangle(point1, point2, color);
		RectangleAdded event = new RectangleAdded(rect);

		rectangles.put(ID, rect);

		timeline.insertEvent(event, Simulator.getCurrentTime());
	}

	/**
	 * Remove a rectangle that is identified by the provided String. This String
	 * was defined when adding the rectangle.
	 * 
	 * @param ID
	 * @param minTimeToShowEdge
	 */
	public void removeRectangle(String ID, int minTimeToShowEdge) {
		if (rectangles.containsKey(ID)) {
			VisRectangle rect = rectangles.get(ID);

			RectangleRemoved event = new RectangleRemoved(rect);

			timeline.insertEvent(event, Simulator.getCurrentTime()
					+ (minTimeToShowEdge * Simulator.SECOND_UNIT));

			rectangles.remove(ID);
		}
	}

	/**
	 * @param nodeID
	 * @return wheter the node already exists or not
	 */
	public boolean nodeExists(NetID nodeID) {
		return visNodes.containsKey(nodeID);
	}

	public static void notifyFinished() {
		Controller.init();
		Controller.connectModelToUI();
	}

	public class EdgeHandle {
		private final VisOverlayEdge e;

		private final NetID from;

		private final NetID to;

		EdgeHandle(VisOverlayEdge e, NetID from, NetID to) {
			this.e = e;
			this.from = from;
			this.to = to;
		}

		public NetID getFrom() {
			return from;
		}

		public NetID getTo() {
			return to;
		}

		/**
		 * Triggers the removal of this edge at the Translator
		 */
		public void remove() {
			overlayEdgeRemoved(e);
		}

		/*
		 * Implementation of equals and hashcode
		 */

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if ((obj == null) || (obj.getClass() != this.getClass()))
				return false;

			EdgeHandle edge = (EdgeHandle) obj;
			return from.equals(edge.from) && to.equals(edge.to);
		}

		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + (null == from ? 0 : from.hashCode());
			hash = 31 * hash + (null == to ? 0 : to.hashCode());
			return hash;
		}

	}

}
