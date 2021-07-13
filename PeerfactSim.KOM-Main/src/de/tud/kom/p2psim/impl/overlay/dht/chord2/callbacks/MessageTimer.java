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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used as Observer for a message. A timer will be started, if
 * message time out event occurs, the message will be resent. After a specified
 * number of retry, if the receiver still not reacts, <code>MessageTimer</code>
 * will notify the sender about offline event of receiver.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageTimer implements TransMessageCallback {

	final static Logger log = SimLogger.getLogger(MessageTimer.class);

	private int resendCounter = 0;

	private final ChordNode masterNode;

	private final Message msg;

	private final ChordContact receiver;

	private static int timeoutCount;

	public MessageTimer(ChordNode masterNode, Message msg, ChordContact receiver) {

		this.masterNode = masterNode;
		this.msg = msg;
		this.receiver = receiver;
	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (!masterNode.isPresent())
			return;

		timeoutCount++;
		log.debug("time out msg = " + msg + " resendTime = " + resendCounter
				+ " sumTimeOut " + timeoutCount);
		if (resendCounter < ChordConfiguration.MESSAGE_RESEND) {
			resendCounter++;
			resendMessage();
		} else {
			log.debug("Receiver (" + receiver + ") of "
					+ msg.getClass().getSimpleName() + " did not answer after "
					+ resendCounter + " tries.");
			masterNode.getChordRoutingTable().receiveOfflineEvent(receiver);

		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		log.debug("receive ack msg = " + msg);
	}

	private void resendMessage() {
		log.debug("resend msg = " + msg + " receiver "
				+ receiver.getOverlayID() + " resend times " + resendCounter);
		masterNode.getTransLayer().sendAndWait(msg, receiver.getTransInfo(),
				masterNode.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
				this, ChordConfiguration.MESSAGE_TIMEOUT);
	}
}
