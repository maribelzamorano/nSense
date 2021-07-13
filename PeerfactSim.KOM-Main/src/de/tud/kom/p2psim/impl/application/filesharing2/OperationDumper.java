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


package de.tud.kom.p2psim.impl.application.filesharing2;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.util.BandwidthMonitor;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class OperationDumper implements OperationAnalyzer, KBROverlayAnalyzer, FSEventListener, NetAnalyzer, ConnectivityAnalyzer {

	Map<Class<? extends Message>, Integer> msgs = new HashMap<Class<? extends Message>, Integer>();
	
	int count = 0;
	long interval = 30 * Simulator.SECOND_UNIT;
	
	long timeOfLastDump = 0;
	
	long realTimeOfLastDump = System.currentTimeMillis();
	
	public OperationDumper() {
		FSEvents.getInstance().addListener(this);
	}
	
	@Override
	public void operationFinished(Operation<?> op) {
		//if (op instanceof FilesharingOperation)
		//	System.out.println(Simulator.getCurrentTime()/1000 + "ms: Operation " + op.getClass().getSimpleName() + " finished.");
	}

	@Override
	public void operationInitiated(Operation<?> op) {
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(Writer output) {
		System.out.println("GNP-STATS: Permitted:" + BandwidthMonitor.permittedAttempts + " Denied: " + BandwidthMonitor.deniedAttempts);
		//Only for Gia:
		//QueryDebugger.getInstance().close();
	}

	@Override
	public void messageDelivered(OverlayContact contact, Message msg, int hops) {
		System.out.println(Simulator.getCurrentTime()/1000 + "ms: Message " + msg.getPayload() + " + delivered to " + contact.getTransInfo().getNetId());
	}

	@Override
	public void messageForwarded(OverlayContact sender, OverlayContact receiver, Message msg, int hops) {
		//System.out.println(Simulator.getCurrentTime()/1000 + "ms: Message " + msg.getClass().getSimpleName() + " + forwarded.");
	}

	@Override
	public void queryFailed(OverlayContact failedHop, Message appMsg) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " ms: Message " + appMsg + " + failed.");
	}

	@Override
	public void queryStarted(OverlayContact contact, Message appMsg) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " Message " + appMsg + " + started at " + contact.getTransInfo().getNetId());
	}

	@Override
	public void lookupStarted(OverlayContact initiator, Object queryUID) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " LOOK? from "  + initiator + "; " + queryUID);
	}

	@Override
	public void lookupSucceeded(OverlayContact initiator, Object queryUID, int hops) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " LOOK! from " + initiator + "; " + queryUID);
	}

	@Override
	public void publishStarted(OverlayContact initiator, int keyToPublish, Object queryUID) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " PUBL? from " + keyToPublish + "; " + initiator + "; " + queryUID);
	}

	@Override
	public void publishSucceeded(OverlayContact initiator, OverlayContact holder, int keyPublished, Object queryUID) {
		System.out.println(Simulator.getFormattedTime(Simulator.getCurrentTime()) + " PUBL! for " + keyPublished + "; " + initiator + "; " + holder + "; " + queryUID);
	}	
	
	@Override
	public void lookupMadeHop(Object queryUID, OverlayContact hop) {
		//Nothing to do
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		countMessages(msg);
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		//Nothing to do;
		
	}

	private void countMessages(NetMessage msg) {
		Message olMsg = msg.getPayload().getPayload();
		
		Integer occurs = msgs.get(olMsg.getClass());
		int newOccurs;
		
		if (occurs != null) newOccurs = occurs +1;
		else newOccurs = 1;
		
		msgs.put(olMsg.getClass(), newOccurs);
		
		long currentTime = Simulator.getCurrentTime();
		
		if (timeOfLastDump + interval <= currentTime) {
			dumpMsgs();
			timeOfLastDump = currentTime;
			realTimeOfLastDump = System.currentTimeMillis();
		}
	}
	
	public void dumpMsgs() {
		
		String timeInterval = NumberFormatToolkit.floorToDecimalsString((System.currentTimeMillis() - realTimeOfLastDump)/1000d, 2) + " sec";
		
		System.out.println("===" + Simulator.getFormattedTime(Simulator.getCurrentTime()) + ": , needed " + timeInterval + " for this part. Messages: ");
		
		for (Class<? extends Message> cl : msgs.keySet()) {
			System.out.println(cl.getSimpleName() + ": " + msgs.get(cl));
		}
		msgs.clear();
		
		System.out.println("======================================================");
	}

	@Override
	public void offlineEvent(Host host) {
		//System.out.println("< Host " + host + " has gone OFFline.");
	}

	@Override
	public void onlineEvent(Host host) {
		//System.out.println("> Host " + host + " has gone ONline.");
	}


	
	

}
