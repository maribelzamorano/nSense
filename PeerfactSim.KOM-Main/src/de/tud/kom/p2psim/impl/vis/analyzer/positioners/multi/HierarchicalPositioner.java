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


package de.tud.kom.p2psim.impl.vis.analyzer.positioners.multi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.MultiPositioner;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.SchematicPositioner;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.Coords;

/**
 * A real hierarchical positioner. The first overlay adapter declared in the XML
 * config acts for the subnet, the second one acts for the super net.
 * 
 * <br>
 * <br>
 * <b>Usage example</b>:
 * 
 * <pre>
 * &lt;Analyzer class=&quot;de.tud.kom.p2psim.impl.util.vis.analyzer.VisAnalyzer&quot; messageEdges=&quot;false&quot;&gt;
 * 		&lt;MultiPositioner class=&quot;de.tud.kom.p2psim.impl.util.vis.analyzer.positioners.multi.HierarchicalPositioner&quot; scaleFactor=&quot;0.13f&quot;/&gt;
 *         	&lt;OverlayAdapter class=&quot;subnet adapter&quot;/&gt;
 *         	&lt;OverlayAdapter class=&quot;supernet adapter&quot;/&gt;
 * &lt;/Analyzer&gt;
 * </pre>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 24.11.2008
 * 
 */
public class HierarchicalPositioner extends MultiPositioner {

	/**
	 * Skalierungsfaktor der Subnetzgruppen, mit Standardwert.
	 */
	public float scaleFactor = 0.15f;

	SchematicPositioner superNetPositioner = null;

	/**
	 * Für jede Bootstrap-Gruppe den Positioner
	 */
	Map<Object, SchematicPositioner> subNetPositioners = new HashMap<Object, SchematicPositioner>();

	/**
	 * Die Hosts einer Bootstrap-Gruppe
	 */
	Map<Object, Set<Host>> bootstrapGroups = new HashMap<Object, Set<Host>>();

	/**
	 * Koordinaten einer Bootstrap-Gruppe
	 */
	Map<Object, Coords> bootstrapSubGroupCoords = new HashMap<Object, Coords>();

	/**
	 * Für jeden Host die Koordinaten
	 */
	Map<Host, Coords> hostCoords = new HashMap<Host, Coords>();

	@Override
	public Coords getSchematicHostPosition(Host host) {
		if (isSuperNetHost(host)) {
			// Found supernet node.
			Object bootstrapMan = getSubNetAdapter().getBootstrapManagerFor(
					getSubNetImpl(host));

			Coords superNetCoords = getSuperNetPositioner()
					.getSchematicHostPosition(host, getSuperNetImpl(host));
			// The coordinates of this supernet node inside the supernet
			Coords subNetCoords = getScaledSchematicHostPosition(
					getSubNetPositioner(bootstrapMan), host,
					getSubNetImpl(host));
			// The coordinates of this supernet node inside the subnet (every
			// supernet node is a subnet node, too).

			Coords subGroupCoords = new Coords(superNetCoords.x
					- subNetCoords.x, superNetCoords.y - subNetCoords.y);
			// The coordinates, that shall be the origin of the subnet group of
			// this supernet node.

			bootstrapSubGroupCoords.put(bootstrapMan, subGroupCoords);

			Set<Host> bootstrapGroup = bootstrapGroups.get(bootstrapMan);
			// Returns all nodes that are positionless until now, because their
			// supernet
			// node has not yet entered the network.

			if (bootstrapGroup != null) {
				for (Host host2Update : bootstrapGroup) {
					Coords childActualCoords = hostCoords.get(host2Update);

					if (childActualCoords != null) {

						Coords childNewCoords = getScaledSchematicHostPosition(
								getSubNetPositioner(bootstrapMan), host2Update,
								getSubNetImpl(host2Update));

						childActualCoords.x = subGroupCoords.x
								+ childNewCoords.x;
						childActualCoords.y = subGroupCoords.y
								+ childNewCoords.y;

						// Assigns new coordinates to all this nodes based on
						// the new information
						// of the bootstrap group available.
					}
				}
			}
			return superNetCoords;
		}

		if (isSubNetHost(host)) {
			Object bootstrapMan = getSubNetAdapter().getBootstrapManagerFor(
					getSubNetImpl(host));

			Coords subGroupCoords = bootstrapSubGroupCoords.get(bootstrapMan);

			if (subGroupCoords != null) {

				Coords childNewCoords = getScaledSchematicHostPosition(
						getSubNetPositioner(bootstrapMan), host,
						getSubNetImpl(host));

				return new Coords(subGroupCoords.x + childNewCoords.x,
						subGroupCoords.y + childNewCoords.y);
			} else {
				// A matching supernet node does not yet exist, the positioner
				// has to wait until it
				// enters the scenario. Thus, the reference is hold.
				Coords coords = new Coords(0.1f, 0.1f);
				hostCoords.put(host, coords);

				addToBootstrapGroups(host, bootstrapMan);

				return coords;
			}
		}

		return null; // The node is neither one of the supernet, nor of the
						// subnet.
	}

