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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterDHTImpl;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ServerJoinOperation;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class NapsterServerNode extends AbstractOverlayNode {

	private static final Logger log = SimLogger
			.getLogger(NapsterServerNode.class);

	private TransLayer transLayer;

	private NapsterOverlayContact ownOverlayContact;

	private ServerMessageHandler messageHandler;

	private NapsterDHTImpl dht;

	private NapsterBootstrapManager bootstrap;

	public NapsterServerNode(OverlayID peerId, short port,
			TransLayer transLayer, NapsterBootstrapManager bootstrap) {
		super(peerId, port);
		this.transLayer = transLayer;
		this.bootstrap = bootstrap;
		bootstrap.registerNode(this);
		messageHandler = new ServerMessageHandler(this);
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		dht = new NapsterDHTImpl();
		ownOverlayContact = new NapsterOverlayContact(
				(NapsterOverlayID) peerId, getTransLayer().getLocalTransInfo(
						port));
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	public void connectivityChanged(ConnectivityEvent ce) {
		// Server is intented not to go down, because this would break the whole
		// system and stop the simulation
	}

	/*
	 * public NapsterOverlayID generateNapsterOverlayID(IPv4NetID ip) { return
	 * new NapsterOverlayID(Math.abs(ip.toString().hashCode())); // return new
	 * NapsterOverlayID(ip.getID().longValue()); }
	 */

	public NapsterDHTImpl getDHT() {
		return dht;
	}

	public int join(OperationCallback callback) {
		ServerJoinOperation serverJoinOperation = new ServerJoinOperation(this,
				callback);
		serverJoinOperation.scheduleImmediately();
		return serverJoinOperation.getOperationID();
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return null;
	}

}
