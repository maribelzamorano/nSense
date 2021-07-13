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

import de.tud.kom.p2psim.api.common.Message;

/**
 * Wrapper Class for a subscription that is not intended to be forwarded. This
 * is used to speed up subscription delivery, as nodes with a small range can
 * use their upload to distribute the subscription to all intended receivers.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercurySubscriptionDirect implements MercuryMessage {

	private MercurySubscription sub;

	public MercurySubscriptionDirect(MercurySubscription sub) {
		this.sub = sub;
	}

	public MercurySubscription getSubscription() {
		return sub;
	}

	@Override
	public long getSize() {
		return sub.getSize();
	}

	@Override
	public Message getPayload() {
		return this;
	}

}
