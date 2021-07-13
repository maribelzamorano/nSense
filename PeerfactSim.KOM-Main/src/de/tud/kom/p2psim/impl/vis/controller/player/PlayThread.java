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

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.EventTimeline;

/**
 * Thread, von der Player-Klasse verwendet, um den Abspielvorgang zu steuern.
 * 
 * Hinweis: Dies ist nicht der einzige Thread, der zeichnet. Die AWT-EventQueue
 * zeichnet die meiste Zeit!.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PlayThread extends Thread {

	boolean open = false;

	Player invokingPlayer;

	long lastFrameBegun = 0;

	long lastFrameDone = 0;

	int framesNotDone;

	/**
	 * Standard-Konstruktor
	 * 
	 * @param p
	 */
	public PlayThread(Player p) {
		this.invokingPlayer = p;
	}

	@Override
	public void run() {
		this.setName("PlayerThread");
		EventTimeline tl = invokingPlayer.getTimeline();

		while (true) {
			invokingPlayer.setPlaying(true);

			while (tl.getActualTime() < tl.getMaxTime() && open) {
				this.doFrame(tl);
				try {

					long timeToSleep = (long) (invokingPlayer
							.getQuantization()*1000d);

					if (timeToSleep > 0)
						Thread.sleep(timeToSleep);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			invokingPlayer.setPlaying(false);

			if (!invokingPlayer.isLooping() || !open)
				break;
			Controller.resetView();
		}

		if (tl.getActualTime() >= tl.getMaxTime())
			invokingPlayer.notifyStopping();

	}

	/**
	 * Zeichnet einen Frame, bzw. stellt ihn in die AWT-EventQueue.
	 * 
	 * @param tl
	 */
	public void doFrame(EventTimeline tl) {

		this.lastFrameBegun = System.currentTimeMillis();

		try {
			
			double speed = invokingPlayer.getSpeed();
			double q = invokingPlayer.getQuantization();
			
			long timeDiff = (long) (speed * q * Player.TIME_UNIT_MULTIPLICATOR);
			
			//System.out.println("Time diff: " +  speed + "*" + q + "*" + Player.TIME_UNIT_MULTIPLICATOR + "=" + timeDiff);
			
			tl.jumpToTime(tl.getActualTime() + timeDiff);

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		this.lastFrameDone = System.currentTimeMillis();
	}

	/**
	 * Schließt den Thread, wenn open=false.
	 * 
	 * @param open
	 */
	public void setOpen(boolean open) {
		this.open = open;
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
