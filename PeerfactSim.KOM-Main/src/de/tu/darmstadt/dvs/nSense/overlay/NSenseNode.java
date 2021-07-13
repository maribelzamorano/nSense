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

package de.tu.darmstadt.dvs.nSense.overlay;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.AbstractIDONodeNDim;
import de.tu.darmstadt.dvs.nSense.application.IDONodeInfoNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.JoinOperation;
import de.tu.darmstadt.dvs.nSense.overlay.operations.RoundOperation;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants;
import de.tu.darmstadt.dvs.nSense.overlay.util.IncomingMessageList;
import de.tu.darmstadt.dvs.nSense.overlay.util.SequenceNumber;
import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * It is the node class for a node in pSense. It controls the operations for
 * join and the execution for the round. Additionally it manages the correctness
 * of the state of a node.
 * 
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 */
public class NSenseNode extends AbstractIDONodeNDim<NSenseID> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(NSenseNode.class);

	private NSenseMessageHandler messageHandler;

	private TransLayer translayer;

	private NSenseBootstrapManager bootstrap;

	private final IncomingMessageList incomingMessageList = new IncomingMessageList();

	private List<Integer> playerActions = new Vector<Integer>();

	private NSense localNSense;

	private SequenceNumber seqNr;

	private NSenseContact contact;

	private long lastJoinAttemptTime = 0;

	private long lastNotAloneInOverlayTime = 0;

	private JoinOperation joinOp = null;

	private RoundOperation roundOp = null;

	/**
	 * Is this node in the game. It is used for the churn.
	 */
	private boolean inGame;

	public NSenseNode(TransLayer translayer, short port,
			NSenseBootstrapManager bootstrap) {
		super(Constants.EMPTY_NSENSE_ID, port,
				Configuration.VISION_RANGE_RADIUS);

		this.translayer = translayer;

		this.messageHandler = new NSenseMessageHandler(this);
		this.getTransLayer().addTransMsgListener(this.messageHandler,
				this.getPort());

		this.setBootstrapManager(bootstrap);

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
				rejoin();
			} else {
				getApplication().stopMovingPlayer();
				if (getPeerStatus() == PeerStatus.PRESENT) {
					bootstrap.unregisterNode(this);
					setPeerStatus(PeerStatus.ABSENT);
				}
				reset();
				if (log.isInfoEnabled())
					log.debug(getOverlayID()
							+ " left the overlay due to churn.");
			}
		}
	}

	private void stopRoundOp() {
		if (roundOp != null)
			roundOp.stopOperation();
	}

	private void stopJoiningOp() {
		if (joinOp != null)
			joinOp.churnDuringJoin();
	}

	/**
	 * Gets the {@link TransInfo} of this node
	 * 
	 * @return The {@link TransInfo} of this node
	 */
	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(this.getPort());
	}

	/**
	 * Gets the incoming message queue back
	 * 
	 * @return the {@link #incomingMessageList}
	 */
	public IncomingMessageList getIncomingMessageList() {
		return incomingMessageList;
	}

	/**
	 * Gets the local nSense object for this node back. If it not exists, then
	 * will be try to create one.
	 * 
	 * @return The local nSense object of this node.
	 */
	public NSense getLocalNSense() {
		if (localNSense == null)
			createLocalNSense();
		return localNSense;
	}

	private void createLocalNSense() {
		if (getOverlayID() == null)
			throw new NullPointerException("nSenseID is null!");
		this.localNSense = new NSense(getOverlayID());
		localNSense.updateLocalNodePosition(getNodeInfo());
	}

	/**
	 * Gets the sequence number of this node return.
	 * 
	 * @return The actually sequence number of this node.
	 */
	public SequenceNumber getSeqNr() {
		return seqNr;
	}

	/**
	 * Sets the sequence number of this node
	 * 
	 * @param seqNr
	 *            The new sequence number of this node.
	 */
	protected void setSeqNr(SequenceNumber seqNr) {
		this.seqNr = seqNr;
	}

	/**
	 * Increment the sequence number at one
	 */
	public void incSeqNr() {
		this.seqNr = seqNr.incSeqNr();
	}

	/**
	 * Gets the contact information for this node return.
	 * 
	 * @return the conctact information for this node.
	 */
	public NSenseContact getContact() {
		return contact;
	}

	/**
	 * Sets the nSenseID of this node. Additionally it creates the new
	 * contactInformation.
	 * 
	 * @param id
	 *            The new nSenseID of this node.
	 */
	public void setOverlayID(NSenseID id) {
		super.setOverlayID(id);
		createContact();
	}

	/**
	 * Create the contact information for this node with the stored pSenseID and
	 * transLayer info.
	 */
	private void createContact() {
		this.contact = new NSenseContact(getOverlayID(), getTransInfo());
	}

	/**
	 * Update the position of this node and update the node info in
	 * {@link NSense}.
	 * 
	 * @param newPosition
	 *            The new position for this node.
	 */
	public void disseminatePosition(VectorN newPosition) {
		super.setPosition(newPosition);
		if (localNSense != null) {
			localNSense.updateLocalNodePosition(getNodeInfo());
		} else
			createLocalNSense();
	}

	public void setPositionDimensions(VectorN newPosition) {
		super.setPositionDimensions(newPosition);
		if (localNSense != null) {
			localNSense.updateLocalNodePosition(getNodeInfo());
		} else
			createLocalNSense();
	}

	/**
	 * Gets a new NodeInfo of the node back.
	 * 
	 * @return A nodeInfo for this node.
	 */
	public NSenseNodeInfo getNodeInfo() {
		return new NSenseNodeInfo(Configuration.VISION_RANGE_RADIUS,
				getPosition(), getPositionDimensions(), getContact(),
				getSeqNr(), new Vector<NSenseID>(), Configuration.MAXIMAL_HOP);
	}

	public long getLastNotAloneInOverlayTime() {
		return lastNotAloneInOverlayTime;
	}

	public void setLastNotAloneInOverlayTime(long lastNotAloneInOverlayTime) {
		this.lastNotAloneInOverlayTime = lastNotAloneInOverlayTime;
	}

	/**
	 * Gets a status about the incoming messages for every round.
	 * 
	 * @return <code>true</code>, if a message is arrived, otherwise
	 *         <code>false</code>.
	 */
	public boolean isAMessageArrived() {
		return !incomingMessageList.isEmpty();
	}

	public void setLastJoinAttempt(long time) {
		this.lastJoinAttemptTime = time;
	}

	public long getLastJoinAttempt() {
		return lastJoinAttemptTime;
	}

	public void addPlayerAction(int payload) {
		playerActions.add(payload);
	}

	public void clearPlayerActions() {
		playerActions.clear();
	}

	public List<Integer> getPlayerActions() {
		return playerActions;
	}

	private void join() {
		inGame = true;
		if (isNetlayerOnline()) {
			reset();

			NSenseID newID = bootstrap.getNextUniquePSenseID();
			setOverlayID(newID);
			createLocalNSense();
			joinOp();
		}
	}

	private void rejoin() {
		if (getPeerStatus() == PeerStatus.PRESENT) {
			setPeerStatus(PeerStatus.ABSENT);
			stopRoundOp();
			bootstrap.unregisterNode(this);
		} else if (getPeerStatus() == PeerStatus.TO_JOIN) {
			setPeerStatus(PeerStatus.ABSENT);
			stopJoiningOp();
		}
		join();
		if (log.isDebugEnabled())
			log.debug(getOverlayID() + " initiated rejoin.");
	}

	private void reset() {
		if (getPeerStatus() == PeerStatus.PRESENT)
			bootstrap.unregisterNode(this);
		setPeerStatus(PeerStatus.ABSENT);
		stopRoundOp();
		stopJoiningOp();
		setOverlayID(Constants.EMPTY_NSENSE_ID);
		setSeqNr(new SequenceNumber((short) 0));
		localNSense = null;
		clearIncomingMessageList();
		clearPlayerActions();
	}

	private void joinOp() {
		joinOp = new JoinOperation(this, new OperationCallback<Boolean>() {

			@Override
			public void calledOperationFailed(Operation<Boolean> op) {
				log.info(getOverlayID()
						+ " could not complete the JoinOperation.");
				joinOp = null;
			}

			@Override
			public void calledOperationSucceeded(Operation<Boolean> op) {
				// if op.getResult() == true, then is the join complete.
				if (op.getResult()) {
					setLastNotAloneInOverlayTime(Simulator.getCurrentTime());
					roundOp();
					joinOp = null;
				} else {
					// Retry
					joinOp();
				}
			}
		});
		joinOp.scheduleWithDelay(Configuration.OP_WAIT_FOR_STATUS_OF_JOIN);
	}

	private void roundOp() {
		if (getPeerStatus() == PeerStatus.PRESENT) {
			roundOp = new RoundOperation(this,
					new OperationCallback<Boolean>() {

						@Override
						public void calledOperationFailed(Operation<Boolean> op) {
							// result is a boolean for "connectedWithOverlay"
							if (op.getResult() != null && !op.getResult()) {
								// do a rejoin, because the node think, he is
								// disjoined from Overlay
								rejoin();
							} else {
								roundOp = null;
							}
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Boolean> op) {
							roundOp();
						}
					});
			roundOp.scheduleWithDelay(getTimeBetweenRounds());
		}
	}

	private long getTimeBetweenRounds() {
		long roundTime = 0;
		if (Configuration.FIT_TIME_BETWEEN_ROUNDS) {
			double uploadRate = getTransLayer().getHost().getNetLayer()
					.getMaxUploadBandwidth();
			long neededTime = (long) Math.ceil(Configuration.ROUND_BYTE_LIMIT
					/ uploadRate * 1000.0 * Simulator.MILLISECOND_UNIT);
			roundTime = Math.max(neededTime, Configuration.TIME_BETWEEN_ROUNDS);
		} else {
			roundTime = Configuration.TIME_BETWEEN_ROUNDS;

		}
		if (log.isDebugEnabled())
			log.debug("The Time between two rounds is: " + roundTime
					+ "ms for the node: " + getOverlayID());
		return roundTime;
	}

	private boolean isNetlayerOnline() {
		return getHost().getNetLayer().isOnline();
	}

	public void clearIncomingMessageList() {
		incomingMessageList.clear();
	}

	/**
	 * Initiate joining
	 */
	@Override
	public void join(VectorN position) {
		setPosition(position);
		join();
	}

	/**
	 * Leave the overlay
	 */
	@Override
	public void leave(boolean crash) {
		inGame = false;
		if (getPeerStatus() == PeerStatus.PRESENT) {
			bootstrap.unregisterNode(this);
		}
		if (log.isInfoEnabled())
			log.info("The node " + getOverlayID() + (crash ? "crash" : "leave"));
		setPeerStatus(PeerStatus.ABSENT);
		reset();
	}

	@Override
	public List<IDONodeInfoNDim> getNeighborsNodeInfo() {
		List<IDONodeInfoNDim> result = new Vector<IDONodeInfoNDim>();
		// for (NSenseID id : localNSense.getSensorNodes()) {
		// if (id != null)
		// if (!result.contains(localNSense.getNodeInfo(id)))
		// result.add(localNSense.getNodeInfo(id));
		// }
		for (NSenseID id : localNSense.getNearNodes()) {
			if (id != null)
				if (!result.contains(localNSense.getNodeInfo(id)))
					result.add(localNSense.getNodeInfo(id));
		}
		return result;
	}

	@Override
	public NSenseBootstrapManager getBootstrapManager() {
		return bootstrap;
	}

	@Override
	public void setBootstrapManager(NSenseBootstrapManager bootstrapManager) {
		if (bootstrapManager instanceof NSenseBootstrapManager)
			this.bootstrap = bootstrapManager;
		else
			log.error("Wrong Bootstrap");
	}

}
