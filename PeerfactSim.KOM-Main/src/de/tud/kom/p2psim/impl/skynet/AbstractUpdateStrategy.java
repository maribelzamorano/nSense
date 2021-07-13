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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.Storage;
import de.tud.kom.p2psim.api.service.skynet.UpdateStrategy;

/**
 * This abstract class implements all getter- and setter-methods for the
 * sub-classes to relieve them from implementing these simple methods on their
 * own.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 * @param <T>
 *            determines a sub-class of <code>Storage</code>, which will be used
 *            within an implementing class to store updates.
 */
public abstract class AbstractUpdateStrategy<T extends Storage> implements
		UpdateStrategy<T> {

	protected long updateInterval;

	protected int numberOfRetransmissions;

	protected long timeForAck;

	protected SkyNetNodeInfo receiverOfNextUpdate;

	protected long sendingTime;

	public AbstractUpdateStrategy() {
		sendingTime = 0;
	}

	public long getUpdateInterval() {
		calculateUpdateInterval();
		return updateInterval;
	}

	public int getNumberOfRetransmissions() {
		return numberOfRetransmissions;
	}

	public long getTimeForACK() {
		return timeForAck;
	}

	public SkyNetNodeInfo getReceiverOfNextUpdate() {
		return receiverOfNextUpdate;
	}

	public long getSendingTime() {
		return sendingTime;
	}

	public void setSendingTime(long sendingTime) {
		this.sendingTime = sendingTime;
	}

}
