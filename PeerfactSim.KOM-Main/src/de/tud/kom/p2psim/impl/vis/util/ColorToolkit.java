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


package de.tud.kom.p2psim.impl.vis.util;

import java.awt.Color;

/**
 * Werkzeuge zur Farbmanipulation
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 24.11.2008
 * 
 */
public class ColorToolkit {

	/**
	 * Gibt eine hellere Version der übergebenen Farbe zurück, die z.B. als
	 * Hintergrund verwendet werden kann.
	 * 
	 * @param color
	 * @return
	 */
	public static Color getLightColorFor(Color color) {
		float weaknessFactor = 0.1f;

		float[] compArray = color.getRGBColorComponents(null);

		// Farben invertieren
		for (int i = 0; i < 3; i++) {
			compArray[i] = 1 - compArray[i];
		}

		// Macht die Farbe um den Faktor weißer.
		for (int i = 0; i < 3; i++) {
			compArray[i] = compArray[i] * weaknessFactor;
		}

		// Zieht den Graustich aus der Farbe.
		float grayPart = Math.min(compArray[0], Math.min(compArray[1],
				compArray[2]));
		for (int i = 0; i < 3; i++) {
			compArray[i] -= grayPart;
		}

		// Farben zurück invertieren
		for (int i = 0; i < 3; i++) {
			compArray[i] = 1 - compArray[i];
		}

		return new Color(compArray[0], compArray[1], compArray[2]);
	}

	public static Color weighColor(Color cl1, Color cl2, double weight) {
		if (weight <= 0)
			return cl1;
		if (weight >= 1)
			return cl2;

		double invWeight = 1 - weight;

		Color result = new Color((int) (cl1.getRed() * weight + cl2.getRed()
				* invWeight), (int) (cl1.getGreen() * weight + cl2.getGreen()
				* invWeight), (int) (cl1.getBlue() * weight + cl2.getBlue()
				* invWeight));

		return result;
	}

}
