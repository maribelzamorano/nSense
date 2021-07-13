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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution.specific;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution.MessageCategory;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution.SpecificMsgDistAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.DataLookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.DataMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.KClosestNodesLookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.NodeListMsg;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Kademlia2MsgDistAnalyzer extends SpecificMsgDistAnalyzer {

	/*
	 * Chord-Messages:
	 * 
	 * class de.tud.kom.p2psim.impl.overlay.dht.chord.messages.StabilizeMsg
	 * class de.tud.kom.p2psim.impl.overlay.dht.chord.messages.LookupReply class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.PongMsg class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.JoinMsg class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.GetInfoReply class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.PingMsg class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.LookupRequest class
	 * de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.GetInfoRequest class
	 * de.tud.kom.p2psim.impl.overlay.dht.chord.messages.NotifyMsg
	 * 
	 * join, leave, maintenance, userMsg, result, other
	 */

	@Override
	protected MessageCategory getMessageCategory(Message overlayMsg, NetID id) {

		if (overlayMsg instanceof DataMsg)
			return MessageCategory.userMsg;
		if (overlayMsg instanceof DataLookupMsg)
			return MessageCategory.userMsg;

		if (overlayMsg instanceof KClosestNodesLookupMsg)
			return MessageCategory.maintenance;
		if (overlayMsg instanceof NodeListMsg)
			return MessageCategory.maintenance;

		else
			return MessageCategory.other;
	}

}
