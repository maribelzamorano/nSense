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


package de.tud.kom.p2psim.impl.overlay.gnutella;

import java.io.Writer;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.GnutellaEvents;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.IGnutellaEventListener;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPing;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.GnutellaPong;
import de.tud.kom.p2psim.impl.overlay.gnutella.gia.messages.GiaHandshake3;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Analyzer that dumps out Gnutella events. For debugging purposes.
 * @author  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MessageDumper implements NetAnalyzer, OperationAnalyzer,
		ConnectivityAnalyzer, IGnutellaEventListener {

	int dumpCounter = 0;
	
	static final boolean MASS_STEP = true;

	public MessageDumper() {
		GnutellaEvents.getGlobal().addListener(this);
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// Message olMsg = msg.getPayload().getPayload();
		//		
		// System.out.println(time() + id + " dropped: " + olMsg);
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		Message olMsg = msg.getPayload().getPayload();		

		if (olMsg instanceof GnutellaPing) return;
		if (olMsg instanceof GnutellaPong) return;
		//if (olMsg instanceof GiaHandshake1) return;
		//if (olMsg instanceof GiaHandshake2) return;
		if (olMsg instanceof GiaHandshake3) return;
		/*
		if (olMsg instanceof Gnutella06ConnectReply) return;
		if (olMsg instanceof Gnutella06Ack) return;
		if (olMsg instanceof Gnutella06Connect) return;
		if (olMsg instanceof Gnutella06Close) return;		
		*/
		
		System.out.println(time() + id + " received: " + olMsg);
		
		if (MASS_STEP) {
			dumpStateCond();
		} else {
			AbstractGnutellaLikeNode.dumpStateOfAll();
			sleep();
		}
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		// Message olMsg = msg.getPayload().getPayload();
		//		
		// if (olMsg instanceof Gnutella06Ping) return;
		// if (olMsg instanceof Gnutella06Pong) return;
		// if (olMsg instanceof Gnutella06ConnectReply) return;
		// if (olMsg instanceof Gnutella06Ack) return;
		// if (olMsg instanceof Gnutella06Connect) return;
		// if (olMsg instanceof Gnutella06Close) return;
		//		
		// System.out.println(time() + id + " sent: " + olMsg);
	}

	@Override
	public void stop(Writer output) {
		AbstractGnutellaLikeNode.dumpStateOfAll();
		sleep();
	}

	void dumpStateCond() {
		dumpCounter++;
		if (dumpCounter >= 100) {
			AbstractGnutellaLikeNode.dumpStateOfAll();
			sleep();
			dumpCounter = 0;
		}
	}
	
	void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {
		// System.out.println(time() + "Finished Op. ComponentState: " +
		// op.getComponent());
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		// System.out.println("Started Op: " + op.getClass().getSimpleName());
		// dumpStateCond();

	}

	@Override
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause) {
		// System.out.println(time() + "Connection failed: " + invoker + " to "
		// + receiver + " UID=" + connectionUID + " Cause=" + cause);
	}

	@Override
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// System.out.println(time() + "Connection started: " + invoker + " to "
		// + receiver + " UID=" + connectionUID);
	}

	@Override
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		// System.out.println(time() + "Connection succeeded: " + invoker +
		// " to " + receiver + " UID=" + connectionUID);
	}

	public String time() {
		return Simulator.getFormattedTime(Simulator.getCurrentTime());
	}

	@Override
	public void start() {
		//Nothing to do
	}

	@Override
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver) {
		// System.out.println(time() + "Ping timeouted: " + invoker + " to " +
		// receiver);
	}

	@Override
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		System.out.println(time() + "Query failed: " + initiator + " for "
				+ query + ", " + hits + " Hits");
	}

	@Override
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query) {
		System.out.println(time() + "Query started: " + initiator + " for "
				+ query);
	}

	@Override
	public void querySucceeded(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		System.out.println(time() + "Query succeeded: " + initiator + " for "
				+ query + ", " + hits + " Hits");
	}

	@Override
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact ownContact) {
		// System.out.println(time() + "Query made hop: " + queryUID + " at " +
		// ownContact);
	}

	@Override
	public void offlineEvent(Host host) {
		System.out.println("Offline: " + host);
	}

	@Override
	public void onlineEvent(Host host) {
		// System.out.println("Online: " + host);
	}

	@Override
	public void reBootstrapped(GnutellaLikeOverlayContact c) {
		System.out.println("Peer re-bootstrapped: " + c);
	}

	@Override
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode,
			GnutellaLikeOverlayContact opponent, ConnBreakCause cause) {
		// TODO Auto-generated method stub
		
	}

}
