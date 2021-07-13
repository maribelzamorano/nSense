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

package de.tud.kom.p2psim.impl.overlay.ido.von.operations;

import java.util.LinkedList;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonConfiguration;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonID;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNode;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNodeInfo;
import de.tud.kom.p2psim.impl.overlay.ido.von.messages.HelloMsg;

/**
 * This operation informs a set of nodes about its existence and the current
 * status. The contacts of nodes that have to be informed are passed as
 * parameters on creation of the operation.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class HelloOperation extends AbstractOperation<VonNode, Object> {

	private final LinkedList<VonNodeInfo> toInform;

	public HelloOperation(VonNode component, LinkedList<VonNodeInfo> toInform,
			OperationCallback callback) {
		super(component, callback);
		this.toInform = toInform;
	}

	@Override
	protected void execute() {
		if (getComponent().getPeerStatus() != PeerStatus.PRESENT) {
			operationFinished(false);
			return;
		}

		scheduleOperationTimeout(VonConfiguration.OP_TIMEOUT_HELLO);

		VonID sId = getComponent().getVonID();
		VonNodeInfo sInfo = getComponent().getNodeInfo();

		VonID rId;
		VonID[] rEnclosings;

		for (VonNodeInfo rInfo : toInform) {

			rId = rInfo.getContact().getOverlayID();
			rEnclosings = getComponent().getLocalVoronoi()
					.getEnclosingNeighbors(rId);

			/*
			 * if the following condition holds, the node is not anymore in the
			 * voronoi and therefore no HelloMsg is send.
			 */
			if (rEnclosings == null)
				continue;

			HelloMsg m = new HelloMsg(sId, rInfo.getContact().getOverlayID(),
					sInfo, rEnclosings);

			getComponent().getTransLayer().send(m,
					rInfo.getContact().getTransInfo(),
					getComponent().getPort(),
					VonConfiguration.TRANSPORT_PROTOCOL);
		}

		operationFinished(true);

	}

	@Override
	public Object getResult() {
		// Is there a result? -> At the moment I do not think so.
		return null;
	}

}
