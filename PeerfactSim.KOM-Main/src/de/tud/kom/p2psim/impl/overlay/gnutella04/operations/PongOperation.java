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

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaConfiguration;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayRoutingTable;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PongMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PongOperation extends BaseOperation {

	private OverlayContact<GnutellaOverlayID> contact;

	public PongOperation(GnutellaOverlayNode node, BigInteger descriptor,
			OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_PING_MAX_TTL,
				GnutellaConfiguration.GNUTELLA_PING_MAX_TTL, 0, descriptor,
				callback);
		this.contact = new GnutellaOverlayContact((GnutellaOverlayID) node
				.getOverlayID(), node.getTransLayer().getLocalTransInfo(
				node.getPort()));
	}

	public PongOperation(GnutellaOverlayNode node, int ttl, int hops,
			BigInteger descriptor, OverlayContact<GnutellaOverlayID> contact,
			OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_PING_MAX_TTL, ttl, hops,
				descriptor, callback);
		this.contact = contact;
	}

	public OverlayContact<GnutellaOverlayID> getContact() {
		return contact;
	}

	@Override
	protected void execute() {
		OverlayContact<GnutellaOverlayID> contactReceiver = ((GnutellaOverlayRoutingTable) getComponent()
				.getRoutingTable()).outgoingPong(this.descriptor);
		if (contactReceiver != null) {
			PongMessage message = new PongMessage((GnutellaOverlayID) this
					.getComponent().getOverlayID(), contactReceiver
					.getOverlayID(), this.ttl, this.hops, this.descriptor,
					this.contact);
			this.getComponent().getTransLayer().send(message,
					contactReceiver.getTransInfo(),
					this.getComponent().getPort(), TransProtocol.UDP);
		}
	}

}
