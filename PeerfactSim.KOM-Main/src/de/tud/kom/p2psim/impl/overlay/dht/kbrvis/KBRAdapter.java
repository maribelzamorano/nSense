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


package de.tud.kom.p2psim.impl.overlay.dht.kbrvis;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.dht.KBRLookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.KBRLookupReplyMsg;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.Translator.EdgeHandle;
import de.tud.kom.p2psim.impl.vis.util.MultiKeyMap;

/**
 * This Adapter may be used to visualize any overlay that complies to the
 * <code>KBR</code> interface.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KBRAdapter extends OverlayAdapter {

	/**
	 * The existing edges for neighborhood relations
	 */
	private final MultiKeyMap<NetID, EdgeHandle> neighborEdges = new MultiKeyMap<NetID, EdgeHandle>();

	public KBRAdapter() {
		addOverlayImpl(KBR.class);
		addOverlayImpl(ForwardMsg.class);
		addOverlayNodeMetric(KBRIDM.class);
	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode nd) {
		return null;
	}

	@Override
	public String getOverlayName() {
		return "KBR enabled DHT";
	}

	@Override
	public void handleLeavingHost(Host host) {
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode overlayNode) {
		KBR kbrNode = (KBR) overlayNode.getHost().getOverlay(KBR.class);

		attributes.put("OverlayID", kbrNode.getLocalOverlayContact()
				.getOverlayID().toString());
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode overlayNode) {
		/*
		 * Refresh the edges to direct neighbors of this node
		 */
		KBR kbrNode = (KBR) overlayNode.getHost().getOverlay(KBR.class);
		refreshDirectNeighbors(kbrNode);
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		/*
		 * Refresh the edges to direct neighbors of this node
		 */
		KBR kbrNode = (KBR) host.getOverlay(KBR.class);
		refreshDirectNeighbors(kbrNode);
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		/*
		 * Draw some colored edges when KBR specific messages are sent
		 */
		if (omsg instanceof ForwardMsg) {
			Message appMsg = omsg.getPayload();
			if (appMsg instanceof KBRLookupMsg) {
				KBRLookupMsg lookupMsg = (KBRLookupMsg) appMsg;
				this.flashEdge(lookupMsg.getSenderContact().getTransInfo()
						.getNetId(), to.getNetLayer().getNetID(), Color.red,
						"KBR-Lookup", appMsg.getClass());
			} else if (appMsg instanceof KBRLookupReplyMsg) {
				KBRLookupReplyMsg replyMsg = (KBRLookupReplyMsg) appMsg;
				this.flashEdge(replyMsg.getSenderContact().getTransInfo()
						.getNetId(), to.getNetLayer().getNetID(), Color.green,
						"KBR-Lookup-Reply", appMsg.getClass());
			}
		}

	}

	/**
	 * Refresh the edges drawn from this node to its neighbors
	 * 
	 * @param node
	 *            the node to refresh
	 */
	@SuppressWarnings("unchecked")
	private void refreshDirectNeighbors(KBR node) {

		// Get all current neighbors of the node
		List<OverlayContact> newNeighbors = node.neighborSet(Integer.MAX_VALUE);

		NetID from = node.getLocalOverlayContact().getTransInfo().getNetId();
		Set<NetID> neighborsToUse = new HashSet<NetID>();

		/*
		 * Add new edges
		 */
		for (OverlayContact olContact : newNeighbors) {
			NetID to = olContact.getTransInfo().getNetId();

			if (!neighborEdges.contains(from, to)) {
				// This is a new edge
				EdgeHandle newEdge = this.addEdge(from, to, Color.GRAY,
						"Neighbors");
				if (newEdge != null) {
					// The edge can be drawn
					neighborEdges.put(from, to, newEdge);
					neighborsToUse.add(to);
				}
			} else {
				// This is a already existing edge
				neighborsToUse.add(to);
			}
		}

		/*
		 * Remove edges that are not used anymore
		 */

		// Get edges to remove
		Set<EdgeHandle> toRemove = neighborEdges.removeComplementarySet(from,
				neighborsToUse);

		// Remove edges
		for (EdgeHandle edgeHandle : toRemove) {
			edgeHandle.remove();
		}

	}

}
