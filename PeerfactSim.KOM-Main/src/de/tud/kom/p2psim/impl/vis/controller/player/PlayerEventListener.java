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


package de.tud.kom.p2psim.impl.vis.controller.player;

/**
 * Reagiert auf Ereignisse des Players
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface PlayerEventListener {

	/**
	 * Ausführung beim Beginn des Abspielvorgangs
	 */
	public void play();

	/**
	 * Ausführung bei Pause
	 */
	public void pause();

	/**
	 * Ausführung beim Stoppen des Abspielvorgangs
	 */
	public void stop();

	/**
	 * Ausführung beim Forward
	 */
	public void forward();

	/**
	 * Ausführung bei Reverse
	 */
	public void reverse();

	/**
	 * Ausführung, wenn die Abspielgeschwindigkeit sich verändert
	 * 
	 * @param speed
	 */
	public void speedChange(double speed);

	/**
	 * Ausführung, wenn die Quantisierung sich verändert
	 */
	public void quantizationChange(double quantization);

}
