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


package de.tud.kom.p2psim.impl.vis.util.visualgraph;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GraphMath {

	/**
	 * Berechnet, ob ein Punkt c die Distanz distance von der Strecke, die durch
	 * a und b gegeben ist, hat.
	 * 
	 * @param line_a
	 * @param line_b
	 * @param c
	 * @param distance
	 * @return
	 */
	public static boolean calculateDistanceFromLine(Point line_a, Point line_b,
			Point c, int distance) {

		float x1 = line_a.x;
		float y1 = line_a.y;

		float x2 = line_b.x;
		float y2 = line_b.y;

		// System.out.println("Punkt1: " + x1 + ", " + y1);
		// System.out.println("Punkt2: " + x2 + ", " + y2);
		// System.out.println("Click: " + c.x + ", " + c.y);

		// TODO: x1=y1-Fall, x2=y2 -Fall

		float x3 = c.x;
		float y3 = c.y;

		// F�lle der Division durch Null abfangen.
		if (x1 == x2) {
			if (between(c.y, y1, y2)) {
				return (x1 - c.x <= distance && x1 - c.x >= -distance);
			} else
				return false;
		} else if (y1 == y2) {
			if (between(c.x, x1, x2)) {
				return (y1 - c.y <= distance && y1 - c.y >= -distance);
			} else
				return false;
		}

		float m = (y2 - y1) / (x2 - x1); // Steigung a, b

		// System.out.println("Steigung: " + m);

		float o = -1 / m; // Orthogonale zur Steigung

		// System.out.println("Orthogonale Steigung: " + o);

		float b = y1 - m * x1;

		// System.out.println("B: " + b);

		float p = y3 - o * x3;

		// System.out.println("P: " + p);

		float resx = (b - p) / (o - m);

		/*
		 * Der Fall wird behandelt, dass der Punkt auf der Gerade, aber
		 * au�erhalb der Strecke liegt.
		 */
		if (!between(resx, x1, x2))
			return false;

		float resy = m * resx + b;

		// System.out.println("Punkt: " + resx + ", " + resy);
		// System.out.println("Distanz: " + c.distance(resx, resy));

		return c.distanceSq(resx, resy) <= distance * distance;

	}

	/**
	 * Berechnet, ob die Zahl a zwischen p1 und p2 liegt.
	 * 
	 * @param a
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean between(float a, float p1, float p2) {
		if (p1 < p2) {
			if (a <= p1 || a >= p2)
				return false;
		} else {
			if (a <= p2 || a >= p1)
				return false;
		}

		return true;
	}

	public static boolean between(int a, int p1, int p2) {
		if (p1 < p2) {
			if (a <= p1 || a >= p2)
				return false;
		} else {
			if (a <= p2 || a >= p1)
				return false;
		}

		return true;
	}

	public static Point getLineRectIntersection(Point linestart, Point lineend,
			Rectangle rect) {

		if (linestart.y != lineend.y) {
			if (between(rect.y, linestart.y, lineend.y)) {
				int aX = linestart.x
						+ (int) (((double) (rect.y - linestart.y))
								* ((double) (linestart.x - lineend.x)) / (linestart.y - lineend.y));
				if (aX >= rect.x && aX <= rect.x + rect.width)
					return new Point(aX, rect.y);
			}
			if (between(rect.y + rect.height, linestart.y, lineend.y)) {

				int cX = linestart.x
						+ (int) (((double) ((rect.y + rect.height) - linestart.y))
								* ((double) (linestart.x - lineend.x)) / (linestart.y - lineend.y));
				if (cX >= rect.x && cX <= rect.x + rect.width)
					return new Point(cX, rect.y + rect.height);
			}
		}
		if (linestart.x != lineend.x) {
			if (between(rect.x, linestart.x, lineend.x)) {
				int bY = linestart.y
						+ (int) (((double) (rect.x - linestart.x))
								* ((double) (linestart.y - lineend.y)) / (linestart.x - lineend.x));
				if (bY >= rect.y && bY <= rect.y + rect.height)
					return new Point(rect.x, bY);
			}
			if (between(rect.x + rect.width, linestart.x, lineend.x)) {

				int dY = linestart.y
						+ (int) (((double) ((rect.x + rect.width) - linestart.x))
								* ((double) (linestart.y - lineend.y)) / (linestart.x - lineend.x));
				if (dY >= rect.y && dY <= rect.y + rect.height)
					return new Point(rect.x + rect.width, dY);
			}
		}

		return null;
	}

}
