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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric;

import java.io.Writer;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class derives the interface <code>TransAnalyzer</code> to receive the
 * message events in transport layer.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageCounter implements TransAnalyzer {

	private static Logger log = SimLogger.getLogger(MessageCounter.class);

	@Override
	public void start() {
		// Nothing to do here
	}

	@Override
	public void stop(Writer output) {
		// Nothing to do here
	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {

		log.trace("Translayer Receive " + msg.getPayload());
		Message overlayMsg = msg.getPayload();
		MessageStore.getInstance().registReceiveMessage(null, overlayMsg,
				Simulator.getCurrentTime());

		// evaluate lookup flow. Not a fundamental functionality
		// if (LookupMessage.class.isAssignableFrom(overlayMsg.getClass())) {
		//
		// LookupMessage lookupMessage = (LookupMessage) overlayMsg;
		// int lookupId = lookupMessage.getLookupID();
		//
		//
		// LookupGenerator generator = new LookupGenerator();
		// if (generator.containLookupId(lookupId)){
		// MessageFlowStore.getInstance().addIntermediateHop(lookupId,
		// lookupMessage.getSender(), lookupMessage.getReceiver());
		// }
		// }

	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {

		log.trace("Translayer Sent " + msg.getPayload());
		Message overlayMsg = msg.getPayload();
		MessageStore.getInstance().registSendMessage(null, overlayMsg,
				Simulator.getCurrentTime());
	}

}
