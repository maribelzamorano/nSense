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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.tud.kom.p2psim.impl.vis.controller.commands.Forward;
import de.tud.kom.p2psim.impl.vis.controller.commands.PlayPause;
import de.tud.kom.p2psim.impl.vis.controller.commands.Reverse;

/**
 * Behandelt Keyboard-Eingaben auf dem Vis-Panel
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 13.11.2008
 * 
 */
public class DefaultKeyboardHandler implements KeyListener {

	/**
	 * Pixel, um die das Bild verschoben wird beim Pfeiltastendruck.
	 */
	public static final int SHIFT_AMOUNT = 10;

	Simple2DVisualization vis;

	/**
	 * Standard-Konstruktor
	 * 
	 * @param vis
	 */
	public DefaultKeyboardHandler(Simple2DVisualization vis) {
		this.vis = vis;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
			vis.zoom(true, null);
		else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
			vis.zoom(false, null);
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			vis.shiftView(new Point(0, -SHIFT_AMOUNT));
		else if (e.getKeyCode() == KeyEvent.VK_UP)
			vis.shiftView(new Point(0, SHIFT_AMOUNT));
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			vis.shiftView(new Point(-SHIFT_AMOUNT, 0));
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			vis.shiftView(new Point(SHIFT_AMOUNT, 0));
		else if (e.getKeyCode() == KeyEvent.VK_S)
			new PlayPause().execute();
		else if (e.getKeyCode() == KeyEvent.VK_D)
			new Forward().execute();
		else if (e.getKeyCode() == KeyEvent.VK_A)
			new Reverse().execute();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// Nothing to do
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Nothing to do
	}

	/**
	 * Debug-Ausgabe
	 * 
	 * @param o
	 */
	public void dbg(Object o) {
		// System.err.println(this.getClass().getSimpleName() + ": " +
		// (o==null?"null":o.toString()));
	}

}
