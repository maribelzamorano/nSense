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

import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.IGnutellaEventListener.FailCause;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IGnutellaConfig;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IManageableConnection;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaConnect;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaConnectReply;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.SeqMessage;

/**
 * Tries to build up a connection. Sends a connect message and
 * waits for a reply. This operation should only be triggered
 * by the connection manager.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class ConnectOperation<TContact extends GnutellaLikeOverlayContact, TConfig extends IGnutellaConfig> 
		extends ReqRespOperation<TContact, AbstractGnutellaLikeNode<TContact, TConfig>, Object> {

	private TContact to;

	IManageableConnection conn;

	private ConnectionManager<?, TContact, TConfig, ?> mgr;

	int connectSeqNr;

	/**
	 * Creates a new connect operation
	 * @param component: the main component of the node that starts the connection attempt.
	 * @param to: the overlay contact to which the connection shall be established.
	 * @param mgr: the connection manager of the initiating node.
	 * @param conn: the control interface of the connection that is maintained by this operation
	 * @param callback
	 */
	public ConnectOperation(AbstractGnutellaLikeNode<TContact, TConfig> component,
			TContact to, ConnectionManager<?, TContact, TConfig, ?> mgr,
			IManageableConnection conn, OperationCallback<Object> callback) {
		super(component, to, callback);
		this.to = to;
		this.conn = conn;
		this.mgr = mgr;
	}

	@Override
	protected void execute() {
		super.execute();
		getComponent().getLocalEventDispatcher().connectionStarted(
				getComponent().getOwnContact(), to, this.getSequenceNumber());
	}

	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new GnutellaConnect<TContact>(getComponent().getOwnContact(), getComponent()
				.hasLowConnectivity());
	}

	public TContact getTo() {
		return to;
	}

	@Override
	public Object getResult() {
		// No result for this operation
		return null;
	}

	void finished(boolean succeeded) {
		this.operationFinished(succeeded);
	}

	protected void addReceivedUltrapeers(List<TContact> contacts) {
		if (mgr != null
				&& mgr.getNumberOfContacts() < getComponent().getConfig()
						.getTryPeersAddLimit())
			mgr.seenContacts(contacts);
	}

	@Override
	protected long getTimeout() {
		return getComponent().getConfig().getConnectTimeout();
	}

	@Override
	protected void timeoutOccured() {
		conn.connectionTimeouted();
		getComponent().getLocalEventDispatcher().connectionFailed(
				getComponent().getOwnContact(), getTo(),
				this.getSequenceNumber(), FailCause.Timeout);
		finished(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean gotResponse(SeqMessage response) {

		if (response instanceof GnutellaConnectReply) {

			GnutellaConnectReply<TContact> reply = (GnutellaConnectReply) response;

			boolean succeeded = false;

			if (reply.isConnectionAccepted()) {
				conn.connectionSucceeded();
				getComponent().getLocalEventDispatcher().connectionSucceeded(
						getComponent().getOwnContact(), getTo(),
						this.getSequenceNumber());
				succeeded = true;
			} else {
				conn.connectionFailed();
				getComponent().getLocalEventDispatcher().connectionFailed(
						getComponent().getOwnContact(), getTo(),
						this.getSequenceNumber(), FailCause.Denied);
			}
			addReceivedUltrapeers(reply.getTryUltrapeers());

			finished(succeeded);
			return true;
		}
		return false;
	}

}
