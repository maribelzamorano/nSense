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
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaClose;

/**
 * Notifies the connected peer about a connection close by sending
 * a message to it. The operation is closing right after sending
 * the notification, a confirmation will NOT be sent.
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class ConnectCloseOperation<TContact extends GnutellaLikeOverlayContact> extends
		AbstractOperation<AbstractGnutellaLikeNode<TContact, ?>, Object> {

	private TContact to;

	private TContact forContact;

	/**
	 * Creates a new ConnectCloseOperation
	 * @param component: the main component of the node initiating this operation
	 * @param to: the receiver of the notification
	 * @param forContact: the contact for which the connection will be closed. May be null.
	 * @param callback: will be notified if the operation has been finished.
	 */
	public ConnectCloseOperation(AbstractGnutellaLikeNode<TContact, ?> component,
			TContact to, TContact forContact,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.to = to;
		this.forContact = forContact;
	}

	@Override
	protected void execute() {

		// Bye messages are asynchronous and unconfirmed

		TransLayer trans = this.getComponent().getHost().getTransLayer();

		AbstractGnutellaMessage msg = new GnutellaClose<TContact>(this.getComponent().getOwnContact(), forContact);

		msg.setSeqNumber(this.getComponent().getNewSequenceNumber());

		trans.send(msg, to.getTransInfo(), to.getTransInfo().getPort(),
				TransProtocol.UDP);

		this.operationFinished(true);

	}

	@Override
	public Object getResult() {
		return null;
	}

}
