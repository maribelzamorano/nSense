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
 * Zum Debuggen des Players
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ConsolePlayerNotifier implements PlayerEventListener {

	@Override
	public void forward() {
		System.out.println("PLAYER: Forward");

	}

	@Override
	public void pause() {
		System.out.println("PLAYER: Pause");
	}

	@Override
	public void play() {
		System.out.println("PLAYER: Play");
	}

	@Override
	public void reverse() {
		System.out.println("PLAYER: Reverse");
	}

	@Override
	public void stop() {
		System.out.println("PLAYER: Stop");
	}

	@Override
	public void speedChange(double speed) {
		System.out.println("Speed Change: " + speed + "ms");
	}

	@Override
	public void quantizationChange(double quantization) {
		System.out.println("Quantization Change: " + quantization + "ms");
	}

}
