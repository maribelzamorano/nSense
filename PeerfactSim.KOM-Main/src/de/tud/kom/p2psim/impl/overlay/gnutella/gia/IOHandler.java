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


/**
 * 
 */
package de.tud.kom.p2psim.impl.overlay.gnutella.gia;

import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IResource;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IPongHandler;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaAck;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPing;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaResources;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaHandshake1;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaPongMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.operations.GiaConnectOperationY;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;

/**
 * Listens for incoming messages and dispatches them to the appropriate components.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class IOHandler implements TransMessageListener {

	private GiaConnectionManager mgr;
	private GiaNode owner;
	private IPongHandler<GiaOverlayContact, GiaPongMessage> pongHdlr;

	public IOHandler(GiaNode owner, GiaConnectionManager mgr, IPongHandler<GiaOverlayContact, GiaPongMessage> pongHdlr) {
		this.mgr =mgr;
		this.owner =owner;
		this.pongHdlr = pongHdlr;
	}
	
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		new IOOperation(owner, receivingEvent).scheduleImmediately();
	}
	
	@SuppressWarnings("unchecked")
	void handleIOEvent(TransMsgEvent receivingEvent) {
		Message receivedMessage = receivingEvent.getPayload();
		
		if (receivedMessage instanceof GiaHandshake1) {
			handleGiaHandshake1((GiaHandshake1)receivedMessage);
			
		} else if (receivedMessage instanceof GnutellaResources) {
			handleResUpdate((GnutellaResources) receivedMessage,
					receivingEvent);
			
		} else if (receivedMessage instanceof GnutellaPing) {
			handlePing((GnutellaPing) receivedMessage, receivingEvent);
			
		}
	}

	/**
	 * @param receivedMessage
	 */
	private void handleGiaHandshake1(GiaHandshake1 receivedMessage) {
		GiaOverlayContact sender = receivedMessage.getSender();
		int senderDegree = receivedMessage.getDegreeOfSender();
		
		mgr.seenContact(sender);
		
		new GiaConnectOperationY(owner, sender, Operations.getEmptyCallback(), mgr, senderDegree, 
				receivedMessage.getSeqNumber(), receivedMessage.isRequestingTryPeers()).scheduleImmediately();
		
	}
	
	private void handlePing(GnutellaPing<GiaOverlayContact> receivedMessage, TransMsgEvent e) {

		GiaOverlayContact requestingContact = receivedMessage
				.getSender();

		if (mgr.peerIsConnected(requestingContact)) {
			GiaPongMessage reply = pongHdlr.generatePongMessage(requestingContact, owner
					.getOwnContact());
			reply.setSeqNumber(receivedMessage.getSeqNumber());
			owner.getHost().getTransLayer().send(reply,
					requestingContact.getTransInfo(),
					requestingContact.getTransInfo().getPort(),
					TransProtocol.UDP);
		}
	}
	
	private void handleResUpdate(GnutellaResources<GiaOverlayContact> receivedMessage,
			TransMsgEvent receivingEvent) {
		GiaOverlayContact requestingContact = receivedMessage
				.getSender();

		if (mgr.peerIsConnected(requestingContact)) {

			GnutellaAck reply = new GnutellaAck();
			reply.setSeqNumber(receivedMessage.getSeqNumber());
			owner.getHost().getTransLayer().send(reply,
					requestingContact.getTransInfo(),
					requestingContact.getTransInfo().getPort(),
					TransProtocol.UDP);
			updateReplicatedResources(requestingContact, receivedMessage
					.getResources());
		}
	}
	
	private void updateReplicatedResources(GiaOverlayContact c,
			Set<IResource> resources) {
		GiaConnectionMetadata metadata = mgr.getMetadata(c);
		metadata.setReplicatedResources(resources);
	}
	
	class IOOperation extends AbstractOperation<GiaNode, Object> {

		TransMsgEvent receivingEvent;

		/**
		 * @param component
		 * @param callback
		 */
		protected IOOperation(GiaNode component, TransMsgEvent receivingEvent) {
			super(component, Operations.getEmptyCallback());
			this.receivingEvent = receivingEvent;
		}

		@Override
		protected void execute() {
			handleIOEvent(receivingEvent);
		}

		@Override
		public Object getResult() {
			return null;
		}
		
	}
	
}
