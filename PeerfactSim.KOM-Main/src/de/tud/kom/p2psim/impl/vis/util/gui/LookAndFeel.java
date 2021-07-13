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


package de.tud.kom.p2psim.impl.vis.util.gui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.UIManager;

import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class LookAndFeel {

	public static Vector<UIManager.LookAndFeelInfo> getAllLookAndFeels() {
		Vector<UIManager.LookAndFeelInfo> v = new Vector<UIManager.LookAndFeelInfo>();
		v.addAll(Arrays.asList(UIManager.getInstalledLookAndFeels()));
		return v;
	}

	public static void setLookAndFeel() {
		setLookAndFeel(Config.getValue("UI/LookAndFeel", getDefaultLaF()));
	}

	private static String getDefaultLaF() {
		return UIManager.getSystemLookAndFeelClassName();
	}

	public static String getActivatedLookAndFeel() {
		return UIManager.getLookAndFeel().getClass().getName();
	}

	/**
	 * Ändert das LookAndFeel entsprechend der Einstellungen.
	 * 
	 */
	public static void setLookAndFeel(String laf) {

		try {
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			System.out.println("Konnte Look-and-Feel " + laf + " nicht laden. "
					+ "Wahrscheinlich ist dies nicht installiert.");
		}
	}

	/*
	 * private static boolean setWindowsLookAndFeel() { try {
	 * UIManager.setLookAndFeel
	 * ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); return true; }
	 * catch (Exception e) {
	 * System.out.println("Konnte Windows LaF nicht laden, versuche es mit GTK"
	 * ); return false; } }
	 * 
	 * private static boolean setGTKLookAndFeel() { try {
	 * UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	 * return true; } catch (Exception e) {
	 * System.out.println("Konnte GTK LaF nicht laden, versuche es mit Default"
	 * ); return false; } }
	 */
}
