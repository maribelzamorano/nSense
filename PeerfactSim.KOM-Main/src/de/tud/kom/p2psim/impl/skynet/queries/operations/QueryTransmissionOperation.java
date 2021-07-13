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


package de.tud.kom.p2psim.impl.skynet.queries.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.skynet.queries.Query;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class implements the operation of a transmission of a query. Within this
 * operation a originated query is injected in the over-overlay of SkyNet and
 * forwarded towards the root until the query is solved or the root is reached.
 * In both cases the solved or unsolved query is returned to the originator and
 * this operation is finished. As this operation is not finished by receiving an
 * acknowledgment, the operation is not terminated within this class, but
 * provides the method <code>setFinishOfOperation(boolean success)</code>, which
 * allows for terminating the operation from any class of a SkyNet-node. If the
 * named method is not called within a predefined period of time, a timeout
 * occurs.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class QueryTransmissionOperation extends
		AbstractOperation<SkyNetNodeInterface, Object> {

	private static Logger log = SimLogger
			.getLogger(QueryTransmissionOperation.class);

	private SkyNetNodeInfo senderInfo;

	private SkyNetNodeInfo receiverInfo;

	private Query query;

	private boolean receiverSP;

	private long timeout;

	public QueryTransmissionOperation(SkyNetNodeInterface component,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			Query query, boolean receiverSP, long timeout,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.senderInfo = senderInfo;
		this.receiverInfo = receiverInfo;
		this.query = query;
		this.receiverSP = receiverSP;
		this.timeout = timeout;
	}

	@Override
	protected void execute() {
		log
				.debug(SkyNetUtilities.getTimeAndNetID(senderInfo)
						+ "starts the QueryTransmission of query "
						+ query.getQueryID());
		scheduleOperationTimeout(timeout);
		((SkyNetNode) getComponent()).getQueryHandler().sendQuery(senderInfo,
				receiverInfo, query, false, -1, receiverSP);
	}

	@Override
	public Object getResult() {
		// not needed
		return null;
	}

	/**
	 * This method allows for terminating the operation from any class of the
	 * SkyNet-node. The given parameter provides the state of the finished
	 * operation (success or failure).
	 * 
	 * @param success
	 *            contains the state of the finished operation
	 */
	public void setFinishOfOperation(boolean success) {
		operationFinished(success);
	}

}
