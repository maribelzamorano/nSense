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


package de.tud.kom.p2psim.impl.vis.ui.common.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConfigWindow extends JDialog implements WindowListener, ActionListener{

	/*
	 * Konfigurationspfade
	 */
	static final String CONF_PATH = "UI/ConfigWindow/";
	static final String CONF_PATH_WIDTH = CONF_PATH + "Width";
	static final String CONF_PATH_HEIGHT = CONF_PATH + "Height";
	static final String CONF_PATH_POSX = CONF_PATH + "PosX";
	static final String CONF_PATH_POSY = CONF_PATH + "PosY";
	static final String CONF_PATH_SELECTED = CONF_PATH + "SelectedTab";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8220820572696470496L;

	Vector<AbstractConfigTab> configTabs = new Vector<AbstractConfigTab>();
	JTabbedPane tabbedPane;
	
	JButton okButton;
	JButton commitButton;
	JButton cancelButton;
	
	
	public ConfigWindow() {
		this.addWindowListener(this);
		
		this.setModal(true);
		this.setTitle("PeerfactSim.KOM | Einstellungen");
		this.setIconImage(Controller.getUIMainWindow().getIconImage());
		this.setLayout(new BorderLayout());
		
		tabbedPane = new JTabbedPane();
		ImageIcon general = new ImageIcon("images/icons/ConfigButton.png");
		ImageIcon gnuplot = new ImageIcon("images/icons/GnuplotButton.png");
		
		//1. Tab
		AbstractConfigTab generalTab = new GeneralTab();
		configTabs.add(generalTab);
		tabbedPane.addTab("Allgemein", general, generalTab,
        				  "Generelle Einstellungen");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_G);
		
		//2. Tab
		AbstractConfigTab gnuplotTab = new GnuplotTab();
		configTabs.add(gnuplotTab);
		tabbedPane.addTab("Gnuplot", gnuplot, gnuplotTab,
		                  "Einstellungen für den Gnuplot-Export");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_N);
		
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		this.setSize(Config.getValue(CONF_PATH_WIDTH, 500), Config.getValue(CONF_PATH_HEIGHT, 500));
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config.getValue(CONF_PATH_POSY, 0)));
		
		tabbedPane.setSelectedIndex(Config.getValue(CONF_PATH_SELECTED, 0));
		
		makeButtons();
		
	}

	private void makeButtons() {
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		pane.add(okButton);
		
		commitButton = new JButton("Übernehmen");
		commitButton.addActionListener(this);
		pane.add(commitButton);
		
		cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(this);
		pane.add(cancelButton);
		
		this.add(pane, BorderLayout.SOUTH);
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

	protected void commitSettingsAllTabs() {
		for (AbstractConfigTab t : configTabs) t.commitSettings();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		
		if (src == okButton) {
			commitSettingsAllTabs();
			this.saveSettings();
			this.setVisible(false);
		} else if (src == commitButton) {
			commitSettingsAllTabs();
		} else if (src == cancelButton){
			this.saveSettings();
			this.setVisible(false);
		}
	}
	
}
