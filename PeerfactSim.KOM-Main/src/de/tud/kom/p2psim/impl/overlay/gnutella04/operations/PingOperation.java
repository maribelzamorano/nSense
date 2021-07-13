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
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaConfiguration;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayRoutingTable;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PingMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PingOperation extends BaseOperation {

	private OverlayID exclude;

	private OverlayContact<GnutellaOverlayID> singleContact = null;

	public PingOperation(GnutellaOverlayNode component,
			OperationCallback<Object> callback) {
		super(component, GnutellaConfiguration.GNUTELLA_PING_MAX_TTL,
				GnutellaConfiguration.GNUTELLA_PING_MAX_TTL, 0, null, callback);
		this.exclude = component.getOverlayID();
		((GnutellaOverlayRoutingTable) component.getRoutingTable())
				.initiatedPing((GnutellaOverlayID) component.getOverlayID(),
						this.getDescriptor());
	}

	public PingOperation(GnutellaOverlayNode component,
			OverlayContact<GnutellaOverlayID> singleContact,
			OperationCallback<Object> callback) {
		super(component, 1, GnutellaConfiguration.GNUTELLA_PING_MAX_TTL, 0,
				null, callback);
		this.singleContact = singleContact;
		((GnutellaOverlayRoutingTable) component.getRoutingTable())
				.initiatedPing((GnutellaOverlayID) component.getOverlayID(),
						this.getDescriptor());
	}

	public PingOperation(GnutellaOverlayNode component, int ttl, int hops,
			BigInteger descriptor, GnutellaOverlayID exclude,
			OperationCallback<Object> callback) {
		super(component, GnutellaConfiguration.GNUTELLA_PING_MAX_TTL, ttl,
				hops, descriptor, callback);
		this.exclude = exclude;
	}

	@Override
	protected void execute() {
		// if (this.getComponent().isActive()){
		if (ttl > 0) {
			if (singleContact == null) {
				for (OverlayContact<GnutellaOverlayID> contact : ((List<OverlayContact<GnutellaOverlayID>>) this
						.getComponent().getRoutingTable().allContacts())) {
					// prevent ping from being sent back
					if (!this.exclude.equals(contact.getOverlayID())) {
						pingContact(contact);
					}
				}
			} else {
				pingContact(singleContact);
			}
			// }
		}
	}

	private void pingContact(OverlayContact<GnutellaOverlayID> contact) {
		PingMessage pingMessage = new PingMessage((GnutellaOverlayID) this
				.getComponent().getOverlayID(), contact.getOverlayID(),
				this.ttl, this.hops, this.descriptor);
		this.getComponent().getTransLayer().send(pingMessage,
				contact.getTransInfo(), this.getComponent().getPort(),
				TransProtocol.UDP);
		// inform routing table about outgoing ping
		((GnutellaOverlayRoutingTable) getComponent().getRoutingTable())
				.outgoingPing(contact, this.getDescriptor());
	}

}
