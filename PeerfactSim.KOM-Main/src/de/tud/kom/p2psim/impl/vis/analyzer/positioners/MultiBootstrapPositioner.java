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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners;

import java.util.HashMap;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.vis.analyzer.MultiBootstrapCapable;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Positioniert Knoten (schematische Koordinaten)in separaten Gruppen
 * in einem Gitter.
 * 
 * Alle Knoten gehören zu einer Gruppe, wenn sie die selbe Instanz
 * eines BootstrapManagers verwenden.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 *
 */
public class MultiBootstrapPositioner implements SchematicPositioner{

	static final int DEFAULT_COLUMNS = 4;
	
	float offset_x;
	float offset_y;
	
	int columns = DEFAULT_COLUMNS;
	
	/**
	 * Die Koordinaten des Ring-Mittelpunktes für jeden Bootstrap-Manager, den
	 * eine Knotengruppe besitzt.
	 */
	HashMap<BootstrapManager, Coords> groupPositions = new HashMap<BootstrapManager, Coords>();
	HashMap<BootstrapManager, SchematicPositioner> positioners = new HashMap<BootstrapManager, SchematicPositioner>();
	
	OverlayAdapter adapter;
	private MultiBootstrapCapable mbAdapter;
	
	int actual_column = -1;
	int actual_row = 0;
	
	/**
	 * Standard-Konstruktor
	 * @param adapter
	 * @param mbAdapter
	 */
	public MultiBootstrapPositioner(OverlayAdapter adapter, MultiBootstrapCapable mbAdapter) {
		this(adapter, mbAdapter, DEFAULT_COLUMNS);
	}
	
	/**
	 * Konstruktor mit expliziter Angabe der Felder
	 * @param mbAdapter
	 * @param adapter
	 * @param fields
	 */
	public MultiBootstrapPositioner(OverlayAdapter adapter, MultiBootstrapCapable mbAdapter, int fields) {
		
		this.adapter = adapter;
		this.mbAdapter = mbAdapter;
		
		this.columns = (int)Math.ceil(Math.sqrt(fields -1));
		
		System.out.println("Fields: " + fields + " Columns: " + columns);
		
		offset_x = 0.0f;
		offset_y = 1.0f - 1f/columns;
		
	}

	@Override
	public Coords getSchematicHostPosition(Host host, OverlayNode node) {
		
		Coords startPos = getGroupStartPos(node);
		
		Coords subPos = positioners.get(mbAdapter.getBootstrapManagerFor(node)).getSchematicHostPosition(host, node);
		
		if (subPos == null) return null;
		
		float x = subPos.x/columns;
		float y = subPos.y/columns;
		
		return new Coords(startPos.x + x, startPos.y + y);
	}
	
	protected Coords getGroupStartPos(OverlayNode node) {
		BootstrapManager boostrap_manager = mbAdapter.getBootstrapManagerFor(node);
		Coords position = groupPositions.get(boostrap_manager);
		
		if (position != null) return position;
		Coords new_position = createNewGroupPos();
		groupPositions.put(boostrap_manager, new_position);
		positioners.put(boostrap_manager, adapter.getNewPositioner());
		
		return new_position;
		
	}

	private Coords createNewGroupPos() {
		actual_column++;
		if (actual_column >= columns) {
			actual_row++;
			actual_column = 0;
		}
		
		
		return new Coords(offset_x + (float)actual_column/(float)columns,
				offset_y - (float)actual_row/(float)columns);
	}
	
}
