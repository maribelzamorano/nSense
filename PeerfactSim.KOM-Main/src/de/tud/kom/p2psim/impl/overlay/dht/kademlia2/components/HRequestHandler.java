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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components;

import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node.HierarchyRestrictableNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.HKClosestNodesLookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.KademliaMsg;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.NodeListMsg;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;

/**
 * A handler for incoming (unsolicited) messages/requests that is able to answer
 * cluster-restricted lookup requests.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
class HRequestHandler<H extends HKademliaOverlayID> extends RequestHandler<H> {

	/**
	 * The HierarchicalNode that owns this message handler.
	 */
	private final HierarchyRestrictableNode<H> myNode;

	/**
	 * Constructs a new hierarchical message/request handler that is able to
	 * answer cluster-restricted lookup requests. It has to be manually
	 * registered as ProximityListener of <code>myNode</code>'s routing table
	 * and as a TransMessageListener of <code>manager</code>.
	 * 
	 * @param manager
	 *            the TransLayer used to reply to messages.
	 * @param myNode
	 *            the HierarchicalNode that owns this message handler.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HRequestHandler(final TransLayer manager, final HierarchyRestrictableNode<H> myNode, final ComponentsConfig conf) {
		super(manager, myNode, conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected final void processMsg(final TransMsgEvent tme) {
		final Message msg = tme.getPayload();
		final TransInfo senderAddr;

		if (msg instanceof HKClosestNodesLookupMsg) {
			senderAddr = tme.getSenderTransInfo();
			myNode.addSenderToRoutingTable((KademliaMsg<H>) msg, senderAddr);
			processClusterRestrictedNodeLookup((HKClosestNodesLookupMsg<H>) msg, tme);
		} else {
			// other kinds of messages handled in superclass
			super.processMsg(tme);
		}
	}

	/**
	 * Handles reception of a cluster-restricted node lookup message.
	 * 
	 * @param msg
	 *            the HKClosestNodesLookupMsg that has been received. (This
	 *            message is a copy of the message contained in
	 *            <code>receiveEvent</code>.)
	 * @param receiveEvent
	 *            the TransMsgEvent that occurred at reception of the message
	 *            that is to be handled here.
	 */
	private final void processClusterRestrictedNodeLookup(final HKClosestNodesLookupMsg<H> msg, final TransMsgEvent receiveEvent) {
		final KademliaOverlayKey key = msg.getNodeKey();
		final int minClusterDepth = msg.getMinClusterDepth();
		final H clusterRef = msg.getSender();
		int localBucketSize = ((AbstractKademliaNode<H>) myNode).getLocalConfig().getBucketSize();
		final Set<KademliaOverlayContact<H>> neighbours = myNode.getKademliaRoutingTable().localLookup(key, localBucketSize, minClusterDepth, clusterRef);
		final NodeListMsg<H> reply = new NodeListMsg<H>(myNode.getTypedOverlayID(), msg.getSender(), neighbours, msg.getReason(), config);
		transLayer.sendReply(reply, receiveEvent, myNode.getPort(), TransProtocol.UDP);
	}

}
