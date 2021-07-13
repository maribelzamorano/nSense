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

package de.tud.kom.p2psim.impl.overlay.ido.cs.operations;

import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientContact;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientID;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientNodeInfo;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ServerNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.UpdatePositionClientMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;

/**
 * The operation of the server, to disseminate the positions of the clients to
 * one client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class ServerDisseminationOperation extends
		AbstractOperation<ServerNode, ClientID> {

	/**
	 * The id of the receiver of this dissemination.
	 */
	private ClientID id;

	private ServerNode node;

	public ServerDisseminationOperation(ServerNode node, ClientID id,
			OperationCallback<ClientID> callback) {
		super(node, callback);
		this.node = node;
		this.id = id;
	}

	/**
	 * Derive all neighbors for the stored clientID, and send this list to the
	 * client.
	 */
	@Override
	protected void execute() {
		if (!isFinished()) {
			ClientContact contact = node.getStorage().getClientContact(id);
			List<ClientNodeInfo> nodeInfos = node.getStorage()
					.findNeighbors(id);
			UpdatePositionClientMessage msg = new UpdatePositionClientMessage(
					nodeInfos);

			node.getTransLayer().send(msg, contact.getTransInfo(),
					node.getPort(), CSConfiguration.TRANSPORT_PROTOCOL);

			this.operationFinished(true);
		}
		this.operationFinished(false);
	}

	@Override
	public ClientID getResult() {
		return this.id;
	}

	/**
	 * Stops this Operation.
	 */
	public void stop() {
		this.operationFinished(false);
	}

}
