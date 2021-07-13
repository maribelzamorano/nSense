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
package de.tud.kom.p2psim.impl.overlay.gnutella.gia.operations;

import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.IManageableConnection;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.SeqMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.operations.ConnectCloseOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.operations.ReqRespOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.ConnectAcceptanceDecider;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaConnectionMetadata;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.GiaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.ConnectAcceptanceDecider.Decision;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaHandshake2;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaHandshake3;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaMessage;

/**
 * Connection operation managing the three-way handshake on the requestee's (i.e. Y) side.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GiaConnectOperationY extends ReqRespOperation<GiaOverlayContact, GiaNode, Object> {

	private GiaConnectionManager mgr;
	private int toDegree;
	
	ConnectAcceptanceDecider decider;
	IManageableConnection conn;
	private int requestSeqNo;
	private boolean requestingTryPeers;

	/**
	 * @param component
	 * @param to
	 * @param callback
	 * @param requestSeqNo 
	 */
	public GiaConnectOperationY(GiaNode component, GiaOverlayContact to,
			OperationCallback<Object> callback, GiaConnectionManager mgr, int toDegree, int requestSeqNo, boolean requestingTryPeers) {
		super(component, to, callback);
		this.mgr = mgr;
		this.toDegree = toDegree;
		this.requestSeqNo = requestSeqNo;
		this.requestingTryPeers=requestingTryPeers;
	}
	
	public void execute() {
		
		if (mgr.knowsContact(getTo())) {
			//Deadlock avoidance
			answerWithDenyMessage();
			this.operationFinished(false);
			return;
		}
		
		decider = new ConnectAcceptanceDecider(mgr, getTo(), toDegree, getComponent().getConfig());
		
		if (decider.getDecision() != Decision.Reject) {
			if (decider.getDecision() == Decision.Accept)
				conn = mgr.reserveForeignConnection(getTo());	//Reserves the connection slot.
			super.execute();	//Initiates the request/response.
			return;
		} else {
			answerWithDenyMessage();
			this.operationFinished(false);
		}
	}
	
	void answerWithDenyMessage() {
		GiaMessage denyMsg = new GiaHandshake2(false, getComponent().getOwnContact(), getTo(), 
				mgr.getDegree(), generateTryPeersList(), getComponent().getTokenAllocationRateFor(getTo()));
		denyMsg.setSeqNumber(requestSeqNo);
		sendMessage(denyMsg, getTo());
	}
	
	List<GiaOverlayContact> generateTryPeersList() {
		if(requestingTryPeers)
			return mgr.getSomeArbitraryPeers(getComponent().getConfig().getTryPeersSize());
		else return null;
	}

	@Override
	protected SeqMessage createReqMessage() {
		return new GiaHandshake2(true, getComponent().getOwnContact(), getTo(), mgr.getDegree(), generateTryPeersList(), getComponent().getTokenAllocationRateFor(getTo()));
	}
	
	/**
	 * Sequenznummer gilt über gesamtes Handshake, deshalb die letzte weiterverwenden.
	 */
	protected int getNewSequenceNumber() {
		return requestSeqNo;
	}

	@Override
	protected long getTimeout() {
		return getComponent().getConfig().getHandshakeTimeout();
	}

	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (response instanceof GiaHandshake3) {
			GiaHandshake3 hs3 = (GiaHandshake3) response;
			if (hs3.getX().equals(getTo()))
				processHandshake3(hs3);
			return true;
		}
		return false;
	}

	/**
	 * @param hs3
	 */
	private void processHandshake3(GiaHandshake3 hs3) {
		if (hs3.isAccepted()) {
			if (decider.getDecision() == Decision.Accept) {
				finishConnection(getTo(), toDegree, hs3.getInitialTokenAllocationRate());
				conn.connectionSucceeded(); //Reserved slot is taken and ping procedure initiated.
			} else if (decider.getDecision() == Decision.DropAndAccept) {
				IManageableConnection c;
				if ((c = mgr.dropAndInsert(decider.getContactToDrop(), getTo())) == null) {
					System.out.println(getTo() + ": Node " + decider.getContactToDrop() +" decided to drop in the second handshake step was lost. This may happen, but should happen rarely.");
					new ConnectCloseOperation<GiaOverlayContact>(getComponent(), getTo(), null,
							Operations.getEmptyCallback()).scheduleImmediately();
				} else {
					finishConnection(getTo(), toDegree,  hs3.getInitialTokenAllocationRate());
					c.connectionSucceeded();
				}
			}
		} else {
			if (decider.getDecision() != Decision.DropAndAccept) conn.connectionFailed();
			//If a connection slot was reserved and the foreign host denies the connection setup, it is
			//marked as failed here.
		}
	}

	private void finishConnection(GiaOverlayContact to, int degreeOfContact, long initialTAR) {
		mgr.putMetadata(to, new GiaConnectionMetadata(getComponent().getConfig(), getComponent().getLocalClock(), 
				initialTAR, degreeOfContact));		
	}
	
	@Override
	protected void timeoutOccured() {
		if (decider.getDecision() == Decision.Accept) conn.connectionTimeouted();
	}

	@Override
	public Object getResult() {
		return null;
	}

}
