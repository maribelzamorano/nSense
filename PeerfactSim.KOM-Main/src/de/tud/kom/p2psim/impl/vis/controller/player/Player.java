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

import java.util.ArrayList;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.EventTimeline;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Spielt die Aufzeichnung ab und führt verwandte Operationen aus.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Player {

	/**
	 * Maximale Framerate in ms (optimiert die Rechenleistung)
	 */
	public static final int FPS = 25;

	/**
	 * So viele Eingabe-Zeiteinheiten sind eine Sekunde.
	 */
	public static final long TIME_UNIT_MULTIPLICATOR = 1000000;

	/**
	 * Schritt beim Vor- und Zurückspulen.
	 */
	public static final int FWD_STEP_SIZE = 50;

	ArrayList<PlayerEventListener> listeners = new ArrayList<PlayerEventListener>();

	EventTimeline timeline;

	PlayThread playthread = new PlayThread(this);

	boolean isPlaying;

	boolean loop = false;

	/**
	 * Geschwindigkeit in Millisekunden pro Frame
	 */
	double speed = Config.getValue("Player/Speed", 10000) /10000d;

	/**
	 * Schritt beim Abspielen in Sekunden.
	 */
	protected double quantization = Config
			.getValue("Player/Quantization", 500) / 10000d;

	public EventTimeline getTimeline() {
		return timeline;
	}

	public void setTimeline(EventTimeline timeline) {
		this.timeline = timeline;
	}

	public void setSpeed(float speed) {
		System.out.println("Speed gesetzt auf:" + speed);
	}

	public void play() {
		if (timeline.getMaxTime() > 0) {
			if (!isPlaying) {
				if (timeline.getActualTime() >= timeline.getMaxTime())
					Controller.resetView();
				playthread = new PlayThread(this);
				playthread.setOpen(true);
				playthread.start();
			}
			this.notifyPlaying();
		}
	}

	public void pause() {
		playthread.setOpen(false);
		this.notifyPause();
	}

	public void stop() {
		playthread.setOpen(false);
		Controller.resetView();
		this.notifyStopping();
	}

	public void rev() {
		long jumptime = timeline.getActualTime() - FWD_STEP_SIZE
				* TIME_UNIT_MULTIPLICATOR;

		if (jumptime <= 0)
			timeline.jumpToTime(0);
		else
			timeline.jumpToTime(jumptime);

		this.notifyReverse();
	}

	public void fwd() {

		long jumptime = timeline.getActualTime() + FWD_STEP_SIZE
				* TIME_UNIT_MULTIPLICATOR;

		if (jumptime >= timeline.getMaxTime())
			timeline.jumpToTime(timeline.getMaxTime());
		else
			timeline.jumpToTime(jumptime);

		this.notifyForward();
	}

	public void reset() {
		//Nothing to do
	}
	
	/**
	 * Gibt die Geschwindigkeit des Players in virtuellen Sekunden pro realer Sekunde zurück
	 * 
	 * @param speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Setzt die Geschwindigkeit des Players in virtuellen Sekunden pro realer Sekunde
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		this.notifySpeedChange(speed);
	}

	/**
	 * Gibt die Quantisierung (Frequenz der Refreshrate) des Players in realen Sekunden zurück
	 * 
	 * @param speed
	 */
	public double getQuantization() {
		return quantization;
	}

	/**
	 * Setzt die Quantisierung (Frequenz der Refreshrate) des Players in realen Sekunden
	 * 
	 * @param speed
	 */
	public void setQuantization(double quantization) {
		this.quantization = quantization;
		this.notifyQuantizationChange(quantization);
	}

	public void showTime() {
		System.out.println(timeline.getActualTime());
	}

	/**
	 * Gibt zurück, ob der Player gerade abspielt.
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	protected void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public void addEventListener(PlayerEventListener l) {
		this.listeners.add(l);
	}

	protected void notifySpeedChange(double speed) {
		for (PlayerEventListener l : listeners) {
			l.speedChange(speed);
		}
	}

	protected void notifyPlaying() {
		for (PlayerEventListener l : listeners) {
			l.play();
		}
	}

	protected void notifyStopping() {
		for (PlayerEventListener l : listeners) {
			l.stop();
		}
	}

	protected void notifyPause() {
		for (PlayerEventListener l : listeners) {
			l.pause();
		}
	}

	protected void notifyForward() {
		for (PlayerEventListener l : listeners) {
			l.forward();
		}
	}

	protected void notifyReverse() {
		for (PlayerEventListener l : listeners) {
			l.reverse();
		}
	}

	private void notifyQuantizationChange(double q) {
		for (PlayerEventListener l : listeners) {
			l.quantizationChange(q);
		}
	}

	public void saveSettings() {
		Config.setValue("Player/Speed", (int)(speed*10000d));
		Config.setValue("Player/Quantization", (int) (quantization * 10000d));

	}

	public boolean isLooping() {
		return loop;
	}

	public void setLooping(boolean loop) {
		this.loop = loop;
	}

}
