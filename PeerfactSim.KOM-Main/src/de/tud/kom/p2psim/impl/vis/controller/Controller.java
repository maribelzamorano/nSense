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


package de.tud.kom.p2psim.impl.vis.controller;

import javax.swing.JComponent;

//import de.tud.kom.p2psim.impl.util.vis.controller.player.ConsolePlayerNotifier;
import de.tud.kom.p2psim.impl.vis.api.visualization.Visualization;
import de.tud.kom.p2psim.impl.vis.controller.player.Player;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;
import de.tud.kom.p2psim.impl.vis.model.EventTimeline;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.ui.common.UIMainWindow;
import de.tud.kom.p2psim.impl.vis.util.Config;
import de.tud.kom.p2psim.impl.vis.util.gui.LookAndFeel;
import de.tud.kom.p2psim.impl.vis.visualization2d.Simple2DVisualization;

/**
 * Steuert die Komponenten der Visualisierungsoberfläche.
 * 
 * 
 * See also <a href="http://www.student.informatik.tu-darmstadt.de/~l_nobach/docs/howto-visualization.pdf"
 * >PeerfactSim.KOM Visualization HOWTO</a>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Controller {

	/**
	 * Das UI-Hauptfenster
	 */
	protected static UIMainWindow ui;

	/**
	 * Die Visualisierungskomponente mit ihrer API
	 */
	protected static Visualization visualization_api;

	/**
	 * Die Visualisierungskomponente als SWING-Komponente
	 */
	protected static JComponent vis_component;

	/**
	 * Das Datenmodell der Visualisierungsgrafik
	 */
	protected static VisDataModel model = null; // new VisDataModel();

	/**
	 * Die Event-Timeline
	 */
	protected static EventTimeline timeline;

	/**
	 * Der Player
	 */
	protected static Player player;

	/**
	 * Initialisiert die Anwendung beim Start. waitingForSim gibt an, ob die
	 * Anwendung auf die Verarbeitung des Simulators warten soll, oder sofort
	 * loslegen kann.
	 */
	public static void init() {

		LookAndFeel.setLookAndFeel();

		MetricsBase.init();

		// model = new VisDataModel(new Coords(20f, 20f));

		player = new Player();
		//player.addEventListener(new ConsolePlayerNotifier()); //zum Debuggen

		Simple2DVisualization vis2d = new Simple2DVisualization();
		visualization_api = vis2d;
		vis_component = vis2d;
		ui = new UIMainWindow(vis_component);
		ui.setVisible(true);

		/*
		 * Meldet das DetailsPane als Listener für Klicks in der Visualisierung
		 * an.
		 */
		visualization_api.addVisActionListener(ui.getDetailsPane());

	}

	/**
	 * Fährt die Anwendung herunter.
	 */
	public static void deinit() {
		ui.saveSettings();
		player.saveSettings();
		MetricsBase.saveSettings();

		Config.writeXMLFile();

		System.exit(0);
	}

	/**
	 * Liefert die API zur Visualisierungskomponente, z.B. zum
	 * Hinzufügen/Modifizieren von Knoten und Kanten zur Laufzeit
	 * 
	 * @return
	 */
	public static Visualization getVisApi() {
		return visualization_api;
	}

	/**
	 * Liefert das UI-Hauptfenster der Anwendung.
	 * 
	 * @return
	 */
	public static UIMainWindow getUIMainWindow() {
		return ui;
	}

	/**
	 * Liefert das gerade geladene Datenmodell. Ist keins geladen, wird null
	 * zurückgegeben.
	 * 
	 * @return
	 */
	public static VisDataModel getModel() {
		return model;
	}

	/**
	 * Setzt das Modell und die Darstellung zurück.
	 */
	public static void resetView() {
		getPlayer().reset();
		getModel().reset();
		getUIMainWindow().reset();
		VisDataModel.needsRefresh();
	}

	/**
	 * Liefert die Timeline. Ist identisch mit getModel().getTimeline().
	 * 
	 * @return
	 */

	public static EventTimeline getTimeline() {
		return model.getTimeline();
	}

	/**
	 * Lädt eine Aufzeichnung in die Anwendung. shownName ist dabei ein Name,
	 * der für die geladene Aufz. steht, z.B. "modell.peerfact" oder
	 * "Unbenannt".
	 * 
	 * @param model
	 * @param shownName
	 */
	public static void loadModelFrontend(VisDataModel model) {
		loadModelBackend(model);

		connectModelToUI();
	}

	public static void loadModelBackend(VisDataModel model) {
		Controller.model = model;
		VisDataModel.newModelLoaded();
	}

	public static void connectModelToUI() {
		player.setTimeline(model.getTimeline());
		ui.setTitleFileName(model.getName());
		resetView();
	}

	/**
	 * Liefert den Player
	 * 
	 * @return
	 */
	public static Player getPlayer() {
		return player;
	}

}
