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

import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node.VisibilityRestrictableNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * A request handler that answers both data lookups and node (k closest nodes)
 * lookups with a visibility restricted set of nodes from the local routing
 * table.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class GenerallyRestrictedRequestHandler<H extends HKademliaOverlayID> extends RequestHandler<H> {

	/**
	 * The node that owns this request handler.
	 */
	private final VisibilityRestrictableNode<H> myNode;

	/**
	 * Constructs a new message handler that restricts results for both data and
	 * node lookups. It has to be manually registered as ProximityListener of
	 * <code>myNode</code>'s routing table and as a TransMessageListener of
	 * <code>manager</code>.
	 * 
	 * @param manager
	 *            the TransLayer used to reply to messages.
	 * @param myNode
	 *            the VisibilityRestrictableNode that owns this message handler.
	 * @param conf
	 *            an ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public GenerallyRestrictedRequestHandler(final TransLayer manager, final VisibilityRestrictableNode<H> myNode, ComponentsConfig conf) {
		super(manager, myNode, conf);
		this.myNode = myNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<H>> nodeLookupForNode(final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<H>) myNode).getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable().visibilityRestrictedLocalLookup(key, localBucketSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Set<KademliaOverlayContact<H>> nodeLookupForData(final KademliaOverlayKey key) {
		int localBucketSize = ((AbstractKademliaNode<H>) myNode).getLocalConfig().getBucketSize();

		return myNode.getKademliaRoutingTable().visibilityRestrictedLocalLookup(key, localBucketSize);
	}
}
