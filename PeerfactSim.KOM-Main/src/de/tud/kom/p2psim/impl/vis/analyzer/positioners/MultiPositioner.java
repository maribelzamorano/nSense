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

import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Ein Positioner, der sämtliche Overlay-Adapter und deren Positioner
 * zur Hilfe nimmt, um Knoten zu platzieren
 * @author  <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 20.11.2008
 *
 */
public abstract class MultiPositioner {

	List<OverlayAdapter> adapters = null;
	
	/**
	 * Setzt die verwendeten OverlayAdapter
	 * @param adapters
	 */
	public void setAdapters(List<OverlayAdapter> adapters) {
		this.adapters = adapters;
	}
	
	/**
	 * Liefert den Positioner an der XML-Config-Stelle posInCfg
	 * @param posInCfg
	 * @return
	 */
	protected OverlayAdapter getAdapterAt(int posInCfg) {
		return adapters.get(posInCfg);
	}
	
	protected List<OverlayAdapter> getAllAdapters() {
		return adapters;
	}

	/**
	 * Gibt die schematische Position für host zurück.
	 * @param host
	 * @return die schematische Position für host
	 */
	public abstract Coords getSchematicHostPosition(Host host);
	
}
