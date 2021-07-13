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


package de.tud.kom.p2psim.impl.vis.api.metrics;

import java.awt.Color;

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Eine Metrik in diesem Sinne ist eine in einem String ausdrückbare Größe,
 * die zu jedem Zeitpunkt an einem Knoten, einer Kante oder dem Gesamtgraphen
 * gemessen werden kann. Sie besteht aus einem <b>Namen </b> und einem
 * objektabhängigen <b>Wert</b> Zum besseren Verständnis hilft es, sich den
 * Code einiger Beispiele näher anzuschauen.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class Metric {

	public Metric() {
		this.loadSettings();
	}

	protected final String base_path = "Metrics/"
			+ this.getClass().getSimpleName();

	protected final String activated_path = base_path + "/Activated";

	protected final String color_path = base_path + "/Color";

	/**
	 * Standardfarbe für eine Metrik.
	 */
	protected Color color = Color.BLACK;

	protected boolean isActivated = false;

	/**
	 * Gibt den Namen der Metrik zurück
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Gibt den Namen der Einheit zurück, falls vorhanden. Sonst leeren String.
	 * Der String ist maximal ca 5 Zeichen lang.
	 * 
	 * @return
	 */
	public abstract String getUnit();

	/**
	 * Gibt an, ob die Metrik numerisch ist, also z.B. für Gnuplot verwendet
	 * werden kann. Die Metrik gibt in diesem Fall mit getValue einen Wert
	 * zurück, den Gnuplot versteht, also eine Zahl o.Ä.
	 * 
	 * @return
	 */
	public abstract boolean isNumeric();

	public boolean isActivated() {
		return this.isActivated;
	}

	/**
	 * Aktiviert die Metrik zum Anzeigen in der Grafik. Ist die Metrik
	 * aktiviert, wird sie in der Grafik angezeigt, ist sie nicht aktiviert,
	 * wird sie nicht angezeigt.
	 * 
	 * @param true, wenn sie aktiviert werden soll, false, wenn sie deaktiviert
	 *        werden soll
	 */
	public void setActivated(boolean activated) {
		this.isActivated = activated;
	}

	/**
	 * Gibt die charakteristische Farbe für die Darstellung der Metrik zurück.
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Setzt die charakteristische Farbe der Metrik. Wird nicht beachtet, wenn
	 * ein Wert in der XML-Config liegt.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {

		int red = Config.getValue(color_path + "/Red", color.getRed());
		int green = Config.getValue(color_path + "/Green", color.getGreen());
		int blue = Config.getValue(color_path + "/Blue", color.getBlue());

		this.color = new Color(red, green, blue);
	}

	public void loadSettings() {
		if (Config.getValue(activated_path, 0) == 0)
			isActivated = false;
		else
			isActivated = true;
	}

	/**
	 * Speichert alle Einstellungen, die für die Metrik getätigt wurden
	 */
	public void saveSettings() {
		if (isActivated == true)
			Config.setValue(activated_path, 1);
		else
			Config.setValue(activated_path, 0);
	}

	/**
	 * Gibt ein repräsentierendes Icon zurück. Kann null sein!
	 * 
	 * @return
	 */
	public ImageIcon getRepresentingIcon() {
		return null;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
