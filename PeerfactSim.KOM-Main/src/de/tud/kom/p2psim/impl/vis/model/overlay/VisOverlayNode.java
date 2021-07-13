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
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.model.MetricObject;
import de.tud.kom.p2psim.impl.vis.model.ModelIterator;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.PositionInfo;

/**
 * Knoten (Peer) im Overlay-Netzwerk.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class VisOverlayNode
		extends
		de.tud.kom.p2psim.impl.vis.util.visualgraph.Node<VisOverlayNode, VisOverlayEdge>
		implements MetricObject, AttributeObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1932483666858398117L;

	Hashtable<String, Serializable> attributes = new Hashtable<String, Serializable>();

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			"images/icons/model/OverlayNode16_16.png");

	/**
	 * Name des Knotens
	 */
	String name;

	public VisOverlayNode(PositionInfo pos, String name) {
		super(pos);
		this.name = name;
	}

	/**
	 * Gibt den Namen des Knotens zurück, der in der Darstellung verwendet
	 * wird.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
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

	/**
	 * Setzt einen Namen f�r diesen Knoten, der in der Darstellung verwendet
	 * werden soll.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt einen Satz aller Metriken für diesen Knoten in der an ihn
	 * gebundenen Form zurück.
	 * 
	 * @return
	 */
	public Vector<BoundMetric> getBoundMetrics() {
		Vector<BoundMetric> res = new Vector<BoundMetric>();
		for (OverlayNodeMetric m : de.tud.kom.p2psim.impl.vis.metrics.MetricsBase
				.forOverlayNodes().getListOfAllMetrics()) {
			res.add(m.getBoundTo(this));
		}
		return res;
	}

	@Override
	public Color getColor() {
		return Color.DARK_GRAY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void iterate(ModelIterator it) {
		it.overlayNodeVisited(this);
	}

	public String toString() {
		return this.getName() + "(" + this.getAttribute("NetID") + ")";
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

}
