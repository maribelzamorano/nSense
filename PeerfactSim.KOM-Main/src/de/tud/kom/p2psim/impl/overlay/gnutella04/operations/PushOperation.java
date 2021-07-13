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


package de.tud.kom.p2psim.impl.overlay.gnutella04.operations;

import java.math.BigInteger;
import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaConfiguration;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PushOperation extends BaseOperation {

	private GnutellaOverlayNode node;

	private OverlayContact<GnutellaOverlayID> pushInitiator;

	private GnutellaOverlayID pushTarget = null;

	private FilesharingKey key;

	public PushOperation(GnutellaOverlayNode node, BigInteger descriptor,
			FilesharingKey key, OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL,
				GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, 0, descriptor,
				callback);
		this.node = node;
		this.pushInitiator = new GnutellaOverlayContact(
				(GnutellaOverlayID) node.getOverlayID(), node.getTransLayer()
						.getLocalTransInfo(node.getPort()));
		this.key = key;
	}

	public PushOperation(GnutellaOverlayNode node, int ttl, int hops,
			BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> pushInitiator,
			GnutellaOverlayID pushTarget, FilesharingKey key,
			OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, ttl, hops,
				descriptor, callback);
		this.node = node;
		this.pushInitiator = pushInitiator;
		this.pushTarget = pushTarget;
		this.key = key;
	}

	@Override
	protected void execute() {
		if (pushTarget == null) {
			List<OverlayContact<GnutellaOverlayID>> queryResults = node
					.getQueryResults(descriptor);
			if (queryResults.isEmpty()) {
				this.operationFinished(false);
				return;
			} else {
				this.pushTarget = queryResults.get(0).getOverlayID();
			}
		}
		// TODO Push Operation
		/*
		 * OverlayContact<GnutellaOverlayID> contactReceiver =
		 * ((GnutellaOverlayRoutingTable)
		 * getComponent().getRoutingTable()).outgoingPush(this.pushInitiator,
		 * this.descriptor); PushMessage message = new
		 * PushMessage((GnutellaOverlayID) this.getComponent().getOverlayID(),
		 * contactReceiver.getOverlayID(), this.ttl, this.hops, this.descriptor,
		 * this.pushInitiator, this.pushTarget, this.key);
		 * this.getComponent().getTransLayer().send(message,
		 * contactReceiver.getTransInfo(), this.getComponent().getPort(),
		 * TransProtocol.UDP); this.operationFinished(true);
		 */
	}

}
