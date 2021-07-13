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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners.multi;

import java.util.HashMap;
import java.util.Iterator;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.MultiPositioner;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.SchematicPositioner;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Positioniert Knoten (schematische Koordinaten)in separaten Gruppen in einem
 * Gitter.
 * 
 * Alle Knoten gehören zu einer Gruppe, wenn sie die selbe Instanz eines
 * BootstrapManagers verwenden.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class MultiBootstrapPositioner extends MultiPositioner {

	static final int DEFAULT_COLUMNS = 4;

	float offset_x;

	float offset_y;

	int columns = DEFAULT_COLUMNS;

	/**
	 * Die Koordinaten des Ring-Mittelpunktes für jeden Bootstrap-Manager, den
	 * eine Knotengruppe besitzt.
	 */
	HashMap<Object, Coords> groupPositions = new HashMap<Object, Coords>();

	HashMap<Object, SchematicPositioner> positioners = new HashMap<Object, SchematicPositioner>();

	int actual_column = -1;

	int actual_row = 0;

	/**
	 * Legt die Felder für den MultiBootstrapPositioner fest.
	 * 
	 * Wird von der XML-Config aufgerufen.
	 * 
	 * @param fields
	 */
	public void setFields(int fields) {
		this.columns = (int) Math.ceil(Math.sqrt(fields - 1));

		offset_x = 0.0f;
		offset_y = 1.0f - 1f / columns;
	}

	@Override
	public Coords getSchematicHostPosition(Host host) {
		Iterator<OverlayNode> it = host.getOverlays();

		while (it.hasNext()) {
			OverlayNode subNetNode = it.next();
			if (getSubNetAdapter().isDedicatedOverlayImplFor(
					subNetNode.getClass()))
				return getSchematicHostPosition(host, subNetNode);
		}
		return null;
	}

	/**
	 * Gibt die schematische Host-Position zurück für gegebene
	 * Overlay-Implementierung.
	 * 
	 * @param host
	 * @param node
	 * @return schematische Host-Position
	 */
	public Coords getSchematicHostPosition(Host host, OverlayNode node) {

		Coords startPos = getGroupStartPos(node);

		Coords subPos = positioners.get(
				getSubNetAdapter().getBootstrapManagerFor(node))
				.getSchematicHostPosition(host, node);

		if (subPos == null)
			return null;

		float x = subPos.x / columns;
		float y = subPos.y / columns;

		return new Coords(startPos.x + x, startPos.y + y);
	}

	protected Coords getGroupStartPos(OverlayNode node) {
		Object boostrap_manager = getSubNetAdapter().getBootstrapManagerFor(
				node);
		Coords position = groupPositions.get(boostrap_manager);

		if (position != null)
			return position;
		Coords new_position = createNewGroupPos();
		groupPositions.put(boostrap_manager, new_position);
		positioners
				.put(boostrap_manager, getSubNetAdapter().getNewPositioner());

		return new_position;

	}

	/**
	 * Liefert den Adapter des Subnetzes (der Subnetze)
	 * 
	 * @return
	 */
	private OverlayAdapter getSubNetAdapter() {
		return this.getAdapterAt(0);
	}

	private Coords createNewGroupPos() {
		actual_column++;
		if (actual_column >= columns) {
			actual_row++;
			actual_column = 0;
		}

		return new Coords(offset_x + (float) actual_column / (float) columns,
				offset_y - (float) actual_row / (float) columns);
	}

}
