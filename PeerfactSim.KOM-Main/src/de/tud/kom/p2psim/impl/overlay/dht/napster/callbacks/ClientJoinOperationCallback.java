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
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.ClientJoinOperation;
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
public class ClientJoinOperationCallback implements
		OperationCallback<NapsterOverlayID> {

	private static final Logger log = SimLogger
			.getLogger(ClientJoinOperationCallback.class);

	private NapsterClientNode client;

	private int retry;

	public ClientJoinOperationCallback(NapsterClientNode client, int retry) {
		this.client = client;
		this.retry = retry;
	}

	public void calledOperationFailed(Operation<NapsterOverlayID> op) {
		log.info(retry + ". ClientJoinOperation with id " + op.getOperationID()
				+ " failed");

		// FIXME Just for workaround. the outer if-else-block is only a
		// workaround, since the server does not refresh its entries
		if (client.getHost().getNetLayer().isOffline()) {
			log
					.warn(Simulator
							.getFormattedTime(Simulator.getCurrentTime())
							+ " Scruffy Access to the DHT to remove "
							+ client.getOwnOverlayContact().toString()
							+ ". This is needed, since the DHT does not refresh its entries");
			client.getServer().getDHT().removeContact(
					client.getHost().getNetLayer().getNetID());
		} else {
			if (retry < 3) {
				retry = retry + 1;
				log.info(retry + ". Retry of ClientJoinOperation");
				((ClientJoinOperation) op).execute();
			} else {
				log.error(Simulator
						.getFormattedTime(Simulator.getCurrentTime())
						+ " "
						+ client.getOwnOverlayContact().getTransInfo()
								.getNetId().toString()
						+ " ----NO CHANCE TO JOIN, so trying a rejoin----");
				client.tryJoin();
			}
		}
	}

	public void calledOperationSucceeded(Operation<NapsterOverlayID> op) {
		NapsterOverlayID oid = op.getResult();

		// initialize everything at the NapsterClientNode, what is needed
		client.setOverlayID(oid);
		client.getOwnOverlayContact().setOverlayID(oid);
		client.setPeerStatus(PeerStatus.PRESENT);

		// Create the serverOverlayContact
		NapsterOverlayID serverOID = ((ClientJoinOperation) op).getReplyMsg()
				.getSender();
		NapsterOverlayContact serverOverlayContact = new NapsterOverlayContact(
				serverOID, client.getServerTransInfo());
		client.setServerOverlayContact(serverOverlayContact);

		// start the SkyNetNode
		long time = Simulator.getCurrentTime();
		((SkyNetNode) client.getHost().getOverlay(AbstractSkyNetNode.class))
				.startSkyNetNode(time);

		// Create the SkyNetID and start the timers

		/*
		 * AddressResolutionImpl ari = AddressResolutionImpl .getInstance((int)
		 * SkyNetConstants.OVERLAY_ID_SIZE); SkyNetBigDecID skyNetID =
		 * ari.getSkyNetID(oid); AbstractSkyNetNode skyNetNode =
		 * (AbstractSkyNetNode) client.getHost()
		 * .getOverlay(AbstractSkyNetNode.class);
		 * skyNetNode.getSkyNetNodeInfo().setSkyNetID(skyNetID);
		 * skyNetNode.getSkyNetMessageHandler().setTryingJoin(false);
		 * log.info(skyNetNode.getSkyNetNodeInfo().toString()); long time =
		 * Simulator.getCurrentTime(); ((SkyNetNode)
		 * skyNetNode).setPresentTime(time); // Schedule next metric-update
		 * skyNetNode.getMetricUpdateStrategy().setSendingTime(time); long
		 * metricsTime = time +
		 * (skyNetNode.getMetricUpdateStrategy().getUpdateInterval());
		 * Simulator.scheduleEvent(new SkyNetEventObject(
		 * SkyNetEventType.METRICS_UPDATE, time), metricsTime, skyNetNode,
		 * null); // Schedule next attribute-update
		 * skyNetNode.getAttributeUpdateStrategy().setSendingTime(time); long
		 * attributeTime = time +
		 * (skyNetNode.getAttributeUpdateStrategy().getUpdateInterval());
		 * Simulator.scheduleEvent(new SkyNetEventObject(
		 * SkyNetEventType.ATTRIBUTE_UPDATE, time), attributeTime, skyNetNode,
		 * null); // other inits ((SkyNetHostProperties)
		 * skyNetNode.getHost().getProperties()).init();
		 */

	}
}
