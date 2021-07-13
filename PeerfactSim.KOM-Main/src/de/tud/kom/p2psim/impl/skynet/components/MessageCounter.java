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


package de.tud.kom.p2psim.impl.skynet.components;

/**
 * This class implements a counter for the sent messages of a single
 * SkyNet-node. The counter is used within the SkyNet-node to inform the node
 * about the sent messages. In addition, every sent message is marked with a
 * number from this counter.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class MessageCounter {

	private long counter;

	public MessageCounter() {
		counter = 0;
	}

	/**
	 * This method returns the amount of sent message of a single SkyNet-node.
	 * 
	 * @return amount of sent messages
	 */
	public long getMessageCount() {
		return counter;
	}

	/**
	 * This method calculates the next number for the next SkyNet-message, which
	 * will be sent.
	 * 
	 * @return the ID for the next SkyNet-message
	 */
	public long assignmentOfMessageNumber() {
		long messageNumber = counter;
		counter = counter + 1;
		return messageNumber;
	}
}
