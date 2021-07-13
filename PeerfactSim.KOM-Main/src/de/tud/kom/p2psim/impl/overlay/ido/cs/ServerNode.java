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

package de.tud.kom.p2psim.impl.overlay.ido.cs;

import java.util.HashMap;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.ServerDisseminationOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.ServerMaintenanceOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;

/**
 * This class is the main class for the server in the Client/Server IDO system.
 * With this server connects all clients. Additionally gives this server a
 * unique ID to a client back.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerNode extends AbstractOverlayNode {

	/**
	 * The maximal number of clients
	 */
	private int maxClients;

	/**
	 * A unique ID counter for the clientIDs
	 */
	private int uniqueIDCounter = 0;

	/**
	 * The {@link TransLayer} for this server
	 */
	private TransLayer translayer;

	/**
	 * The incoming Message Handler
	 */
	private ServerMessageHandler msgHandler;

	/**
	 * The storage of the server. It stores the incoming client node infos.
	 */
	private ServerStorage serverStorage;

	/**
	 * The dissemination operations.
	 */
	private HashMap<ClientID, ServerDisseminationOperation> disseminationOperations;

	protected ServerNode(TransLayer translayer, short port, int maxClients) {
		super(null, port);
		this.maxClients = maxClients;

		this.translayer = translayer;
		this.msgHandler = new ServerMessageHandler(this);
		this.getTransLayer().addTransMsgListener(this.msgHandler,
				this.getPort());

		this.serverStorage = new ServerStorage(maxClients);

		this.maintenanceEvent();

		this.disseminationOperations = new HashMap<ClientID, ServerDisseminationOperation>();

		if (getTransLayer().getHost().getNetLayer().isOnline())
			setPeerStatus(PeerStatus.PRESENT);
		else
			setPeerStatus(PeerStatus.ABSENT);
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			setPeerStatus(PeerStatus.PRESENT);
		} else {
			setPeerStatus(PeerStatus.ABSENT);
		}
	}

	@Override
	public TransLayer getTransLayer() {
		return translayer;
	}

	/**
	 * Returns an unique ClientID.
	 * 
	 * @return A unique ClientID.
	 */
	public ClientID getUniqueClientID() {
		ClientID result = new ClientID(uniqueIDCounter);
		uniqueIDCounter++;
		return result;
	}

	/**
	 * Gets the {@link TransInfo} of this node
	 * 
	 * @return The {@link TransInfo} of this node
	 */
	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(this.getPort());
	}

	public ServerStorage getStorage() {
		return serverStorage;
	}

	/**
	 * Starts the dissemination operation of the given client ID.
	 * 
	 * @param id
	 *            The ID of the client.
	 */
	public void startDissemination(ClientID id) {
		disseminationOperations.put(id, null);
		disseminationEvent(id);
	}

	/**
	 * Stops the dissemination operation for the given client ID.
	 * 
	 * @param id
	 *            The ID of the client.
	 */
	public void stopDissemination(ClientID id) {
		if (disseminationOperations.containsKey(id))
			disseminationOperations.get(id).stop();
		disseminationOperations.remove(id);
	}

	/**
	 * Adds a dissemination operation for a specific id to the scheduler. After
	 * successful, it reexecute the dissemination.
	 * 
	 * @param id
	 *            The id of a client.
	 */
	protected void disseminationEvent(ClientID id) {
		ServerDisseminationOperation op = new ServerDisseminationOperation(
				this, id, new OperationCallback<ClientID>() {

					@Override
					public void calledOperationFailed(Operation<ClientID> op) {
						// do nothing, because it should be stopped!
					}

					@Override
					public void calledOperationSucceeded(Operation<ClientID> op) {
						ClientID id = op.getResult();
						disseminationEvent(id);
					}
				});
		if (disseminationOperations.containsKey(id)) {
			op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_DISSEMINATION_SERVER);
			disseminationOperations.put(id, op);
		}
	}

	/**
	 * Adds the maintenance operation to the scheduler. After successful
	 * execution, it reexecutes the operation.
	 */
	protected void maintenanceEvent() {
		ServerMaintenanceOperation op = new ServerMaintenanceOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// Do nothing
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						maintenanceEvent();
					}
				});
		op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_MAINTENANCE_SERVER);

	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return null;
	}
}
