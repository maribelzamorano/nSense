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


package de.tud.kom.p2psim.impl.vis.visualization2d;

import java.awt.Point;

import de.tud.kom.p2psim.impl.vis.model.MetricObject;
import de.tud.kom.p2psim.impl.vis.model.ModelFilter;
import de.tud.kom.p2psim.impl.vis.model.ModelIterator;
import de.tud.kom.p2psim.impl.vis.model.overlay.FlashOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Node;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.VisRectangle;

/**
 * Iterator über das Modell, der überprüft, ob und wo auf das Modell geklickt
 * wurde.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ModelClickHandler implements
		ModelIterator<VisOverlayNode, VisOverlayEdge, FlashOverlayEdge> {

	Point clickPoint;

	Point width_height;

	MetricObject clickedObject;

	private final Simple2DVisualization vis;

	public ModelClickHandler(Point clickPoint, Point width_height,
			Simple2DVisualization vis) {
		super();
		this.clickPoint = clickPoint;
		this.width_height = width_height;
		this.vis = vis;
	}

	/**
	 * "Klickt" in das Visualisierungsfenster und gibt das Objekt zurück, auf
	 * das geklickt wurde, ansonsten <b>null</b>
	 * 
	 * @param clickPoint
	 */
	public MetricObject getObjectFromClick(Point clickPoint) {

		return clickedObject;

	}

	@Override
	public boolean shallStop() {
		return clickedObject != null;
	}

	/*
	 * EDGES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	@Override
	public void overlayEdgeVisited(VisOverlayEdge edge) {
		if (getFilter().typeActivated(edge)
				&& overlayEdgeClicked(edge, clickPoint)) {
			clickedObject = edge;
		}

	}

	@Override
	public void flashOverlayEdgeVisited(FlashOverlayEdge edge) {
		this.overlayEdgeVisited(edge);
	}

	/**
	 * Bereich um die Linie, auf den geklickt werden kann, damit sie ausgewählt
	 * wird in Pixeln.
	 */
	static final int click_distance = 8;

	public boolean overlayEdgeClicked(VisOverlayEdge e, Point clickPoint) {

		return de.tud.kom.p2psim.impl.vis.util.visualgraph.GraphMath
				.calculateDistanceFromLine(
						getPositionInWindow(getSelectedNodePosition(e
								.getNodeA())),
						getPositionInWindow(getSelectedNodePosition(e
								.getNodeB())), clickPoint, click_distance);

	}

	/*
	 * NODES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	/**
	 * Klick-Radius
	 */
	final int CLICK_RADIUS = Painter.NODE_RADIUS + 4;

	@Override
	public void overlayNodeVisited(VisOverlayNode node) {
		if (overlayNodeClicked(node, clickPoint)) {
			clickedObject = node;
		}

	}

	public boolean overlayNodeClicked(VisOverlayNode node, Point clickPoint) {
		return clickPoint.distanceSq(this
				.getPositionInWindow(getSelectedNodePosition(node))) < CLICK_RADIUS
				* CLICK_RADIUS;
	}

	/*
	 * Rectangle
	 * ----------------------------------------------------------------
	 * ----------
	 * ----------------------------------------------------------------
	 * ----------------
	 */

	@Override
	public void rectangleVisited(VisRectangle rect) {
		// Nichts zu tun
	}

	/*
	 * UTILS
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	/**
	 * Gibt die Position zurück, die ausgewählt wurde, um den Knoten zu
	 * zeichnen.
	 */
	public Coords getSelectedNodePosition(Node node) {
		if (vis.schematicPositionSet())
			return node.getSchematicPosition();
		return node.getTopologicalPosition();
	}

	/**
	 * Konvertiert eine topologische Position in eine Position im
	 * Visualisierungsfenster (in Pixeln).
	 * 
	 * @param topologicalPosition
	 */
	private Point getPositionInWindow(Coords topoPos) {
		return Simple2DVisualization.getPositionInWindow(topoPos, width_height,
				vis);
	}

	@Override
	public boolean onlyHighestPrio() {
		return true;
	}

	public ModelFilter getFilter() {
		return vis.getDataModel().getFilter();
	}

}
