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


package de.tud.kom.p2psim.impl.overlay.dht.pastry.operations;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.MsgTransInfo;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryConstants;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryContact;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.PastryNode;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.TransmissionCallback.Failed;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.TransmissionCallback.Succeeded;
import de.tud.kom.p2psim.impl.overlay.dht.pastry.messages.RequestLeafSetMsg;

/**
 * @author Julius Rückert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RequestLeafSetOperation extends AbstractPastryOperation<Object>
		implements Failed, Succeeded {

	PastryContact targetNode;

	PastryNode node;

	public RequestLeafSetOperation(PastryNode node, PastryContact targetNode) {
		super(node);
		this.node = node;
		this.targetNode = targetNode;
	}

	@Override
	public void execute() {
		if (!node.isPresent()) {
			operationFinished(false);
			return;
		}

		scheduleOperationTimeout(PastryConstants.OP_JOIN_TIMEOUT);

		RequestLeafSetMsg msg = new RequestLeafSetMsg(node.getOverlayID(),
				targetNode.getOverlayID());

		node.getMsgHandler().sendMsg(
				new MsgTransInfo<PastryContact>(msg, targetNode), this);

		/*
		 * FIXME: Is there a better solution? Do we need this operation? Maybe
		 * just move code to LeafSet.
		 */

	}

	@Override
	public Object getResult() {
		// There is no result
		return null;
	}

	@Override
	public void transmissionSucceeded(Message msg, Message reply) {
		operationFinished(true);
	}

	@Override
	public void transmissionFailed(Message msg) {
		operationFinished(false);
	}

}
