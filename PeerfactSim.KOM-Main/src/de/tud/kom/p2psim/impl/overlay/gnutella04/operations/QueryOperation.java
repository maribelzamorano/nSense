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
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class QueryOperation extends BaseOperation {

	private OverlayID exclude;

	private FilesharingKey key;

	public QueryOperation(GnutellaOverlayNode component, FilesharingKey key,
			OperationCallback<Object> callback) {
		super(component, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL,
				GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, 0, null, callback);
		this.exclude = component.getOverlayID();
		// inform node about new Query in order to get results
		component.registerQuery(this.getDescriptor(), key);
		this.key = key;
	}

	public QueryOperation(GnutellaOverlayNode component, int ttl, int hops,
			BigInteger descriptor, GnutellaOverlayID exclude,
			FilesharingKey key, OperationCallback<Object> callback) {
		super(component, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, ttl,
				hops, descriptor, callback);
		this.exclude = component.getOverlayID();
		this.key = key;
	}

	@Override
	protected void execute() {
		// if(this.getComponent().isActive()){
		if (ttl > 0) {
			for (OverlayContact<GnutellaOverlayID> contact : (List<OverlayContact<GnutellaOverlayID>>) this
					.getComponent().getRoutingTable().allContacts()) {
				// prevent ping from being sent back
				if (!this.exclude.equals(contact.getOverlayID())) {
					QueryMessage message = new QueryMessage(
							(GnutellaOverlayID) this.getComponent()
									.getOverlayID(), contact.getOverlayID(),
							this.ttl, this.hops, this.descriptor, this.key);
					this.getComponent().getTransLayer().send(message,
							contact.getTransInfo(),
							this.getComponent().getPort(), TransProtocol.UDP);
					// inform routing table about outgoing query
					((GnutellaOverlayRoutingTable) getComponent()
							.getRoutingTable()).outgoingQuery(contact, this
							.getDescriptor());
				}
				// }
			}
		}
	}
}
