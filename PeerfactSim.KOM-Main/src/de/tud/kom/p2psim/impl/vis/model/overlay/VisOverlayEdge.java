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


package de.tud.kom.p2psim.impl.vis.model.overlay;

import java.awt.Color;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.api.metrics.BoundMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.model.MetricObject;
import de.tud.kom.p2psim.impl.vis.model.ModelIterator;
import de.tud.kom.p2psim.impl.vis.model.TypeObject;

/**
 * Kante (Verbindung) im Overlay-Netzwerk.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VisOverlayEdge
		extends
		de.tud.kom.p2psim.impl.vis.util.visualgraph.Edge<VisOverlayNode, VisOverlayEdge>
		implements TypeObject, MetricObject, AttributeObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6939096705519916055L;

	Hashtable<String, Serializable> attributes = new Hashtable<String, Serializable>();

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			"images/icons/model/OverlayEdgePermanent16_16.png");

	protected Color cl = Color.RED;

	int uniqueTypeIdentifier = -1;

	/**
	 * Nicht darstellungsspezifische Kante des Overlays. Sollte nur von der
	 * Darstellungsfactory erzeugt werden.
	 * 
	 * @param a
	 * @param b
	 */

	public VisOverlayEdge(VisOverlayNode a, VisOverlayNode b) {
		super(a, b);
	}

	/**
	 * Gibt einen Satz aller Metriken für diese Kante in der an sie gebundenen
	 * Form zurück.
	 * 
	 * @see BoundMetric
	 * @return
	 */
	public Vector<BoundMetric> getBoundMetrics() {
		Vector<BoundMetric> res = new Vector<BoundMetric>();
		for (OverlayEdgeMetric m : de.tud.kom.p2psim.impl.vis.metrics.MetricsBase
				.forOverlayEdges().getListOfAllMetrics()) {
			res.add(m.getBoundTo(this));
		}
		return res;
	}

	@Override
	public Color getColor() {
		return cl;
	}

	/**
	 * Setzt die Farbe der Kante in der Visualisierungsdarstellung auf einen
	 * bestimmten Wert.
	 * 
	 * @param c
	 */
	public void setColor(Color c) {
		this.cl = c;
	}

	/**
	 * F�gt ein Attribut ein. <b> Achtung! Alle Attribute m�ssen
	 * serializable sein! </b>
	 * 
	 * @param name
	 * @param value
	 */
	public void insertAttribute(String name, Serializable value) {
		this.attributes.put(name, value);
	}

	/**
	 * Gibt das Attribut mit dem Key name zurück.
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Gibt eine Map aller Attribute zurück.
	 * 
	 * @return
	 */
	public Map<String, Serializable> getAttributes() {
		return attributes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void iterate(ModelIterator it) {
		it.overlayEdgeVisited(this);
	}

	@Override
	public String toString() {
		return node1.toString() + " - " + node2.toString();
	}

	/**
	 * Gibt einen eindeutigen Identifier zurück, der den Typ der Nachricht
	 * identifiziert. Unkategorisierte Nachrichten sind dabei alle zusammen ein
	 * Typ!
	 * 
	 * @return
	 */
	@Override
	public int getUniqueTypeIdentifier() {

		if (uniqueTypeIdentifier == -1) {

			uniqueTypeIdentifier = this.getClass().hashCode()
					+ getHashCodeForAttr("type")
					+ getHashCodeForAttr("overlay")
					+ getHashCodeForAttr("msg_class");

		}

		return uniqueTypeIdentifier;
	}

	/**
	 * Gibt den Namen des Typs zurück.
	 * 
	 * @return
	 */
	@Override
	public String getTypeName() {
		String name = getAttributes().get("type").toString();
		if (name == null) {
			name = getAttributes().get("msg_class").toString();
			if (name == null) {
				name = getAttributes().get("overlay").toString();
				if (name == null)
					name = "Uncategorized";
			}
		}
		return name;
	}

	protected int getHashCodeForAttr(String attr) {
		Object attrib = getAttributes().get(attr);
		return (attrib == null) ? 0 : attrib.hashCode();
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

}
