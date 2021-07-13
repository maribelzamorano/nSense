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

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.overlay.ido.AbstractIDONode;
import de.tud.kom.p2psim.impl.overlay.ido.cs.messages.UpdatePositionServerMessage;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.ClientHeartbeatOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.ClientMaintenanceOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.JoinOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.operations.LeaveOperation;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConfiguration;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is the main class for the client in the Client/Server IDO system.
 * This class provides the joining and leaving of the overlay (in this case, the
 * connecting and disconnecting with the server). Additionally provides this
 * class the disseminating of a position.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class ClientNode extends AbstractIDONode<ClientID> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(ClientNode.class);

	/**
	 * The {@link TransLayer} of the node
	 */
	private TransLayer translayer;

	/**
	 * The incoming message handler of the node
	 */
	private ClientMessageHandler msgHandler;

	/**
	 * The {@link TransInfo} of a server, to which does the dissemination.
	 */
	private TransInfo serverTransInfo;

	/**
	 * The storage of other client positions
	 */
	private ClientStorage storage;

	/**
	 * The bootstrapManager, which is used
	 */
	private CSBootstrapManager bootstrap;

	/**
	 * The last heartbeat time. It describes the last dissemination to the
	 * server.
	 */
	private long lastHeartbeat;

	/**
	 * The heartbeat operation.
	 */
	private ClientHeartbeatOperation heartbeatOperation;

	/**
	 * The last time, which send the server an update.
	 */
	private long lastUpdate;

	/**
	 * The maintenance operation
	 */
	private ClientMaintenanceOperation maintenanceOp;

	/**
	 * The join operation.
	 */
	private JoinOperation joinOp;

	/**
	 * A flag, if the node should be in the game.
	 */
	private boolean inGame = false;

	/**
	 * The constructor of the Client.
	 * 
	 * @param translayer
	 *            The translayer of the node
	 * @param port
	 *            The listing port
	 * @param aoi
	 *            The radius for the area of interest
	 * @param bootstrap
	 *            The bootstrap manager, which should be used.
	 */
	protected ClientNode(TransLayer translayer, short port, int aoi,
			BootstrapManager bootstrap) {
		super(ClientID.EMPTY_ID, port, aoi);

		this.translayer = translayer;
		this.msgHandler = new ClientMessageHandler(this);
		this.getTransLayer().addTransMsgListener(this.msgHandler,
				this.getPort());
		this.setBootstrapManager(bootstrap);

		this.storage = new ClientStorage();

		setPeerStatus(PeerStatus.ABSENT);
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline() && inGame) {
			join();
		} else {
			stop();
			setPeerStatus(PeerStatus.ABSENT);
		}

	}

	@Override
	public void leave(boolean crash) {
		inGame = false;
		if (!crash && getPeerStatus() == PeerStatus.PRESENT) {
			LeaveOperation op = new LeaveOperation(this);
			op.scheduleImmediately();
		}
		stop();
		setPeerStatus(PeerStatus.ABSENT);
	}

	/**
	 * Stops the operation of this node.
	 */
	public void stop() {
		if (heartbeatOperation != null) {
			heartbeatOperation.stop();
			heartbeatOperation = null;
		}
		if (maintenanceOp != null) {
			maintenanceOp.stop();
			maintenanceOp = null;
		}
		if (joinOp != null) {
			joinOp.stop();
			joinOp = null;
		}

	}

	/**
	 * Resets the node. Stops the operation, sets the storage back, sets the
	 * peer status to absent and set the serverTransInfo to null.
	 */
	public void reset() {
		stop();
		storage = new ClientStorage();
		setPeerStatus(PeerStatus.ABSENT);
		serverTransInfo = null;
	}

	@Override
	public void join(Point position) {
		inGame = true;
		setPosition(position);
		join();
	}

	/**
	 * Adds the join Operation to the scheduler. If the operation failed, it do
	 * a rejoin. Otherwise the node is present in the overlay and starts
	 * hearbeat Operation and maintenace Operation.
	 */
	public void join() {
		reset();
		JoinOperation op = new JoinOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						joinOp = null;
						setPeerStatus(PeerStatus.PRESENT);
						heartbeatOperation();
						maintenanceOperation();
					}

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// little dirty ;-) Only a rejoin, if the node has the
						// PeerStatus.TO_JOIN
						if (getTransLayer().getHost().getNetLayer().isOnline()
								&& getPeerStatus() == PeerStatus.TO_JOIN) {
							// do a rejoin
							join();
						}
					}
				});
		setPeerStatus(PeerStatus.TO_JOIN);
		op.scheduleImmediately();
		joinOp = op;
	}

	@Override
	public void disseminatePosition(Point position) {
		this.setPosition(position);
		// disseminate the position
		disseminatePosition();
	}

	/**
	 * Disseminate the actually nodeInfo of this node. This equates the
	 * dissemination of the position of this node.
	 */
	public void disseminatePosition() {
		if (getPeerStatus() == PeerStatus.PRESENT
				&& getTransLayer().getHost().getNetLayer().isOnline()) {

			ClientNodeInfo nodeInfo = this.getNodeInfo();
			UpdatePositionServerMessage msg = new UpdatePositionServerMessage(
					nodeInfo);
			this.getTransLayer().send(msg, this.getServerTransInfo(),
					this.getPort(), CSConfiguration.TRANSPORT_PROTOCOL);
			this.setLastHeartbeat(Simulator.getCurrentTime());
		}
	}

	/**
	 * Adds the heartbeat Operation to the scheduler, and started a new hearbeat
	 * Operation, if it successful finished, otherwise stops the operation.
	 */
	public void heartbeatOperation() {
		ClientHeartbeatOperation op = new ClientHeartbeatOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// do nothing because it is stopped
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						heartbeatOperation();
					}
				});
		op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_HEARTBEAT_OPERATION);
		heartbeatOperation = op;
	}

	/**
	 * Adds the maintenance Operation to the scheduler, and started a new
	 * maintenance Operation, if it successful finished, otherwise stops the
	 * operation.
	 */
	public void maintenanceOperation() {
		ClientMaintenanceOperation op = new ClientMaintenanceOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// is stopped. do nothing
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						maintenanceOperation();
					}
				});
		op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_MAINTENANCE_CLIENT);
		maintenanceOp = op;
	}

	@Override
	public List<IDONodeInfo> getNeighborsNodeInfo() {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>(
				storage.getNeighbors());
		return result;
	}

	@Override
	public BootstrapManager getBootstrapManager() {
		return bootstrap;
	}

	@Override
	public void setBootstrapManager(BootstrapManager bootstrapManager) {
		if (bootstrapManager instanceof CSBootstrapManager)
			this.bootstrap = (CSBootstrapManager) bootstrapManager;
		else
			log.error("The wrong bootstrapManager!");

	}

	@Override
	public TransLayer getTransLayer() {
		return translayer;
	}

	/**
	 * Gets the {@link TransInfo} of this node
	 * 
	 * @return The {@link TransInfo} of this node
	 */
	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(this.getPort());
	}

	public void setServerTransInfo(TransInfo serverTransInfo) {
		this.serverTransInfo = serverTransInfo;
	}

	public TransInfo getServerTransInfo() {
		return serverTransInfo;
	}

	public ClientStorage getStorage() {
		return storage;
	}

	public ClientNodeInfo getNodeInfo() {
		return new ClientNodeInfo(this.getPosition(), this.getAOI(),
				this.getOverlayID());
	}

	public long getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	public void setLastUpdate(long time) {
		this.lastUpdate = time;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}
}
