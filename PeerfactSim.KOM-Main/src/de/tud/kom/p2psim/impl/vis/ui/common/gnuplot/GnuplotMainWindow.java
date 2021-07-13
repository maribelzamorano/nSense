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


package de.tud.kom.p2psim.impl.vis.ui.common.gnuplot;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnuplotMainWindow extends JDialog implements WindowListener {

	/*
	 * Konfigurationspfade
	 */
	static final String CONF_PATH = "UI/GnuplotExportWindow/";

	static final String CONF_PATH_WIDTH = CONF_PATH + "Width";

	static final String CONF_PATH_HEIGHT = CONF_PATH + "Height";

	static final String CONF_PATH_POSX = CONF_PATH + "PosX";

	static final String CONF_PATH_POSY = CONF_PATH + "PosY";

	static final String CONF_PATH_SELECTED = CONF_PATH + "SelectedTab";

	/**
	 * 
	 */
	private static final long serialVersionUID = -8220820572696470496L;

	JTabbedPane tabbedPane;

	public GnuplotMainWindow() {
		this.addWindowListener(this);

		this.setModal(true);
		this.setTitle("PeerfactSim.KOM | Gnuplot-Export");
		this.setIconImage(Controller.getUIMainWindow().getIconImage());

		tabbedPane = new JTabbedPane();
		ImageIcon metric = new ImageIcon("images/icons/misc/Metric16_16.png");
		ImageIcon peer = new ImageIcon(
				"images/icons/model/OverlayNode16_16.png");
		ImageIcon connection = new ImageIcon(
				"images/icons/model/OverlayEdge16_16.png");

		tabbedPane.addTab("Metriken über Zeit", metric,
				new MetricsVsTimeDialog(),
				"Exportiert ausgewählte Metriken über eine Zeitspanne");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_M);

		tabbedPane.addTab("Peers über Zeit", peer,
				new MetricPeerVsTimeDialog(),
				"Gegenüberstellung von Peers bzgl. einer Metrik über Zeit.");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);

		tabbedPane
				.addTab(
						"Verbindungen über Zeit",
						connection,
						new MetricConnectionVsTimeDialog(),
						"Gegenüberstellung von Verbindungen zwischen Peers bzgl. einer Metrik über Zeit.");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);

		this.getContentPane().add(tabbedPane);

		this.setSize(Config.getValue(CONF_PATH_WIDTH, 500), Config.getValue(
				CONF_PATH_HEIGHT, 500));
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config
				.getValue(CONF_PATH_POSY, 0)));

		tabbedPane.setSelectedIndex(Config.getValue(CONF_PATH_SELECTED, 0));
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		//Nothing to do
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//Nothing to do
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.saveSettings();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		//Nothing to do
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		//Nothing to do
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		//Nothing to do
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		//Nothing to do
	}

	/**
	 * Speichert Einstellungen wie Fenstergröße o.Ä.
	 */
	protected void saveSettings() {
		Config.setValue(CONF_PATH_WIDTH, this.getWidth());
		Config.setValue(CONF_PATH_HEIGHT, this.getHeight());
		Config.setValue(CONF_PATH_POSX, this.getX());
		Config.setValue(CONF_PATH_POSY, this.getY());
		Config.setValue(CONF_PATH_SELECTED, tabbedPane.getSelectedIndex());
	}

}
