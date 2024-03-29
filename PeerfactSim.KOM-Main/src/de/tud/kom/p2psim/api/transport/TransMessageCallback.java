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



package de.tud.kom.p2psim.api.transport;

import de.tud.kom.p2psim.api.common.Message;

/**
 * TransMessageCallbacks are used when a process has sent a message and waits
 * for a reply (or replies). This is different from a process waiting for
 * incoming connection requests. The latter should use
 * {@link TransMessageListener} for new incoming connections.
 * 
 * A TransMessageCallback waits for incoming replies passed from the
 * <code>TransLayer</code> and performs some action if various events occur.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 12/03/2007
 * 
 */

public interface TransMessageCallback {

	/**
	 * Invoking this method denotes a reply is received to a request message
	 * with communication identifier <code>commId</code>
	 * 
	 * @param msg
	 *            the incoming message
	 * @param senderInfo
	 *            the TransInfo of the sender
	 * @param commId
	 *            the unique communication id
	 */
	public void receive(Message msg, TransInfo senderInfo, int commId);

	/**
	 * Invoking this methods denotes that no reply is received in predefined
	 * simulation time units for the message with communication identifier
	 * <code>commId</code>.
	 * 
	 * @param commId
	 *            the unique communication id
	 */
	public void messageTimeoutOccured(int commId);

}
