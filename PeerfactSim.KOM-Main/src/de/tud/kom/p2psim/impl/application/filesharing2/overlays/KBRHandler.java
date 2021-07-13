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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.scenario.ConfigurationException;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages.HavingResourceMessage;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages.LookupResourceMessage;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages.PublishResourceMessage;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.KBR.messages.PublishSucceededMessage;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.KBRKademliaNode;

/**
 * Filesharing2 overlay handler wrapper for all KBR-enabled overlays. This
 * component was not used in my studies, so is not well debugged although it
 * seems to work pretty good.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRHandler extends StructuredHandler implements KBRListener {

	private final KBR overlay;

	private final Set<OverlayKey> keysStored = new HashSet<OverlayKey>();

	public KBRHandler(KBR overlay) {
		this.overlay = overlay;
		overlay.setKBRListener(this);
	}

	@Override
	public void join() {

		if (overlay instanceof DHTNode) {
			((DHTNode) overlay).join(Operations.EMPTY_CALLBACK);
			return;
		}
		if (overlay instanceof ChordNode) {
			((ChordNode) overlay).join(Operations.EMPTY_CALLBACK);
			return;
		}
		if (overlay instanceof KBRKademliaNode) {
			((KBRKademliaNode) overlay).connect();
			return;
		}
		throw new ConfigurationException(
				"Join: KBR node type was not recognized automatically. "
						+ "Implement it or join manually.");
	}

	@Override
	public void leave() {
		if (overlay instanceof ChordNode) {
			((ChordNode) overlay).leave(Operations.EMPTY_CALLBACK);
			return;
		}
		if (overlay instanceof KBRKademliaNode) {
			((KBRKademliaNode) overlay).disconnect();
			return;
		}
		throw new ConfigurationException(
				"Leave: KBR node type was not recognized automatically. "
						+ "Implement it or leave manually.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void lookupResource(int key) {
		OverlayKey key2lookup = overlay.getNewOverlayKey(key);
		LookupResourceMessage msg2forward = new LookupResourceMessage(
				key2lookup, overlay.getLocalOverlayContact());
		this.lookupStarted(overlay.getLocalOverlayContact(),
				msg2forward.getQueryUID());
		if (overlay.isRootOf(key2lookup))
			deliver(key2lookup, msg2forward);
		else
			overlay.route(key2lookup, msg2forward, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void publishSingleResource(int resourceKey) {
		OverlayKey key2publish = overlay.getNewOverlayKey(resourceKey);
		PublishResourceMessage msg2forward = new PublishResourceMessage(
				key2publish, overlay.getLocalOverlayContact());
		this.publishStarted(overlay.getLocalOverlayContact(), resourceKey,
				msg2forward.getQueryUID());
		if (overlay.isRootOf(key2publish))
			deliver(key2publish, msg2forward);
		else
			overlay.route(key2publish, msg2forward, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deliver(OverlayKey key, Message msg) {

		Message olmsg = msg;

		// System.out.println("Message " + msg + " delivered at " +
		// overlay.getLocalOverlayContact());

		if (olmsg instanceof LookupResourceMessage) {
			LookupResourceMessage lookupMsg = (LookupResourceMessage) olmsg;
			if (keysStored.contains(lookupMsg.getKeyLookedUp())) {
				sendQuerySuccess(lookupMsg.getKeyLookedUp(),
						lookupMsg.getInitiator(), lookupMsg.getQueryUID());
			} else {
				System.out.println("Key looked up is not stored.");
			}
		} else if (olmsg instanceof PublishResourceMessage) {
			PublishResourceMessage pubMsg = (PublishResourceMessage) olmsg;

			OverlayKey key2publish = pubMsg.getKey2publish();

			keysStored.add(key2publish);

			List<? extends OverlayContact> replNodes = overlay.replicaSet(
					pubMsg.getKey2publish(), this.getReplicaCount());

			// System.out.println("Received " + replNodes.size() +
			// "nodes for replicating " + key2publish);

			if (!pubMsg.hasReplicationFlag()) {
				// Send replicated key/values to all neighbors.
				for (OverlayContact contact : replNodes) {
					PublishResourceMessage repMsg = new PublishResourceMessage(
							key2publish, overlay.getLocalOverlayContact());
					repMsg.setReplicationFlag(true);
					overlay.route(null, repMsg, contact);
				}

				sendStoreSuccess(key2publish, pubMsg.getInitiator(),
						pubMsg.getQueryUID());
			}

		} else if (olmsg instanceof HavingResourceMessage) {
			HavingResourceMessage hvMsg = (HavingResourceMessage) olmsg;
			if (hvMsg.getRequestor().equals(overlay.getLocalOverlayContact())) {
				this.lookupSucceeded(overlay.getLocalOverlayContact(),
						hvMsg.getQueryUID(), 0);
				// TODO: Ranks aus OverlayKeys extrapolieren
			}

		} else if (olmsg instanceof PublishSucceededMessage) {
			PublishSucceededMessage succMsg = (PublishSucceededMessage) olmsg;
			if (succMsg.getRequestor().equals(overlay.getLocalOverlayContact())) {
				this.publishSucceeded(overlay.getLocalOverlayContact(),
						succMsg.getPublishResponsibleNode(), 0,
						succMsg.getQueryUID());
				// TODO: Ranks aus OverlayKeys extrapolieren
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void sendStoreSuccess(OverlayKey keyStored,
			OverlayContact contact, long queryUID) {
		Message msg2forward = new PublishSucceededMessage(queryUID, keyStored,
				contact, overlay.getLocalOverlayContact());
		if (overlay.getLocalOverlayContact().equals(contact))
			deliver(keyStored, msg2forward);
		else
			overlay.route(null, msg2forward, contact);
	}

	@SuppressWarnings("unchecked")
	protected void sendQuerySuccess(OverlayKey keyLookedUp,
			OverlayContact contact, long queryUID) {
		Message msg2forward = new HavingResourceMessage(queryUID, keyLookedUp,
				contact, overlay.getLocalOverlayContact());
		if (overlay.getLocalOverlayContact().equals(contact))
			deliver(keyLookedUp, msg2forward);
		else
			overlay.route(null, msg2forward, contact);
	}

	@Override
	public void forward(KBRForwardInformation information) {
		Message olmsg = information.getMessage();

		if (olmsg instanceof LookupResourceMessage) {
			LookupResourceMessage lookupMsg = (LookupResourceMessage) olmsg;
			this.lookupMadeHop(lookupMsg.getQueryUID(),
					overlay.getLocalOverlayContact());
		}

	}

	@Override
	public void update(OverlayContact contact, boolean joined) {
		// TODO Auto-generated method stub

	}

	public int getReplicaCount() {
		return 20; // TODO nur zum Test!
	}

}
