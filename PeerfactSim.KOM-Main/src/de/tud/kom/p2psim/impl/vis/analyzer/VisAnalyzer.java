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


package de.tud.kom.p2psim.impl.vis.analyzer;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.network.gnp.topology.GeographicPosition;
import de.tud.kom.p2psim.impl.network.gnp.topology.GnpPosition;
import de.tud.kom.p2psim.impl.network.modular.st.positioning.GNPPositioning.GNPPosition;
import de.tud.kom.p2psim.impl.network.modular.st.positioning.TorusPositioning.TorusPosition;
import de.tud.kom.p2psim.impl.network.simple.SimpleEuclidianPoint;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;
import de.tud.kom.p2psim.impl.vis.analyzer.netPosTransformers.GeographicalPositionTransformer;
import de.tud.kom.p2psim.impl.vis.analyzer.netPosTransformers.GnpPositionTransformer;
import de.tud.kom.p2psim.impl.vis.analyzer.netPosTransformers.NewGnpPositionTransformer;
import de.tud.kom.p2psim.impl.vis.analyzer.netPosTransformers.SimpleEuclidianPointTransformer;
import de.tud.kom.p2psim.impl.vis.analyzer.netPosTransformers.TorusPositionTransformer;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.MultiPositioner;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.multi.TakeFirstPositioner;
import de.tud.kom.p2psim.impl.vis.util.Config;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.PositionInfo;

/**
 * Der Analyzer für die Visualisierungsoberfläche.
 * 
 * See also <a href=
 * "http://www.student.informatik.tu-darmstadt.de/~l_nobach/docs/howto-visualization.pdf"
 * >PeerfactSim.KOM Visualization HOWTO</a>
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @edit Leo Nobach
 * 
 * @version 05/06/2011
 */
