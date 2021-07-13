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


package de.tud.kom.p2psim.impl.vis.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsBase;
import de.tud.kom.p2psim.impl.vis.metrics.MetricsPack;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayGraph;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * Grundklasse für das Datenmodell.
 * 
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class VisDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2639836545144899408L;

	private Coords lowerBounds;

	private Coords upperBounds;

	public VisDataModel() {
		this("Unbenannt");
	}

	private String name;

	private final VisOverlayGraph g;

	private final EventTimeline tl;

	/**
	 * Alle dynamisch von z.B. OverlayAdaptern geladene Metriken. Nur zum
	 * Speichern und Laden.
	 */
	MetricsPack specialMetrics = null;

	ModelFilter filter = new ModelFilter();

	volatile boolean unsaved = true;

	static boolean muted = false;

	static ArrayList<ModelRefreshListener> listeners = new ArrayList<ModelRefreshListener>();

	public VisDataModel(String name) {
		g = new VisOverlayGraph();
		tl = new EventTimeline();
		this.name = name;

		this.upperBounds = new Coords(Float.MIN_NORMAL, Float.MIN_VALUE);
		this.lowerBounds = new Coords(Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public VisOverlayGraph getOverlayGraph() {
		return g;
	}

	public ModelFilter getFilter() {
		return filter;
	}

	/**
	 * Setzt die maximalen geographischen Koordinaten der Knoten.
	 * 
	 * @return
	 */
	public void setUpperBounds(Coords bounds) {
		this.upperBounds = bounds;
	}

	/**
	 * Gibt die Breite der minimalen geographischen Koordinaten der Knoten
	 * zurück.
	 * 
	 * @return
	 */
	public Coords getLowerBounds() {
		return this.lowerBounds;
	}

	/**
	 * Setzt die minimalen geographischen Koordinaten der Knoten.
	 * 
	 * @return
	 */
	public void setLowerBounds(Coords bounds) {
		this.lowerBounds = bounds;
	}

	/**
	 * Gibt die Breite der maximalen geographischen Koordinaten der Knoten
	 * zurück.
	 * 
	 * @return
	 */
	public Coords getUpperBounds() {
		return this.upperBounds;
	}

	/**
	 * Iteriert den Iterator it über das gesamte Modell, von "oben nach unten"
	 * 
	 * @param it
	 */
	public void iterate(ModelIterator it, MetricObject selectedObject) {
		g.iterate(it, selectedObject);
	}

	/**
	 * Iteriert den Iterator it über das gesamte Modell. von "unten nach oben"
	 * 
	 * @param it
	 */
	public void iterateBottomTop(ModelIterator it) {
		g.iterateBottomTop(it);
	}

	/**
	 * Setzt das Modell zurück (leeres Modell)
	 */
	public void reset() {
		g.reset();
		tl.reset();
	}

	public EventTimeline getTimeline() {
		return tl;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	public void saveTo(File file) throws IOException {
		this.specialMetrics = MetricsBase.getDynamicMetricsPack();
		specialMetrics.clearInstances();
		ObjectOutputStream objOut = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(file))));
		System.out.println("Aufzeichnung wird in Datei " + file.getName()
				+ " geschrieben...");
		objOut.writeObject(this);
		objOut.close();
		Controller.getModel().setUnsaved(false);
	}

	/*
	 * Statische Methoden: Die Timeline kann ausgetauscht werden als Objekt
	 * (open/save). was nicht ausgetauscht werden soll, statisch machen:
	 */

	public static VisDataModel fromFile(File file) throws IOException {
		try {
			ObjectInputStream objIn = new ObjectInputStream(
					new BufferedInputStream(new GZIPInputStream(
							new FileInputStream(file))));
			VisDataModel model = (VisDataModel) objIn.readObject();
			objIn.close();
			model.setUnsaved(false);
			MetricsBase.setDynamicMetricsPack(model.specialMetrics);
			return model;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addRefreshListener(ModelRefreshListener l) {
		listeners.add(l);
	}

	public static void needsRefresh() {
		if (!muted)
			for (ModelRefreshListener l : listeners)
				l.modelNeedsRefresh(Controller.getModel());
	}

	public static void newModelLoaded() {
		if (!muted)
			for (ModelRefreshListener l : listeners)
				l.newModelLoaded(Controller.getModel());
	}

	public static void simulationFinished() {
		if (!muted)
			for (ModelRefreshListener l : listeners)
				l.simulationFinished(Controller.getModel());
	}

	/**
	 * Stellt das Datenmodell "stumm", also die Listener werden nicht über neue
	 * Er- eignisse informiert. z.B. für den Gnuplot-Export.
	 * 
	 * @param mute
	 */
	public static void mute(boolean mute) {
		muted = mute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
