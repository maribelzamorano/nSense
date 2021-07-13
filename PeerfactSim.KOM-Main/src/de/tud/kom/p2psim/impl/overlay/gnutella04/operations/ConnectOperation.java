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


package de.tud.kom.p2psim.impl.overlay.gnutella04.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.ConnectMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConnectOperation extends
		AbstractOperation<GnutellaOverlayNode, Object> {

	private GnutellaOverlayNode node;

	private TransInfo connectInfo;

	public ConnectOperation(GnutellaOverlayNode node, TransInfo connectInfo,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.connectInfo = connectInfo;
		this.node = node;
	}

	@Override
	protected void execute() {
		GnutellaOverlayContact contact = new GnutellaOverlayContact(
				(GnutellaOverlayID) node.getOverlayID(), node.getTransLayer()
						.getLocalTransInfo(node.getPort()));
		ConnectMessage message = new ConnectMessage(
				(GnutellaOverlayID) this.node.getOverlayID(), null, contact);
		node.getTransLayer().send(message, connectInfo, node.getPort(),
				TransProtocol.UDP);
	}

	@Override
	public Object getResult() {
		return this;
	}

}
