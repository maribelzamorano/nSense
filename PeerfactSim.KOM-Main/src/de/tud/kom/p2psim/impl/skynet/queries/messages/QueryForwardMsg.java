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


package de.tud.kom.p2psim.impl.skynet.queries.messages;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;
import de.tud.kom.p2psim.impl.skynet.queries.Query;

/**
 * This message is used to forward a query through the SkyNet-tree. It contains
 * the query and signalizes by the <code>isSolved</code>-flag, if the message
 * contains just the ordinary query or the result.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class QueryForwardMsg extends AbstractSkyNetMessage {

	private Query query;

	private boolean isSolved;

	public QueryForwardMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, Query query, boolean isSolved,
			long skyNetMsgID, boolean receiverSP, boolean senderSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, receiverSP,
				senderSP);
		this.query = query;
		this.isSolved = isSolved;
	}

	/**
	 * This method returns the content of the message, which is the query in
	 * this case.
	 * 
	 * @return the forwarded query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * This method determines, if the message contains the ordinary query or the
	 * result for the query.
	 * 
	 * @return <code>true</code>, if the message contains the result of the
	 *         query, <code>false</code> otherwise
	 */
	public boolean isSolved() {
		return isSolved;
	}

	public void setSolved(boolean isSolved) {
		this.isSolved = isSolved;
	}

	@Override
	public long getSize() {
		return query.getSize() + super.getSize();
	}

}
