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


package de.tud.kom.p2psim.impl.vis.api.visualization;

import java.awt.event.KeyListener;

import javax.swing.JToolBar;

import de.tud.kom.p2psim.impl.vis.model.MetricObject;
import de.tud.kom.p2psim.impl.vis.model.flashevents.FlashEvent;

/**
 * Interface einer Klasse, die für das Visualisieren eines Datenmodells
 * zuständig sein soll.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface Visualization {

	/**
	 * Ein individuelles Menü für die Visualisierungskomponente
	 */
	public JToolBar getVisualizationSpecificToolbar();

	public void addVisActionListener(VisActionListener val);

	public void queueFlashEvent(FlashEvent e);

	/**
	 * Setzt ein Hintergrundbild. Falls keins verwendet werden soll, null
	 * angeben.
	 * 
	 * @param path
	 */
	public void setBackgroundImagePath(String path);

	/**
	 * Gibt den KeyListener zurück, der die Tastatureingaben für die
	 * Visualisierungsoberfläche behandelt
	 * 
	 * @return
	 */
	public KeyListener getVisKeyListener();

	/**
	 * Gibt das im Visualisierungsfenster gerade ausgewählte Objekt zurück
	 * 
	 * @return
	 */
	public MetricObject getSelectedObject();

	public void setSimulatorStillRunning(boolean running);

}
