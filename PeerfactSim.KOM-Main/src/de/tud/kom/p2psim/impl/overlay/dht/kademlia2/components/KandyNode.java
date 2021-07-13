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

import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node.VisibilityRestrictableNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.KandyRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.VisibilityRestrictableRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.KandyOperationFactory;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.OperationFactory;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;

/**
 * A node in a Kandy overlay. Kandy is the Kademlia version of Canon and
 * supports somewhat simplified routing in a virtual hierarchy only by modifying
 * the way the routing table is filled. For details about nodes, see
 * {@link AbstractKademliaNode}. This implementation uses visibility restricted
 * lookups wherever it is possible (in both data and node lookups, for
 * instance).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KandyNode<H extends HKademliaOverlayID> extends
		AbstractKademliaNode<H> implements VisibilityRestrictableNode<H> {

	/**
	 * This node's routing table.
	 */
	private final VisibilityRestrictableRoutingTable<H> routingTable;

	/**
	 * This node's operation factory (used to construct lookup, store etc.
	 * operations)
	 */
	private final OperationFactory<H> operationFactory;

	/**
	 * Constructs a new Kandy node.
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KandyNode(final KademliaOverlayContact<H> myContact,
			final TransLayer messageManager, final ComponentsConfig conf) {
		super(myContact, messageManager, conf);
		routingTable = new KandyRoutingTable<H>(myContact, conf, this);
		operationFactory = new KandyOperationFactory<H>(this, conf);

		// construct handler for incoming requests & new neighbours
		final RequestHandler<H> requestHandler = new GenerallyRestrictedRequestHandler<H>(
				getMessageManager(), this, conf);
		getMessageManager().addTransMsgListener(requestHandler, getPort());
		routingTable.registerProximityListener(requestHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final OperationFactory<H> getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final VisibilityRestrictableRoutingTable<H> getKademliaRoutingTable() {
		return this.routingTable;
	}

}
