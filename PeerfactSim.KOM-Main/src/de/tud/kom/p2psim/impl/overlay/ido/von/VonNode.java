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

package de.tud.kom.p2psim.impl.overlay.ido.von;

import java.awt.Point;
import java.util.LinkedList;
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
import de.tud.kom.p2psim.impl.overlay.ido.von.operations.DisseminationOperation;
import de.tud.kom.p2psim.impl.overlay.ido.von.operations.HeartbeatAndMaintenanceOperation;
import de.tud.kom.p2psim.impl.overlay.ido.von.operations.JoinOperation;
import de.tud.kom.p2psim.impl.overlay.ido.von.operations.ObtainOlIDOperation;
import de.tud.kom.p2psim.impl.overlay.ido.von.voronoi.Voronoi;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class represents the main functionality of a node in the VON overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonNode extends AbstractIDONode<VonID> {

	final static Logger log = SimLogger.getLogger(VonNode.class);

	private final TransLayer translayer;

	private VonBootstrapManager bootstrapManager;

	/*
	 * Some fields to be used if node is the master
	 */
	protected boolean isMaster = false;

	protected static VonID lastAssignedID = VonID.EMPTY_ID;

	/*
	 * General node fields
	 */
	protected final VonMessageHandler msgHandler;

	private Voronoi localVoronoi;

	/*
	 * Additional fields for maintenance
	 */
	private long lastHeartbeatTime = 0;

	/**
	 * Is this node in the game. It is used for the churn.
	 */
	private boolean inGame;

	public VonNode(TransLayer translayer, short port) {
		super(VonID.EMPTY_ID, port, VonConfiguration.DEFAULT_AOI_RADIUS);
		this.translayer = translayer;

		/*
		 * Create the message handler and add it as listener to the translayer
		 */
		msgHandler = new VonMessageHandler(this);
		getTransLayer().addTransMsgListener(msgHandler, port);

		// New nodes are in the beginning absent until they join
		setPeerStatus(PeerStatus.ABSENT);
	}

	@Override
	public TransLayer getTransLayer() {
		return translayer;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (inGame) {
			if (ce.isOnline()) {
				getApplication().startMovingPlayer();
				// Rejoin the overlay
				join();

				log.debug(getVonID() + " initiated rejoin.");

			} else {
				getApplication().stopMovingPlayer();
				if (this.getPeerStatus() != PeerStatus.ABSENT) {
					if (isMaster)
						log.error(getVonID()
								+ " the master was effected by churn");

					bootstrapManager.unregisterNode(this);
					isMaster = false;

					if (waitingjoinOp != null) {
						waitingjoinOp.churnDuringJoin();
					}
					log.debug(getVonID() + " left the overlay due to churn.");
				}
				setPeerStatus(PeerStatus.ABSENT);

			}
		}

	}

	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(this.getPort());
	}

	/**
	 * Initiate joining. This includes the following steps:
	 * 
	 * - Obtaining of a new OverlayID
	 * 
	 * - Joining (this includes a query for initial neighbors and the initiation
	 * of the connection to them)
	 */
	private void join() {
		if (isNetlayerOnline()) {

			if (!bootstrapManager.anyNodeAvailable()) {
				/*
				 * If there is no other node, joining does not include any
				 * communication. A new overlay is started.
				 */
				VonID newID = new VonID(lastAssignedID.getUniqueValue() + 1);
				setOverlayID(newID);
				lastAssignedID = newID;

				bootstrapManager.registerNode(this);
				isMaster = true;

				resetVoronoiAndInternalState();

				setPeerStatus(PeerStatus.PRESENT);
				log.info("Peer " + getVonID() + " is new Master.");

				/*
				 * Initiate sending of heart beat messages
				 */
				setLastHeartbeatTime(Simulator.getCurrentTime());
				scheduleNewHeartbeatAndMaintenanceOperation();

			} else {
				// Reset peer status
				setOverlayID(VonID.EMPTY_ID);
				setPeerStatus(PeerStatus.TO_JOIN);

				obtainIdOperation();
			}
		}
	}

	protected void obtainIdOperation() {
		if (isNetlayerOnline()) {
			// Obtain an unique OverlayID from the gateway server
			ObtainOlIDOperation op = new ObtainOlIDOperation(this,
					new OperationCallback<VonID>() {

						@Override
						public void calledOperationFailed(Operation<VonID> op) {
							log.error(getTransInfo().getNetId()
									+ " could not obtain a new overlay ID from the gateway server. I retry");
							// retry
							obtainIdOperation();
						}

						@Override
						public void calledOperationSucceeded(Operation<VonID> op) {

							log.debug(getTransInfo().getNetId()
									+ " received new ID (" + op.getResult()
									+ ")");
							/*
							 * Everything was OK, set the new obtained overlay
							 * id
							 */
							setOverlayID(op.getResult());
							resetVoronoiAndInternalState();

							enterTheOverlay();

							/*
							 * Initiate sending of heart beat messages
							 */
							setLastHeartbeatTime(Simulator.getCurrentTime());
							scheduleNewHeartbeatAndMaintenanceOperation();
						}
					});
			op.scheduleImmediately();
		}
	}

	/**
	 * Used to schedule a new HeartbeatAndMaintenanceOperation with a delay that
	 * is given as configuration parameter
	 */
	protected void scheduleNewHeartbeatAndMaintenanceOperation() {
		if (getPeerStatus() == PeerStatus.PRESENT) {

			HeartbeatAndMaintenanceOperation hbOp = new HeartbeatAndMaintenanceOperation(
					this, new OperationCallback() {

						@Override
						public void calledOperationFailed(Operation op) {
							// Nothing to do here
						}

						@Override
						public void calledOperationSucceeded(Operation op) {
							scheduleNewHeartbeatAndMaintenanceOperation();
						}
					});

			hbOp.scheduleWithDelay(VonConfiguration.INTERVAL_BETWEEN_HEARTBEATS);

		}
	}

	/**
	 * Reference to a waiting JoinOperation. Is null, if there is none.
	 */
	private JoinOperation waitingjoinOp;

	/**
	 * Tell the potential waiting JoinOperation that the PeerMsg arrived.
	 * 
	 * --> The JoinOperation is marked as successfully finished with this call.
	 */
	public void peerMsgReceived() {
		if (waitingjoinOp != null) {
			waitingjoinOp.peerMsgReceived();
			waitingjoinOp = null;
		}
	}

	/**
	 * Immediately schedule JoinOperation
	 */
	protected void enterTheOverlay() {
		if (isNetlayerOnline()) {
			waitingjoinOp = new JoinOperation(this, new OperationCallback() {

				@Override
				public void calledOperationFailed(Operation op) {
					if (isNetlayerOnline()) {
						log.error(getVonID()
								+ " could not complete the JoinOperation. Retry it...");

						// retry
						enterTheOverlay();
					} else {
						waitingjoinOp = null;
					}
				}

				@Override
				public void calledOperationSucceeded(Operation op) {

					log.info(getVonID() + " could complete the JoinOperation.");

					/*
					 * Set the peer present and tidy up its voronoi
					 */
					setPeerStatus(PeerStatus.PRESENT);
					removeUnneededContactsFromVoronoi();

					/*
					 * Initiate sending of heart beat messages
					 */
					setLastHeartbeatTime(Simulator.getCurrentTime());
					scheduleNewHeartbeatAndMaintenanceOperation();
				}

			});

			waitingjoinOp.scheduleImmediately();
		}
	}

	private boolean isNetlayerOnline() {
		return getHost().getNetLayer().isOnline();
	}

	public VonID getVonID() {
		return getOverlayID();
	}

	/**
	 * Locally update the position of the node.
	 * 
	 * @param newPos
	 */
	public void updateOwnPosition(Point newPos) {
		setPosition(newPos);
		localVoronoi.update(getVonID(), newPos, getAOI(), Long.MIN_VALUE);
	}

	/**
	 * Resets the local voronoi.
	 * 
	 * This results in a voronoi that only includes the own node and should be
	 * done after the node obtained a valid overlay id.
	 */
	private void resetVoronoiAndInternalState() {
		VonID id = getVonID();

		if (id.compareTo(VonID.EMPTY_ID) != 0) {
			setAOI(VonConfiguration.DEFAULT_AOI_RADIUS);
			localVoronoi = new Voronoi(id);

			localVoronoi.insert(getNodeInfo(), Long.MIN_VALUE);
		}
	}

	public VonNodeInfo getNodeInfo() {
		return new VonNodeInfo(new VonContact(getVonID(), getTransInfo()),
				getPosition(), getAOI());
	}

	public Voronoi getLocalVoronoi() {
		return localVoronoi;
	}

	/**
	 * Removes all contacts from the local voronoi that are not needed anymore.
	 * Not needed contacts are such that are outside the AOI and which are not
	 * enclosing neighbors.
	 * 
	 * @return the removed nodes
	 */
	public LinkedList<VonNodeInfo> removeUnneededContactsFromVoronoi() {

		LinkedList<VonNodeInfo> removedNodes = localVoronoi
				.removeOutsideOfAOIAndNotEnclosing(getVonID(), getAOI());

		log.debug(getVonID()
				+ " removed "
				+ removedNodes.size()
				+ " contacts ("
				+ (removedNodes.size() > 0 ? removedNodes.get(0).getContact()
						.getOverlayID()
						+ (removedNodes.size() > 1 ? ","
								+ removedNodes.get(1).getContact()
										.getOverlayID() : "")
						+ (removedNodes.size() > 2 ? ",.." : "") : "-") + ").");

		return removedNodes;
	}

	public long getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}

	public void setLastHeartbeatTime(long lastHeartbeatTime) {
		this.lastHeartbeatTime = lastHeartbeatTime;
	}

	public VonMessageHandler getMsgHandler() {
		return msgHandler;
	}

	/**
	 * Leave the overlay
	 */
	@Override
	public void leave(boolean crash) {
		inGame = false;
		if (log.isInfoEnabled())
			log.info("The node " + getOverlayID() + (crash ? "crash" : "leave"));

		setPeerStatus(PeerStatus.ABSENT);
		setOverlayID(VonID.EMPTY_ID);
	}

	@Override
	public void join(Point position) {
		inGame = true;
		setPosition(position);
		join();
	}

	@Override
	public List<IDONodeInfo> getNeighborsNodeInfo() {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>();
		for (VonNodeInfo nodeInfo : localVoronoi.getVonNeighbors(
				this.getVonID(), this.getAOI())) {
			result.add(nodeInfo);
		}
		return result;
	}

	@Override
	public void disseminatePosition(Point position) {
		updateOwnPosition(position);
		if (this.getPeerStatus() == PeerStatus.PRESENT) {
			DisseminationOperation dissOp = new DisseminationOperation(this,
					new OperationCallback() {

						@Override
						public void calledOperationFailed(Operation op) {
							// do nothing
						}

						@Override
						public void calledOperationSucceeded(Operation op) {
							// do nothing
						}
					});
			dissOp.scheduleImmediately();
		}
	}

	@Override
	public void setBootstrapManager(BootstrapManager bootstrapManager) {
		if (bootstrapManager instanceof VonBootstrapManager)
			this.bootstrapManager = (VonBootstrapManager) bootstrapManager;
		else
			log.error("Wrong Bootstrap");
	}

	@Override
	public VonBootstrapManager getBootstrapManager() {
		return bootstrapManager;
	}
}
