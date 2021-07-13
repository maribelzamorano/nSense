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


package de.tud.kom.p2psim.impl.vis.visualization2d.toolbar;

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.controller.commands.Zoom;
import de.tud.kom.p2psim.impl.vis.ui.common.toolbar.elements.SimpleToolbarButton;
import de.tud.kom.p2psim.impl.vis.visualization2d.Simple2DVisualization;

/**
 * Button zum Hineinzoomen
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 06.11.2008
 * 
 */
public class ZoomButton extends SimpleToolbarButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8883086992639815840L;

	static final ImageIcon iconIn = new ImageIcon("images/icons/zoomIn.png");

	static final ImageIcon iconOut = new ImageIcon("images/icons/zoomOut.png");

	static final String tooltipIn = "Hereinzoomen";

	static final String tooltipOut = "Herauszoomen";

	/**
	 * Standard-Konstruktor
	 * 
	 * @param zoomOut
	 * @param vis
	 */
	public ZoomButton(boolean zoomOut, Simple2DVisualization vis) {
		super();
		this.setIcon(zoomOut ? iconOut : iconIn);
		this.setToolTipText(zoomOut ? tooltipOut : tooltipIn);
		this.addCommand(new Zoom(zoomOut, vis));
	}

}
