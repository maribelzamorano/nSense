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


package de.tud.kom.p2psim.impl.vis.ui.common;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.controller.commands.CloseApplication;
import de.tud.kom.p2psim.impl.vis.ui.common.DetailsPane.DetailsPane;
import de.tud.kom.p2psim.impl.vis.ui.common.toolbar.MainToolBar;
import de.tud.kom.p2psim.impl.vis.ui.common.toolbar.PlayerToolBar;
import de.tud.kom.p2psim.impl.vis.util.Config;
import de.tud.kom.p2psim.impl.vis.util.DropBoxLayout;
import de.tud.kom.p2psim.impl.vis.util.GlobalKeyEventDispatcher;
import de.tud.kom.p2psim.impl.vis.util.SplitPaneConfigSaver;

/**
 * Hauptfenster der Visualisierungskomponente
 * 
 * See also <a href="http://www.student.informatik.tu-darmstadt.de/~l_nobach/docs/howto-visualization.pdf"
 * >PeerfactSim.KOM Visualization HOWTO</a>
 * 
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UIMainWindow extends JFrame implements WindowListener {

	/*
	 * Konfigurationspfade
	 */
	static final String CONF_PATH = "UI/MainWindow/";

	static final String CONF_PATH_WIDTH = CONF_PATH + "Width";

	static final String CONF_PATH_HEIGHT = CONF_PATH + "Height";

	static final String CONF_PATH_POSX = CONF_PATH + "PosX";

	static final String CONF_PATH_POSY = CONF_PATH + "PosY";

	/**
	 * 
	 */
	private static final long serialVersionUID = -6552097891551361608L;

	protected static final String SPLITTER_CONF_PATH = "UI/MainWindow/SplitterPos";

	public static final Image WINDOW_ICON = new ImageIcon(
			"images/icons/frame_icon.png").getImage();

	public static final String STANDARD_TITLE = "PeerfactSim.KOM - Visualization | Codename Jamboree";

	protected DetailsPane dp = new DetailsPane();

	public UIMainWindow(JComponent vis_comp) {
		this.createWindowOnStart(vis_comp);
	}

	public void createWindowOnStart(JComponent vis_comp) {
		this.setTitle(STANDARD_TITLE);
		this.setIconImage(WINDOW_ICON);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);

		GlobalKeyEventDispatcher dispatcher = new GlobalKeyEventDispatcher(this);
		dispatcher.addKeyListener(Controller.getVisApi().getVisKeyListener());

		this.setSize(Config.getValue(CONF_PATH_WIDTH, 800), Config.getValue(
				CONF_PATH_HEIGHT, 600));
		this.setLocation(new Point(Config.getValue(CONF_PATH_POSX, 0), Config
				.getValue(CONF_PATH_POSY, 0)));

		/**
		 * Ein beweglicher Splitter zwischen Visualisierungskomponente und
		 * Detailabschnitt.
		 */
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.dp,
				vis_comp);
		sp.setDividerLocation(Config.getValue(SPLITTER_CONF_PATH, 350));
		sp.addPropertyChangeListener(new SplitPaneConfigSaver(
				SPLITTER_CONF_PATH));

		this.getContentPane().setLayout(new BorderLayout());
		/**
		 * UI-Komponente
		 */
		this.getContentPane().add(sp, BorderLayout.CENTER);

		/**
		 * Menüleisten
		 */
		createToolBars();
		
		/**
		 * Timeline-Slider
		 */
		this.getContentPane().add(new TimelineSlider(), BorderLayout.SOUTH);

	}
	
	void createToolBars() {
		JPanel toolbarP = new JPanel();
		toolbarP.setLayout(new DropBoxLayout(DropBoxLayout.MODE_WRAP));
		toolbarP.add(new MainToolBar());
		toolbarP.add(Controller.getVisApi().getVisualizationSpecificToolbar(), BorderLayout.NORTH);
		toolbarP.add(new PlayerToolBar(), BorderLayout.NORTH);
		this.add(toolbarP, BorderLayout.NORTH);
	}

	public DetailsPane getDetailsPane() {
		return dp;
	}

	/**
	 * Setzt den Titel-Dateinamen
	 * 
	 * @param filename
	 */
	public void setTitleFileName(String shownName) {
		setTitle(UIMainWindow.STANDARD_TITLE + " | \"" + shownName + "\"");
	}

	/**
	 * Speichert Einstellungen wie Fenstergröße o.ä.
	 */
	public void saveSettings() {
		Config.setValue(CONF_PATH_WIDTH, this.getWidth());
		Config.setValue(CONF_PATH_HEIGHT, this.getHeight());
		Config.setValue(CONF_PATH_POSX, this.getX());
		Config.setValue(CONF_PATH_POSY, this.getY());
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		new CloseApplication().execute();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Baut das Fenster neu auf.
	 */
	public void reset() {
		dp.reset();
	}

}
