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


package de.tud.kom.p2psim.impl.overlay.dht.napster.callbacks;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetNode;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ClientLeaveOperationCallback implements OperationCallback<Object> {

	private static final Logger log = SimLogger
			.getLogger(ClientLeaveOperationCallback.class);

	private NapsterClientNode client;

	private int retry;

	public ClientLeaveOperationCallback(NapsterClientNode client, int retry) {
		this.client = client;
		this.retry = retry;
	}

	public void calledOperationFailed(Operation<Object> op) {
		log.info("ClientLeaveOperation with id " + op.getOperationID()
				+ " failed");
		if (retry < 3) {
			retry = retry + 1;
			log.info(retry + ". Retry of ClientLeaveOperation");
			client.leave(new ClientLeaveOperationCallback(client, retry));
		} else {
			retry = 0;
			log.error("------NO CHANCE TO LEAVE------");
		}

	}

	public void calledOperationSucceeded(Operation<Object> op) {
		retry = 0;
		client.setPeerStatus(PeerStatus.ABSENT);

		log.debug("ClientLeaveOperation with id " + op.getOperationID()
				+ " succeeded. The client left the overlay ");

		// reset the setting of the overlays
		SkyNetNode skyNetNode = (SkyNetNode) client.getHost().getOverlay(
				AbstractSkyNetNode.class);
		client.resetNapsterClient();
		skyNetNode.resetSkyNetNode(Simulator.getCurrentTime());

	}

}
