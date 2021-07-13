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

package de.tud.kom.p2psim.impl.service.mercury.analyzer;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.service.mercury.messages.AbstractMercuryMessage;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryPublication;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MessageAnalyzer implements Analyzer, NetAnalyzer {

	private boolean start = false;
	
	private Map<NetID, Long> aggrMsgSizes = new HashMap<NetID, Long>();

	private HashMap<String, Integer> loggedMessages = new HashMap<String, Integer>();

	private HashMap<String, Integer> loggedMessagesSizes = new HashMap<String, Integer>();

	private HashMap<NetID, Integer> numMessages = new HashMap<NetID, Integer>();

	private HashMap<String, HashMap<Integer, Integer>> numMessagesBySeqNo = new HashMap<String, HashMap<Integer, Integer>>();

	private HashMap<String, Integer> numMessagesByType = new HashMap<String, Integer>();

	private void addMessage(AbstractMercuryMessage msg) {

		if (!numMessagesBySeqNo.containsKey(msg.getClass().getName())) {
			numMessagesBySeqNo.put(msg.getClass().getName(),
					new HashMap<Integer, Integer>());
		}
		HashMap<Integer, Integer> type = numMessagesBySeqNo.get(msg.getClass()
				.getName());
		if (!type.containsKey(msg.getSeqNr())) {
			type.put(msg.getSeqNr(), 0);
		}

		type.put(msg.getSeqNr(), type.get(msg.getSeqNr()) + 1);

		String name = msg.getClass().getName();
		if (!loggedMessages.containsKey(name)) {
			loggedMessages.put(name, 0);
			loggedMessagesSizes.put(name, 0);
		}
		loggedMessages.put(name, loggedMessages.get(name) + 1);
		loggedMessagesSizes.put(name,
				loggedMessagesSizes.get(name) + (int) msg.getSize());

	}

	private void printNumMessagesBySeqNo() {
		for (String name : numMessagesBySeqNo.keySet()) {
			System.out.println(name);
			
			for (Integer anz : numMessagesBySeqNo.get(name).values()) {
				if (anz > 3) {
					System.out.print(anz + " ; ");
				}
			}
		}
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		if (start) {
			Message msg2 = msg.getPayload().getPayload();


			Message merc = msg2.getPayload();

			if (msg2 instanceof AbstractMercuryMessage) {
				AbstractMercuryMessage merm = (AbstractMercuryMessage) msg2;
				addMessage(merm);
			}

			if (msg2 instanceof MercuryPublication) {
				if (numMessages.containsKey(msg.getSender())) {
					numMessages.put(msg.getSender(),
							numMessages.get(msg.getSender()) + 1);
				} else {
					numMessages.put(msg.getSender(), 1);
				}
			}

			if (!numMessagesByType.containsKey(msg2.getClass().getSimpleName())) {
				numMessagesByType.put(msg2.getClass().getSimpleName(), 0);
			}
			numMessagesByType.put(msg2.getClass().getSimpleName(),
					numMessagesByType.get(msg2.getClass().getSimpleName()) + 1);


			// if(msg2.getPayload() instanceof MercurySubscription){
			// MercurySubscription ms = (MercurySubscription) msg2.getPayload();
			// ms.get
			// }

			// System.out.println(Simulator.getFormattedTime(Simulator
			// .getCurrentTime())
			// + " from:"
			// + msg.getSender().toString()
			// + " to:"
			// + msg.getReceiver().toString()
			// + " type:"
			// + msg2.getClass().getSimpleName()
			// + " size:"
			// + msg2.getSize());

//			System.out.println("#######MsgSend#########");
//			System.out.println("Sender: " + msg.getSender());
//			System.out.println("Receiver: " + msg.getReceiver());
//			System.out.println("Payload Msg: "
//					+ msg.getPayload().getPayload().getClass() + msg.hashCode());
		}
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		if (start) {
//			System.out.println("#######MsgReceived#########");
//			System.out.println("Sender: " + msg.getSender());
//			System.out.println("Receiver: " + msg.getReceiver());
//			System.out.println("Payload Msg: "
//					+ msg.getPayload().getPayload().getClass() + msg.hashCode());
//			if (msg.getPayload().getPayload().getPayload() instanceof MercuryMessage) {
				if (!aggrMsgSizes.containsKey(id)) {
					aggrMsgSizes.put(id, (long) 0);
				}
				aggrMsgSizes.put(id, (long) (aggrMsgSizes.get(id)+1));
//			}
		}
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// not interested
	}

	@Override
	public void start() {
		this.start = true;

	}

	@Override
	public void stop(Writer output) {
		this.start = false;
		System.out.println(this.loggedMessages.toString());
		System.out.println(this.loggedMessagesSizes.toString());
		System.out.println(this.numMessages.toString());
		printNumMessagesBySeqNo();
		System.out.println(this.numMessagesByType.toString());
		// System.out.println(aggrMsgSizes.toString());
	}

}