	/**
	 * Fügt den Host host zur Bootstrap-Gruppe hinzu, die durch bootstrapMan
	 * beschrieben wird.
	 * 
	 * @param host
	 * @param bootstrapMan
	 */
	private void addToBootstrapGroups(Host host, Object bootstrapMan) {
		Set<Host> group = bootstrapGroups.get(bootstrapMan);
		if (group != null) {
			group.add(host);
		} else {
			group = new HashSet<Host>();
			group.add(host);
			bootstrapGroups.put(bootstrapMan, group);
		}
	}

	/**
	 * Ob host ein Host ist, der eine Implementierung des Subnetzes hat.
	 * 
	 * @param host
	 * @return
	 */
	protected boolean isSubNetHost(Host host) {
		return isHostForAdapter(host, getSubNetAdapter());
	}

	/**
	 * Ob host ein Host ist, der eine Implementierung des Supernetzes hat
	 * 
	 * @param host
	 * @return
	 */
	protected boolean isSuperNetHost(Host host) {
		return isHostForAdapter(host, getSuperNetAdapter());
	}

	/**
	 * Die Overlay-Implementierung für das Subnetz.
	 * 
	 * @param host
	 * @return
	 */
	protected OverlayNode getSubNetImpl(Host host) {
		return getImplForAdapter(host, getSubNetAdapter());
	}

	/**
	 * Die Overlay-Implementierung für das Supernetz
	 * 
	 * @param host
	 * @return
	 */
	protected OverlayNode getSuperNetImpl(Host host) {
		return getImplForAdapter(host, getSuperNetAdapter());
	}

	/**
	 * Ob der Host vom Adapter adapter behandelt werden kann.
	 * 
	 * @param host
	 * @param adapter
	 * @return
	 */
	protected boolean isHostForAdapter(Host host, OverlayAdapter adapter) {
		Iterator<OverlayNode> it = host.getOverlays();
		while (it.hasNext()) {
			if (adapter.isDedicatedOverlayImplFor(it.next().getClass()))
				return true;
		}
		return false;
	}

	/**
	 * Die erste Implementierung, die von adapter behandelt werden kann, die
	 * host besitzt.
	 * 
	 * @param host
	 * @param adapter
	 * @return
	 */
	protected OverlayNode getImplForAdapter(Host host, OverlayAdapter adapter) {
		Iterator<OverlayNode> it = host.getOverlays();
		while (it.hasNext()) {
			OverlayNode node = it.next();
			if (adapter.isDedicatedOverlayImplFor(node.getClass()))
				return node;
		}
		return null;

	}

	/**
	 * Der Adapter des Subnetzes.
	 * 
	 * @return
	 */
	protected OverlayAdapter getSubNetAdapter() {
		return this.getAdapterAt(0);
	}

	/**
	 * Der Adapter des Supernetzes.
	 * 
	 * @return
	 */
	protected OverlayAdapter getSuperNetAdapter() {
		return this.getAdapterAt(1);
	}

	/**
	 * Der Positionierer des Supernetzes
	 * 
	 * @return
	 */
	protected SchematicPositioner getSuperNetPositioner() {
		if (superNetPositioner == null) {
			superNetPositioner = getSuperNetAdapter().getNewPositioner();
		}
		return superNetPositioner;
	}

	/**
	 * Der Positionierer des Subnetzes
	 * 
	 * @param bootstrapMan
	 * @return
	 */
	protected SchematicPositioner getSubNetPositioner(Object bootstrapMan) {
		SchematicPositioner loadedPos = subNetPositioners.get(bootstrapMan);
		if (loadedPos == null) {
			loadedPos = getSubNetAdapter().getNewPositioner();
			subNetPositioners.put(bootstrapMan, loadedPos);
		}
		return loadedPos;
	}

	/**
	 * Liefert die schematische Position, die pos für host und node liefern
	 * würde, zurück, um den diesem Positioner zugewiesenen Skalierungsfaktor
	 * skaliert.
	 * 
	 * @param pos
	 * @param host
	 * @param node
	 * @return
	 */
	protected Coords getScaledSchematicHostPosition(SchematicPositioner pos,
			Host host, OverlayNode node) {
		return pos.getSchematicHostPosition(host, node).scaleTo(scaleFactor);
	}

	/**
	 * Setzt den Skalierungsfaktor Der Skalierungsfaktor ist der Faktor, um den
	 * die Darstellungen Subnetzgruppen kleiner sind als die der
	 * Supernetzgruppe.
	 * 
	 * Wird von der XML-Config aufgerufen.
	 * 
	 * @param scaleFactor
	 */
	public void setScaleFactor(String scaleFactor) {
		try {
			this.scaleFactor = Float.valueOf(scaleFactor);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Scale factor is not a float!");
		}
	}

}
