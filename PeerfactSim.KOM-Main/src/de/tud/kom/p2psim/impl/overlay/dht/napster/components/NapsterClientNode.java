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


package de.tud.kom.p2psim.impl.overlay.dht.napster.components;

import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.napster.NapsterNode;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.overlay.dht.DHTListener;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.callbacks.ClientJoinOperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.napster.callbacks.ClientLeaveOperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ClientJoinOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ClientLeaveOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.GetPredecessorOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.NodeLookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ResponsibleForKeyOperation;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ResponsibleForKeyResult;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterClientNode extends AbstractOverlayNode implements
		NapsterNode<NapsterOverlayContact> {

	private static final Logger log = SimLogger
			.getLogger(NapsterClientNode.class);

	private final TransLayer transLayer;

	private final BootstrapManager bootstrap;

	private NapsterOverlayContact ownOverlayContact;

	private NapsterOverlayContact serverOverlayContact;

	private final ClientMessageHandler messageHandler;

	// FIXME Just for workaround. Just used to delete the client at the server
	// if the client goes down. Later, this will be realized, by a normal DHT
	@Deprecated
	private final NapsterServerNode server;

	@Deprecated
	public NapsterServerNode getServer() {
		return server;
	}

	public NapsterClientNode(short port, TransLayer transLayer,
			NapsterServerNode server, BootstrapManager bootstrap,
			NapsterOverlayContact contact) {
		super(contact.getOverlayID(), port);
		this.server = server;
		this.transLayer = transLayer;
		this.bootstrap = bootstrap;
		this.messageHandler = new ClientMessageHandler(this);
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		TransInfo t = transLayer.getLocalTransInfo(getPort());
		// only needed for visualization
		this.ownOverlayContact = contact;

		// this.ownOverlayContact = new NapsterOverlayContact(null, t);
		// REMOVIX
		// setOverlayID(ownOverlayContact.getOverlayID());
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public int join(OperationCallback callback) {
		TransInfo transInfo = transLayer.getLocalTransInfo(getPort());
		ClientJoinOperation op = new ClientJoinOperation(this, transInfo,
				callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int leave(OperationCallback callback) {
		ClientLeaveOperation op = new ClientLeaveOperation(this, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int responsibleForKey(NapsterOverlayID key,
			OperationCallback<ResponsibleForKeyResult> callback) {
		ResponsibleForKeyOperation op = new ResponsibleForKeyOperation(this,
				key, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	public int nodeLookup(OverlayID key,
			OperationCallback<OverlayContact> callback) {
		NodeLookupOperation op = new NodeLookupOperation(this,
				(NapsterOverlayID) key, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	public int getPredecessor(OperationCallback<NapsterOverlayContact> callback) {
		GetPredecessorOperation op = new GetPredecessorOperation(this, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int store(OverlayKey key, DHTObject obj, OperationCallback callback) {
		// not yet needed
		return 0;
	}

	@Override
	public int valueLookup(OverlayKey key, OperationCallback<DHTObject> callback) {
		// not yet needed
		return 0;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			if (getPeerStatus().equals(PeerStatus.ABSENT)) {
				join(new ClientJoinOperationCallback(this, 0));
			} else {
				log.fatal(Simulator.getFormattedTime(Simulator.getCurrentTime())
						+ " NapsterClient "
						+ getHost().getNetLayer().getNetID()
						+ " was never set to ABSENT");
			}
		} else if (ce.isOffline()) {
			if (getPeerStatus().equals(PeerStatus.PRESENT)) {
				setPeerStatus(PeerStatus.ABSENT);
				server.getDHT().removeContact(ownOverlayContact.getOverlayID());
				((SkyNetNode) getHost().getOverlay(SkyNetNode.class))
						.resetSkyNetNode(Simulator.getCurrentTime());
			} else {
				if (getServer().getDHT().containsOverlayID(getOwnOverlayID())) {
					log.warn(Simulator.getFormattedTime(Simulator
							.getCurrentTime())
							+ " Scruffy Access to the DHT to remove "
							+ getOwnOverlayContact().toString()
							+ ". This is needed, since the DHT does not refresh its entries");
					getServer().getDHT().removeContact(getOwnOverlayID());
					setPeerStatus(PeerStatus.ABSENT);
				}
				log.fatal(Simulator.getFormattedTime(Simulator.getCurrentTime())
						+ " NapsterClient "
						+ getOwnOverlayContact().toString()
						+ " was never set to PRESENT");
			}
		}
	}

	public TransInfo getServerTransInfo() {
		List<TransInfo> bootstrapInfo = bootstrap.getBootstrapInfo();
		return bootstrapInfo.isEmpty() ? null : bootstrapInfo.get(0);
	}

	public NapsterOverlayID getOwnOverlayID() {
		return (NapsterOverlayID) getOverlayID();
	}

	public NapsterOverlayContact getOwnOverlayContact() {
		return ownOverlayContact;
	}

	public void setOwnOverlayContact(NapsterOverlayContact ownOverlayContact) {
		this.ownOverlayContact = ownOverlayContact;
	}

	public NapsterOverlayContact getServerOverlayContact() {
		return serverOverlayContact;
	}

	public void setServerOverlayContact(
			NapsterOverlayContact serverOverlayContact) {
		if (this.serverOverlayContact == null) {
			log.info("Created serverOverlayContact "
					+ serverOverlayContact.toString());
			this.serverOverlayContact = serverOverlayContact;
		}
	}

	// flag-methods, checks if the host is actually online or present to execute
	// one of the
	// methods, defined in this class

	public boolean isPresent() {
		return getPeerStatus().equals(PeerStatus.PRESENT);
	}

	// ---------------------------------------------------------------------
	// aux-methods for other operations
	// ---------------------------------------------------------------------

	public void resetNapsterClient() {
		// TODO reset the NapsterClient, if it goes offline or leaves the
		// overlay(=becomining absent)
	}

	// ---------------------------------------------------------------------
	// methods for triggering the operations from the action-file, just for
	// testing
	// ---------------------------------------------------------------------

	public void tryJoin() {
		if (getPeerStatus().equals(PeerStatus.ABSENT)) {
			join(new ClientJoinOperationCallback(this, 0));
		} else {
			log.warn("Client " + ownOverlayContact.toString()
					+ " cannot join, he is already PRESENT");
		}

	}

	public void tryLeave() {
		if (isPresent()) {
			leave(new ClientLeaveOperationCallback(this, 0));
		} else {
			log.warn("Client " + ownOverlayContact.toString()
					+ " cannot leave, he is actually not PRESENT");
		}
	}

	public void tryNodeLookup(String key) {
		if (isPresent()) {
			final NapsterOverlayID keyID = new NapsterOverlayID(new IPv4NetID(
					key));
			nodeLookup(keyID, new OperationCallback<OverlayContact>() {

				private final Logger log = SimLogger
						.getLogger(OperationCallback.class);

				@Override
				public void calledOperationFailed(Operation<OverlayContact> op) {
					log.info("NodeLookupOperation with id "
							+ op.getOperationID() + " failed");
				}

				@Override
				public void calledOperationSucceeded(
						Operation<OverlayContact> op) {
					NapsterOverlayContact contact = (NapsterOverlayContact) op
							.getResult();
					log.info("NodeLookupOperation with id "
							+ op.getOperationID() + " succeeded. The client "
							+ getOwnOverlayContact().toString()
							+ " received the overlayContact "
							+ contact.toString() + " for the OverlayKey "
							+ keyID.getID());
				}
			});
		}
	}

	public void tryGetPredecessor() {
		if (isPresent()) {
			getPredecessor(new OperationCallback<NapsterOverlayContact>() {

				private final Logger log = SimLogger
						.getLogger(OperationCallback.class);

				@Override
				public void calledOperationFailed(
						Operation<NapsterOverlayContact> op) {
					log.info("GetPredecessorOperation with id "
							+ op.getOperationID() + " failed");
				}

				@Override
				public void calledOperationSucceeded(
						Operation<NapsterOverlayContact> op) {
					NapsterOverlayContact contact = op.getResult();
					log.info("GetPredecessorOperation with id "
							+ op.getOperationID() + " succeeded. The client "
							+ getOwnOverlayContact().toString()
							+ " has the Predecessor " + contact.toString());
				}
			});
		}
	}

	public void tryResponsibiltyForKey(String key) {
		if (isPresent()) {
			final NapsterOverlayID keyID = new NapsterOverlayID(new IPv4NetID(
					key));
			responsibleForKey(keyID,
					new OperationCallback<ResponsibleForKeyResult>() {

						private final Logger log = SimLogger
								.getLogger(OperationCallback.class);

						@Override
						public void calledOperationFailed(
								Operation<ResponsibleForKeyResult> op) {
							log.info("NodeLookupOperation with id "
									+ op.getOperationID() + " failed");
						}

						@Override
						public void calledOperationSucceeded(
								Operation<ResponsibleForKeyResult> op) {
							Boolean flag = op.getResult()
									.getResponsibiltyFlag();
							if (flag) {
								log.info("ResponsibiltyForKeyOperation with id "
										+ op.getOperationID()
										+ " succeeded. The client "
										+ getOwnOverlayContact().toString()
										+ " is responsible for the OverlayKey "
										+ keyID.getID());
							} else {
								log.info("ResponsibiltyForKeyOperation with id "
										+ op.getOperationID()
										+ " succeeded. The client "
										+ getOwnOverlayContact().toString()
										+ " is not responsible for the OverlayKey "
										+ keyID.getID());
							}
						}
					});
		}
	}

	@Override
	public int nodeLookup(OverlayKey key,
			OperationCallback<List<NapsterOverlayContact>> callback,
			boolean returnSingleNode) {
		// Not needed TODO: Why??
		return -1;
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return null;
	}

	@Override
	public void registerDHTListener(DHTListener listener) {
		// not yet implemented, as store etc. are not implemented in Napster.
		Log.warn("Napster is not a fully functional DHT. It does not support a DHTListener yet.");
	}

}
