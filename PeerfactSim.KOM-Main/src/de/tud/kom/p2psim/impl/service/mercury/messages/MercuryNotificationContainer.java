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

package de.tud.kom.p2psim.impl.service.mercury.messages;

import java.util.List;

import de.tud.kom.p2psim.api.common.Message;

/**
 * Container for MercuryNotifications, stores a bunch of Notifications for one
 * target and allows for better utilization of the assigned bandwith
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryNotificationContainer extends AbstractMercuryMessage {

	private List<MercuryNotification> notifications;

	public MercuryNotificationContainer(List<MercuryNotification> notifications) {
		this.notifications = notifications;
	}

	/**
	 * Get all Notifications stored in this Container
	 * 
	 * @return
	 */
	public List<MercuryNotification> getNotifications() {
		return notifications;
	}

	@Override
	public String toString() {
		return "NotificationContainer [" + getSeqNr() + "]"
				+ notifications.toString();
	}

	@Override
	public long getSize() {
		long size = 0;
		for (MercuryNotification notify : notifications) {
			size += notify.getSize();
		}
		return super.getSize() + size;
	}

	@Override
	public Message getPayload() {
		return this;
	}

}
