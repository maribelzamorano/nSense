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


package de.tud.kom.p2psim.impl.overlay.gnutella.common.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaAck;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaResources;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.SeqMessage;

/**
 * This operation transmits a node's resources to other nodes, e.g. if the Gnutella
 * overlay supports replication like in Gia or GNutella06v2. This
 * operation is acknowledged.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class UpdateDocumentsOperation<TContact extends GnutellaLikeOverlayContact> 
extends ReqRespOperation<TContact, AbstractGnutellaLikeNode<TContact, ?>, Object> {

	private ConnectionManager<?, TContact, ?, ?> mgr;

	public UpdateDocumentsOperation(AbstractGnutellaLikeNode<TContact, ?> component,
			ConnectionManager<?, TContact, ?, ?> mgr, TContact to,
			OperationCallback<Object> callback) {
		super(component, to, callback);
		this.mgr = mgr;
	}

	public void execute() {
		if (!getComponent().getResources().isEmpty())
			super.execute();
		else
			operationFinished(true);
		//if no resources are shared, no replication information needs to be transmitted.
	}
	
	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new GnutellaResources<TContact>(getComponent().getOwnContact(),
				getComponent().getResources());
	}

	@Override
	protected long getTimeout() {
		return this.getComponent().getConfig().getResponseTimeout();
	}

	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (response instanceof GnutellaAck) {
			operationFinished(true);
			return true;
		}
		return false;
	}

	@Override
	protected void timeoutOccured() {
		
		if (!mgr.foundDeadContact(this.getTo())) {
			this.scheduleImmediately(); // Retry
		}
		operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
