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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.overlay.ido.AbstractIDONode;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.operation.CleanOperation;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.operation.HeartbeatOperation;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.operation.SubscriptionOperation;
import de.tud.kom.p2psim.impl.service.mercury.MercuryService;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The main component for the mercury IDO. It provides the interface for the IDO
 * application. It provides the join and leave to the overlay.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/28/2011
 */
public class MercuryIDONode extends AbstractIDONode<OverlayID> implements
		IDONode {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(MercuryIDONode.class);

	/**
	 * The service which is using
	 */
	private MercuryService service;

	/**
	 * The trans layer of the host
	 */
	private TransLayer translayer;

	/**
	 * The storage, which store the received neighbors
	 */
	private NeighborStorage storage;

	/**
	 * Flag to prevent automatically rejoins after churn related online events,
	 * if the initial node join was not initiated yet (using the join method) or
	 * the node has intentionally left the overlay (using the leave method).
	 */
	protected boolean rejoinOnOnlineEvent = false;

	/**
	 * The time, which is send the last position update
	 */
	private long lastHeartbeat = 0;

	/**
	 * The actually heartbeat operation
	 */
	private HeartbeatOperation heartbeatOperation;

	/**
	 * the actually subscription operation, which update the subscription
	 */
	private SubscriptionOperation updateSubscriptionOperation;

	/**
	 * Sets the given parameters. Additionally it fetches from the host the
	 * service, creates a {@link MercuryIDOListener} and add this listener to
	 * the {@link MercuryService}.
	 * 
	 * @param peerId
	 *            The overlayID for this node.
	 * @param host
	 *            The host of this node.
	 */
	protected MercuryIDONode(OverlayID peerId, Host host) {
		super(peerId, (short) 0, MercuryIDOConfiguration.AOI);

		this.setHost(host);

		this.translayer = this.getHost().getTransLayer();

		this.storage = new NeighborStorage(this.getOverlayID());

		this.service = this.getHost().getComponent(MercuryService.class);

		this.service.addListener(new MercuryIDOListener(this));
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline() && rejoinOnOnlineEvent) {
			// rejoin
			if (getPosition() != null)
				join(getPosition());
			else {
				// position should be set, because it is set by the method join
				// AND it should not be reset.
				log.error("Try a rejoin, but the position is null! The position should not be null by a rejoin");
			}

		} else {
			this.service.stop(Operations.EMPTY_CALLBACK);
			reset();
		}

	}

	/**
	 * Resets the node. It stops the operations and set the PeerStatus to
	 * absent. Additionally resets the storage and the lastHeartbeat time.
	 */
	public void reset() {
		if (heartbeatOperation != null)
			heartbeatOperation.stop();
		if (updateSubscriptionOperation != null)
			updateSubscriptionOperation.stop();
		setPeerStatus(PeerStatus.ABSENT);
		this.storage = new NeighborStorage(this.getOverlayID());
		lastHeartbeat = 0;
	}

	@Override
	public void leave(boolean crash) {
		rejoinOnOnlineEvent = false;
		service.stop(Operations.EMPTY_CALLBACK);
		reset();
	}

	@Override
	public void join(Point position) {
		setPosition(position);
		rejoinOnOnlineEvent = true;
		setPeerStatus(PeerStatus.TO_JOIN);
		service.start(new OperationCallback<Object>() {

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				// TODO: endless recursion?
				if (rejoinOnOnlineEvent
						&& getPeerStatus().equals(PeerStatus.TO_JOIN)
						&& getHost().getNetLayer().isOnline()) {
					join(getPosition());
					log.info("Do rejoin in MercuryIDONode");
					log.error("join Failed!");
				}
			}

			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				if (getPeerStatus().equals(PeerStatus.TO_JOIN)) {
					setPeerStatus(PeerStatus.PRESENT);
					heartbeatOperation();
					updateSubscriptionOperation();
				}
			}
		});
	}

	/**
	 * 
	 * Side Condition: It sets the position, and removes information from the
	 * neighborStorage, which are expired or out of range.
	 * 
	 * @see de.tud.kom.p2psim.api.overlay.IDONode#disseminatePosition(java.awt.Point)
	 * 
	 */
	@Override
	public void disseminatePosition(Point position) {

		setPosition(position);

		List<IMercuryAttribute> attributes = new Vector<IMercuryAttribute>();
		attributes.add(this.getService().createAttribute(
				this.getService().getAttributeByName("x"), position.x));
		attributes.add(this.getService().createAttribute(
				this.getService().getAttributeByName("y"), position.y));

		MercuryIDOPayload payload = new MercuryIDOPayload(getOverlayID(),
				getAOI());

		// System.err.println(Simulator.getFormattedTime(Simulator
		// .getCurrentTime())
		// + " new Publication "
		// + attributes.toString());
		getService().publish(attributes, payload);

		setLastHeartbeat(Simulator.getCurrentTime());

		// clean the storage
		new CleanOperation(this).scheduleImmediately();
	}

	@Override
	public List<IDONodeInfo> getNeighborsNodeInfo() {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>(
				storage.getAllNeighbors());
		return result;
	}

	@Override
	public BootstrapManager getBootstrapManager() {
		// has no Bootstrap
		return null;
	}

	@Override
	public void setBootstrapManager(BootstrapManager bootstrapManager) {
		// do nothing, because it exists no bootstrap
	}

	@Override
	public TransLayer getTransLayer() {
		return translayer;
	}

	/**
	 * A heartbeat operation which is repeating, if the node is online. It sends
	 * a position update, if the node hasn't send a position update in a
	 * specific time.
	 */
	protected void heartbeatOperation() {
		HeartbeatOperation op = new HeartbeatOperation(this,
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
		op.scheduleWithDelay(MercuryIDOConfiguration.TIME_BETWEEN_HEARTBEAT_OPERATION);
		heartbeatOperation = op;
	}

	/**
	 * This is a repeating operation, if the node is online. It generates in
	 * specific time intervals a subscription.
	 */
	protected void updateSubscriptionOperation() {
		SubscriptionOperation op = new SubscriptionOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// do nothing because it is stopped
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						updateSubscriptionOperation();
					}
				});
		op.scheduleWithDelay(MercuryIDOConfiguration.TIME_BETWEEN_SUBSCRIPTION_UPDATE);
		updateSubscriptionOperation = op;
	}

	/**
	 * Gets the last heartbeat time.
	 * 
	 * @return The last time, which is send a heartbeat.
	 */
	public long getLastHeartbeat() {
		return lastHeartbeat;
	}

	/**
	 * Sets the last heartbeat time.
	 * 
	 * @param currentTime
	 *            The time of the last heartbeat.
	 */
	public void setLastHeartbeat(long currentTime) {
		this.lastHeartbeat = currentTime;
	}

	/**
	 * Gets the mercury service.
	 * 
	 * @return The mercury service for this node.
	 */
	public MercuryService getService() {
		return service;
	}

	/**
	 * Gets the storage of this node.
	 * 
	 * @return The neighbor storage of this node.
	 */
	public NeighborStorage getStorage() {
		return storage;
	}

}
