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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.ultrapeer;

import java.util.List;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IResource;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.QueryHit;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IPongHandler;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaAck;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaClose;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaConnect;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaConnectReply;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPing;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPong;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaResources;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.Gnutella06ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.messages.Gnutella06LeafQueryRequest;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.messages.Gnutella06LeafQueryResponse;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.messages.Gnutella06Query;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;

/**
 * Receives the node's overlay messages, replies directly or 
 * forwards it to the appropriate components. Only listens to
 * asynchronous messages received, does not receive RPC replies,
 * this is done by the components itself (when they have made a request).
 * 
 * Implementation for ultrapeers.
 * 
 * @author Leo Nobach  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class IOHandler implements TransMessageListener {

	//private final static Logger log = SimLogger.getLogger(IOHandler.class);

	Ultrapeer owner;

	private Gnutella06ConnectionManager<Object> upMgr;

	private IPongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> pongHdlr;

	private Gnutella06ConnectionManager<LeafInfo> leafMgr;

	private QueryHandler qHndlr;

	public IOHandler(Gnutella06ConnectionManager<Object> upMgr,
			Gnutella06ConnectionManager<LeafInfo> leafMgr, QueryHandler qHndlr,
			Ultrapeer owner, IPongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> pongHdlr) {
		this.upMgr = upMgr;
		this.leafMgr = leafMgr;
		this.owner = owner;
		this.pongHdlr = pongHdlr;
		this.qHndlr = qHndlr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message receivedMessage = receivingEvent.getPayload();

		if (receivedMessage instanceof GnutellaConnect) {
			handleConnect((GnutellaConnect) receivedMessage, receivingEvent);
			return;
		} else if (receivedMessage instanceof GnutellaPing) {
			handlePing((GnutellaPing) receivedMessage, receivingEvent);
			return;
		} else if (receivedMessage instanceof GnutellaClose) {
			handleClose((GnutellaClose) receivedMessage, receivingEvent);
			return;
		} else if (receivedMessage instanceof GnutellaResources) {
			handleResUpdate((GnutellaResources) receivedMessage,
					receivingEvent);
			return;
		} else if (receivedMessage instanceof Gnutella06LeafQueryRequest) {
			handleLeafQuery((Gnutella06LeafQueryRequest) receivedMessage,
					receivingEvent);
			return;
		} else if (receivedMessage instanceof Gnutella06Query) {
			handleUPQuery((Gnutella06Query) receivedMessage, receivingEvent);
			return;
		}

		// log.debug("Incompatible message received: " +
		// receivedMessage.getClass().getSimpleName());
	}

	private void handleUPQuery(Gnutella06Query receivedMessage,
			TransMsgEvent receivingEvent) {
		qHndlr.foreignUPQueryAttempt(receivedMessage);
	}

	private void handleLeafQuery(Gnutella06LeafQueryRequest receivedMessage,
			TransMsgEvent receivingEvent) {
		if (leafMgr.peerIsConnected(receivedMessage.getRequester())) {
			int queryUID = receivedMessage.getQuery().getQueryUID();
			int seqNo = receivedMessage.getSeqNumber();
			owner.getLocalEventDispatcher().queryMadeHop(queryUID,
					owner.getOwnContact());
			new DynamicQueryOperation(receivedMessage.getQuery(), receivedMessage.getHitsWanted(), qHndlr,
					owner, leafMgr, upMgr, this.new LeafQueryCallback(
							receivedMessage.getRequester(), queryUID, seqNo))
					.scheduleImmediately();
		}
	}

	class LeafQueryCallback implements OperationCallback<List<QueryHit>> {

		private Gnutella06OverlayContact qInvoker;

		private int seqNo;

		private int queryUID;

		LeafQueryCallback(Gnutella06OverlayContact qInvoker, int queryUID,
				int seqNo) {
			this.qInvoker = qInvoker;
			this.queryUID = queryUID;
			this.seqNo = seqNo;
		}

		@Override
		public void calledOperationFailed(Operation<List<QueryHit>> op) {
			sendReply(new Gnutella06LeafQueryResponse(op.getResult(), queryUID));
		}

		@Override
		public void calledOperationSucceeded(Operation<List<QueryHit>> op) {
			sendReply(new Gnutella06LeafQueryResponse(op.getResult(), queryUID));
		}

		void sendReply(AbstractGnutellaMessage reply) {
			reply.setSeqNumber(seqNo);
			owner.getHost().getTransLayer().send(reply,
					qInvoker.getTransInfo(), qInvoker.getTransInfo().getPort(),
					TransProtocol.UDP);
		}

	}

	private void handleResUpdate(GnutellaResources<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {
		Gnutella06OverlayContact requestingContact = receivedMessage
				.getSender();

		if (leafMgr.peerIsConnected(requestingContact)) {

			GnutellaAck reply = new GnutellaAck();
			reply.setSeqNumber(receivedMessage.getSeqNumber());
			owner.getHost().getTransLayer().send(reply,
					requestingContact.getTransInfo(),
					requestingContact.getTransInfo().getPort(),
					TransProtocol.UDP);
			updateLeafResources(requestingContact, receivedMessage
					.getResources());
		}
	}

	private void updateLeafResources(Gnutella06OverlayContact c,
			Set<IResource> resources) {
		LeafInfo metadata = leafMgr.getMetadata(c);
		metadata.setLeafResources(resources);
	}

	private void handleClose(GnutellaClose<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {
		upMgr.foreignCloseAttempt(receivedMessage.getSndr());

		Gnutella06OverlayContact c = receivedMessage.getCausedContact();
		if (c != null)
			upMgr.seenContact(c);
	}

	private void handleConnect(GnutellaConnect<Gnutella06OverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {

		Gnutella06OverlayContact requestingContact = receivedMessage
				.getSenderInfo();

		boolean connectionAccepted;

		if (requestingContact.isUltrapeer())
			connectionAccepted = upMgr
					.foreignConnectionAttempt(requestingContact);
		else
			connectionAccepted = leafMgr
					.foreignConnectionAttempt(requestingContact);

		GnutellaConnectReply reply = new GnutellaConnectReply<Gnutella06OverlayContact>(upMgr
				.getSomeConnectedPeers(owner.getConfig().getTryPeersSize()),
				connectionAccepted);
		reply.setSeqNumber(receivedMessage.getSeqNumber());
		owner.getHost().getTransLayer().send(reply,
				requestingContact.getTransInfo(),
				requestingContact.getTransInfo().getPort(), TransProtocol.UDP);
	}

	private void handlePing(GnutellaPing<Gnutella06OverlayContact> receivedMessage, TransMsgEvent e) {

		Gnutella06OverlayContact requestingContact = receivedMessage
				.getSender();

		if (upMgr.peerIsConnected(requestingContact)
				|| leafMgr.peerIsConnected(requestingContact)) {

			GnutellaPong<Gnutella06OverlayContact> reply = pongHdlr.generatePongMessage(requestingContact, owner
					.getOwnContact());
			reply.setSeqNumber(receivedMessage.getSeqNumber());
			owner.getHost().getTransLayer().send(reply,
					requestingContact.getTransInfo(),
					requestingContact.getTransInfo().getPort(),
					TransProtocol.UDP);
		}
	}

}
