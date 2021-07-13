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


package de.tud.kom.p2psim.impl.vis.model;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * Ein Objekt, das außer einer Klasse einen unterscheidbaren Typ hat.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 11.12.2008
 * 
 */
public interface TypeObject {

	/**
	 * Gibt eine für den Typen eindeutige ID aus
	 * 
	 * @return
	 */
	public int getUniqueTypeIdentifier();

	/**
	 * Gibt einen Namen für diesen Typen zurück
	 * 
	 * @return
	 */
	public String getTypeName();

	/**
	 * Gibt ein Icon für den Typ dieses Objekts zurück.
	 * 
	 * @return
	 */
	public ImageIcon getRepresentingIcon();

	/**
	 * Gibt eine Farbe für den Typ dieses Objekts zurück.
	 * 
	 * @return
	 */
	public Color getColor();

}
