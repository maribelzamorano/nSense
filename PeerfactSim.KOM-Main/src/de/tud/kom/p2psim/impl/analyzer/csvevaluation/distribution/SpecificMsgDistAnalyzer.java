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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class SpecificMsgDistAnalyzer extends MsgsRcvDistAnalyzer {

	@Override
	protected void resetDistributions() {
		resetCatMsgs();
		super.resetDistributions();
	}

	protected void resetCatMsgs() {
		catMsgs = new HashMap<MessageCategory, Map<NetID, Integer>>();
		MessageCategory[] array = MessageCategory.values();
		for (int i = 0; i < array.length; i++)
			catMsgs.put(array[i], new HashMap<NetID, Integer>());
	}

	static final Map<MessageCategory, Integer> categoryPositions = new HashMap<MessageCategory, Integer>();

	static {
		MessageCategory[] array = MessageCategory.values();
		for (int i = 0; i < array.length; i++)
			categoryPositions.put(array[i], i);
	}

	Map<Class<? extends Message>, Integer> messageTypes = new HashMap<Class<? extends Message>, Integer>();

	Map<MessageCategory, Map<NetID, Integer>> catMsgs = new HashMap<MessageCategory, Map<NetID, Integer>>();

	Set<NetID> hostsSeen = new HashSet<NetID>();

	public SpecificMsgDistAnalyzer() {
		super();
		resetCatMsgs();
	}

	protected abstract MessageCategory getMessageCategory(Message overlayMsg,
			NetID id);

	protected void declareDistributions() {
		MessageCategory[] array = MessageCategory.values();
		for (int i = 0; i < array.length; i++)
			addDistribution(array[i].toString());
	}

	protected void hostGotMsg(NetID id, NetMessage msg) {
		this.checkTimeProgress();

		Message overlayMsg = msg.getPayload().getPayload(); // Overlay

		MessageCategory cat = getMessageCategory(overlayMsg, msg.getReceiver());
		if (cat == null)
			return;

		int catPos = categoryPositions.get(cat);
		Map<NetID, Integer> catMap = catMsgs.get(cat);

		if (!catMap.containsKey(id))
			hostSeen(id);

		int oldAmount = catMap.get(id);
		catMap.put(id, oldAmount + 1);
		this.updateHost(id, catPos, oldAmount + 1);

		updateMessageTypes(overlayMsg);
	}

	private void updateMessageTypes(Message msg) {
		Integer count = messageTypes.get(msg.getClass());
		if (count == null)
			count = 0;
		messageTypes.put(msg.getClass(), count + 1);
	}

	protected void hostSeen(NetID id) {
		this.checkTimeProgress();

		if (!hostsSeen.contains(id)) {

			MessageCategory[] cats = MessageCategory.values();

			for (int i = 0; i < cats.length; i++) {
				Map<NetID, Integer> catMap = catMsgs.get(cats[i]);

				if (!catMap.containsKey(id)) {
					catMap.put(id, 0);
					this.addHostOrUpdateAll(id, newEmptyValueArray());
				}
			}
		}
	}

	protected void hostLeft(NetID id) {
		this.checkTimeProgress();

		if (hostsSeen.contains(id)) {

			MessageCategory[] cats = MessageCategory.values();

			for (int i = 0; i < cats.length; i++) {
				Map<NetID, Integer> catMap = catMsgs.get(cats[i]);

				if (catMap.containsKey(id)) {
					catMap.remove(id);
					this.removeHost(id);
				}
			}
		}
	}

	public void stop(Writer w) {
		super.stop(w);
		printAppearedMsgTypes();
	}

	public void printAppearedMsgTypes() {
		System.out
				.println("===== Overlay message types that appeared during simulation: =====");
		for (Class<? extends Message> msg : messageTypes.keySet()) {
			System.out.println(msg + ": " + messageTypes.get(msg));
		}
		System.out
				.println("==================================================================");
	}

	static long[] newEmptyValueArray() {
		int length = MessageCategory.values().length;
		long[] result = new long[length];
		for (int i = 0; i < length; i++)
			result[i] = 0;
		return result;
	}

}