public class VisAnalyzer implements OperationAnalyzer, TransAnalyzer,
		NetAnalyzer, ConnectivityAnalyzer {

	/**
	 * Translator der sich um das Weiterreichen von Informationen an die
	 * Visualisierung kümmert
	 */
	private Translator translator;

	/**
	 * Liste der momentan vorhandenen Hosts im Szenario
	 */
	private final Map<NetID, Host> hosts = new HashMap<NetID, Host>();

	/**
	 * Zwischenspeicher um Sender einer AbstractTransMessage zu bestimmen
	 */
	private final HashMap<AbstractTransMessage, NetID> firstSender = new HashMap<AbstractTransMessage, NetID>();

	/**
	 * Zwischenspeicher um endgültigen Empfänger einer AbstractTransMessage zu
	 * bestimmen
	 */
	private final HashMap<AbstractTransMessage, NetID> lastReceiver = new HashMap<AbstractTransMessage, NetID>();

	/**
	 * Flag read from the config to decide if a fixed bound according to the
	 * dimension of a image should be used or if the bound is determined
	 * dynamical.
	 */
	private final boolean useFixedBound = Boolean.parseBoolean(Config.getValue(
			"UI/BackgroundImageEnabled", "false"));

	// -----------------------VisAnalyzer2---------------------------

	protected List<OverlayAdapter> loadedOLAdapters = new LinkedList<OverlayAdapter>();

	boolean messageEdges = false;

	private MultiPositioner rootPositioner = null;

	// --------------------------------------------------------------

	/**
	 * Gibt die Instanz des verwendeten Translators zurück
	 */
	public Translator getTranslator() {
		return translator;
	}

	private boolean hostIsNew(Host host) {
		return host != null && !hosts.values().contains(host);
	}

	private void checkHost(Host host) {
		if (hostIsNew(host)) {
			hosts.put(host.getNetLayer().getNetID(), host);
			foundNewHost(host);
		}
	}

	public void checkHost(NetID id) {
		if (!hosts.containsKey(id)) {
			Host newHost = GlobalOracle.getHostForNetID(id);
			if (newHost != null) {
				hosts.put(id, newHost);
				foundNewHost(newHost);
			}
		}
	}

	@Override
	public void netMsgSend(NetMessage message, NetID id) {
		checkHost(id);

		AbstractTransMessage tmsg;
		Message payload = message.getPayload();
		if (payload instanceof AbstractTransMessage) {
			tmsg = (AbstractTransMessage) payload;

			if (!lastReceiver.containsKey(tmsg)) {
				firstSender.put(tmsg, message.getSender());
				lastReceiver.put(tmsg, message.getReceiver());
			} else {
				lastReceiver.remove(tmsg);
				lastReceiver.put(tmsg, message.getReceiver());
			}
		}
	}

	@Override
	public void netMsgDrop(NetMessage message, NetID id) {
		// Nothing to do
	}

	@Override
	public void netMsgReceive(NetMessage message, NetID id) {
		// Nothing to do
	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		overlayMsgOccured(msg.getPayload(), firstSender.get(msg),
				lastReceiver.get(msg));

		// Hash-Map aufräumen
		firstSender.remove(msg);
		lastReceiver.remove(msg);
	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		// Nothing to do
	}

	@Override
	public void start() {
		translator = new Translator();
		getTranslator().setUpperBoundForCoordinates(1.0f, 1.0f);
		preparePositioners();
	}

	@Override
	public void stop(Writer output) {
		Translator.notifyFinished();
	}

	@Override
	public void operationFinished(Operation<?> op) {

		handleOperation(op.getComponent().getHost(), op, true);

	}

	@Override
	public void operationInitiated(Operation<?> op) {
		Host host = op.getComponent().getHost();
		checkHost(host);

		handleOperation(host, op, false);
	}

	/**
	 * Lässt eine Operation von den Overlay-Adaptern behandeln
	 * 
	 * @param host
	 * @param op
	 */
	private void handleOperation(Host host, Operation<?> op, boolean finished) {
		checkHost(host);

		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode> it = host.getOverlays();

			while (it.hasNext()) {
				it.next();
				if (adapter.isDedicatedOverlayImplFor(op.getComponent()
						.getClass())) {
					adapter.handleOperation(host, op, finished);
				}
			}
		}
	}

	@Override
	public void offlineEvent(Host host) {
		foundLeavingHost(host);
		hosts.remove(host.getNetLayer().getNetID());
	}

	@Override
	public void onlineEvent(Host host) {
		checkHost(host);
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein neuer Host im Szenario entdeckt
	 * wurde
	 * 
	 * @param host
	 */
	public void foundNewHost(Host host) {

		// NetPosition des Hosts ermitteln
		NetPosition netPos = host.getProperties().getNetPosition();

		Coords schem_coords = rootPositioner.getSchematicHostPosition(host);
		// System.out.println("Schematische Koordinaten für " + host + ": "+
		// schem_coords);

		// Attribute setzen
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();

		// Alle Overlay-Klassennamen eintragen, die der Host besitzt
		Iterator<OverlayNode> iter = host.getOverlays();
		while (iter.hasNext())
			addOverlayToAttributes(attributes, iter.next().getClass()
					.getSimpleName(), "overlay_raw");

		// Jeder verantwortliche Overlay-Adapter prüft VOR dem Erstellen des
		// Knotens
		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode> it = host.getOverlays();
			while (it.hasNext()) {
				OverlayNode overlayNode = it.next();

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {

					addOverlayToAttributes(attributes,
							adapter.getOverlayName(), "overlay");
					adapter.handleNewHost(attributes, host, overlayNode);
					// -> Der erste OverlayAdapter, der Zuweisung einer
					// schematischen Position zu einem Knoten unterstützt,
					// weist die Position dem Knoten zu.
				}
			}
		}

		PositionInfo pos = new PositionInfo(transformPosition(netPos),
				schem_coords);

		// Distributor ueber neuen Knoten benachrichtigen
		getTranslator().overlayNodeAdded(host.getNetLayer().getNetID(),
				host.getProperties().getGroupID(), pos, attributes);

		// Jeder verantwortliche Overlay-Adapter prüft NACH dem Erstellen des
		// Knotens noch einmal

		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode> it = host.getOverlays();

			while (it.hasNext()) {
				OverlayNode overlayNode = it.next();

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {
					adapter.handleNewHostAfter(host, overlayNode);
				}
			}
		}
	}

	/**
	 * Fügt ein weiteres Overlay mit Namen name in attributes ein.
	 * 
	 * @param attributes
	 * @param name
	 */
	@SuppressWarnings(value = { "unchecked" })
	// keine Möglichkeit bisher das zu umgehen.
	public void addOverlayToAttributes(Map<String, Serializable> attributes,
			String name, String key) {
		List<Serializable> overlays = (List<Serializable>) attributes.get(key);

		if (overlays == null) {
			overlays = new LinkedList<Serializable>();
			attributes.put(key, (Serializable) overlays);
		}

		overlays.add(name);
	}

	/**
	 * Diese Methode wird aufgerufen, wenn entdeckt wird, dass ein Host das
	 * Szenario verlässt
	 * 
	 * @param host
	 */
	public void foundLeavingHost(Host host) {
		// Jeder verantwortliche Overlay-Adapter prüft vor dem Verschwinden
		// eines Knotens alles.

		Iterator<OverlayNode> it = host.getOverlays();
		while (it.hasNext()) {
			OverlayNode overlayNode = it.next();

			for (OverlayAdapter adapter : loadedOLAdapters) {

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {
					adapter.handleLeavingHost(host);
				}
			}
		}

		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	/**
	 * Diese Methode wird aufgerufen, wenn eine Overlay-Message gesendet wird,
	 * bevor sie visualisiert wird.
	 * 
	 * @param omsg
	 * @param from
	 * @param to
	 */
	public void overlayMsgOccured(Message omsg, NetID from, NetID to) {
		// Jeder verantwortliche Overlay-Adapter prüft vor dem Verschwinden
		// eines Knotens alles.

		for (OverlayAdapter adapter : loadedOLAdapters) {

			if (adapter.isDedicatedOverlayImplFor(omsg.getClass())) {

				Host fromHost = hosts.get(from);
				Host toHost = hosts.get(to);

				if (fromHost == null) {
					System.err
							.println("Warnung: netMsgSend: Passender Host zu NetID "
									+ from + " ist null.");
				}
				if (toHost == null) {
					System.err
							.println("Warnung: netMsgSend: Passender Host zu NetID "
									+ to + " ist null.");
				}

				adapter.handleOverlayMsg(omsg, fromHost, from, toHost, to);
			}
		}

		// Eine Flash-Overlay-Kante wird gezeichnet, um den Versand einer
		// Message anzuzeigen.

		if (messageEdges) {
			Map<String, Serializable> attributes = new HashMap<String, Serializable>();
			attributes.put("type", omsg.getClass().getSimpleName());
			attributes.put("msg_class", omsg.getClass().getSimpleName());
			translator.overlayEdgeFlash(from, to, Color.RED, attributes);
		}

		// An Distributor melden dass Message verschickt wurde
		getTranslator()
				.overlayMessageSent(from, to, omsg.getClass().toString());
	}

	/**
	 * Wandelt eine gegebene NetPosition durch Unterscheidung der konkreten
	 * Implementierung in Coords um, die bei der Visualisierung zum
	 * Positionieren der Knoten benötigt werden..
	 * 
	 * @param netPos
	 * @return die umgewandelte NetPosition
	 */
	public Coords transformPosition(NetPosition netPos) {

		Coords coords = null;

		if (netPos != null) {

			// Is it a SimpleEuclidianPoint?
			if (netPos instanceof SimpleEuclidianPoint) {
				coords = new SimpleEuclidianPointTransformer()
						.transform((SimpleEuclidianPoint) netPos);

				// Is it a GnpPosition?
			} else if (netPos instanceof GnpPosition) {
				coords = new GnpPositionTransformer()
						.transform((GnpPosition) netPos);

				// Is it a GeographicPosition?
			} else if (netPos instanceof GeographicPosition) {
				coords = new GeographicalPositionTransformer()
						.transform((GeographicPosition) netPos);

				// Is it a GNPPosition (Modular Net Layer)?
			} else if (netPos instanceof GNPPosition) {
				coords = new NewGnpPositionTransformer()
						.transform((GNPPosition) netPos);

				// Is it a TorusPosition (Modular Net Layer)?
			} else if (netPos instanceof TorusPosition) {
				coords = new TorusPositionTransformer()
						.transform((TorusPosition) netPos);

				// Is the type not supported?
			} else {
				coords = new Coords(0, 0);
				System.err
						.println(this.getClass().getName()
								+ " - There is no transformer for the given NetPosition-type.");
			}

			// Passe die obere Bound für die Visualisierung an
			Coords currentUpperBound = getTranslator()
					.getUpperBoundForCoordinates();
			float currentMaxX = currentUpperBound.x;
			float currentMaxY = currentUpperBound.y;

			// Ist eine der Koordinaten größer als der momentane Bound, setze
			// den Bound neu
			if (coords.x > currentMaxX || coords.y > currentMaxY)
				getTranslator().setUpperBoundForCoordinates(
						Math.max(coords.x, currentMaxX),
						Math.max(coords.y, currentMaxY));

			// Passe die untere Bound für die Visualisierung an
			Coords currentLowerBound = getTranslator()
					.getLowerBoundForCoordinates();
			float currentMinX = currentLowerBound.x;
			float currentMinY = currentLowerBound.y;

			// Set bound
			if (useFixedBound) {
				// Bound is fixed

				// Set lower bound to 0,0
				getTranslator().setLowerBoundForCoordinates(0, 0);

				// Get last used background image path
				String lastPath = Config.getValue("UI/LastBackgroundImage", "");

				try {
					// Read the image
					Image lastImage = ImageIO.read(new File(lastPath));

					// Set upper bound to the image dimensions
					getTranslator()
							.setUpperBoundForCoordinates(
									lastImage.getWidth(null),
									lastImage.getHeight(null));

				} catch (IOException e) {
					// If the image could not be read, set the bound to a
					// standard value
					getTranslator().setUpperBoundForCoordinates(1250, 625);

					System.out
							.println("Could not read dimensions of given background image.");
				}

			} else {
				// Ist eine der Koordinaten kleiner als der momentane Bound,
				// setze den Bound neu
				if (coords.x < currentMinX || coords.y < currentMinY)
					getTranslator().setLowerBoundForCoordinates(
							Math.min(coords.x, currentMinX),
							Math.min(coords.y, currentMinY));

			}

		} else {
			System.err
					.println(this.getClass().getName()
							+ " - Ein gefundener Host besitzt keine NetPosition und kann daher nicht visualisiert werden.");
		}
		return coords;

	}

	/**
	 * Bereitet den Positioner vor.
	 */
	protected void preparePositioners() {
		if (rootPositioner == null) {
			rootPositioner = new TakeFirstPositioner();
		}
		rootPositioner.setAdapters(loadedOLAdapters);
	}

	/**
	 * Setzbar durch XML-Config: Setzt einen Overlay-Adapter ein, die für die
	 * Analyse der Ausgaben verwendet werden sollen. Es können mehrere gesetzt
	 * werden.
	 * 
	 * Werte bitte als vollständigen Klassennamen übergeben
	 * 
	 * @param overlayClassPaths
	 */
	public void setOverlayAdapter(OverlayAdapter adapter) {
		adapter.setParentAnalyzer(this);
		loadedOLAdapters.add(adapter);
	}

	public void setMultiPositioner(MultiPositioner pos) {
		this.rootPositioner = pos;
	}

	/**
	 * Setzbar durch XML-Config: Zeigt Messages als aufblitzende Verbindungen
	 * an. Zu empfehlen, wenn es keinen brauchbaren Overlay-Adapter für das
	 * Szenario gibt.
	 */
	public void setMessageEdges(boolean messageEdges) {
		this.messageEdges = messageEdges;
	}
}
