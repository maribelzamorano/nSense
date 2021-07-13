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
 * Basic Messaging-Class for Mercury
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public abstract class AbstractMercuryMessage implements MercuryMessage {

	private MercuryPayload mercuryPayload = null;

	private static int seqNr = 0;

	private int ownSeqNr = 0;

	public AbstractMercuryMessage() {
		seqNr++;
		ownSeqNr = seqNr;
	}
	
	/**
	 * ONLY USED FOR DEBUGGING!
	 * 
	 * @return
	 */
	public int getSeqNr() {
		return this.ownSeqNr;
	}

	@Override
	public long getSize() {
		// IP-Header
		return 6; // FIXME include Payload
	}

	@Override
	public Message getPayload() {
		return this;
	}
	
	public MercuryPayload getMercuryPayload() {
		return this.mercuryPayload;
	}
	
	public void setMercuryPayload(MercuryPayload payload) {
		this.mercuryPayload = payload;
	}
	


}
