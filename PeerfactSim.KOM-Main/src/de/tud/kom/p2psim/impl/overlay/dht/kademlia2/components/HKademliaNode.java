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
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node.HierarchyRestrictableNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.HKademliaRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.HierarchyRestrictableRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.HKademliaOperationFactory;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;

/**
 * A node in an overlay network that supports routing in virtual hierarchies
 * ("hierarchical Kademlia"). For details about nodes, see
 * {@link AbstractKademliaNode}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademliaNode<H extends HKademliaOverlayID> extends AbstractKademliaNode<H> implements HierarchyRestrictableNode<H> {

	/**
	 * This node's routing table.
	 */
	private final HierarchyRestrictableRoutingTable<H> routingTable;

	/**
	 * This node's operation factory (used to construct lookup, store etc.
	 * operations)
	 */
	private final HKademliaOperationFactory<H> operationFactory;

	/**
	 * Constructs a new hierarchical Kademlia node.
	 * 
	 * @param myContact
	 *            the KademliaOverlayContact of the new node.
	 * @param messageManager
	 *            the TransLayer of the new node.
	 * @param conf
	 *            a ComponentsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public HKademliaNode(final KademliaOverlayContact<H> myContact, final TransLayer messageManager, final ComponentsConfig conf) {
		super(myContact, messageManager, conf);
		routingTable = new HKademliaRoutingTable<H>(myContact, conf, this);
		operationFactory = new HKademliaOperationFactory<H>(this, conf);

		// construct handler for incoming requests & new neighbours
		final RequestHandler<H> requestHandler = new HRequestHandler<H>(getMessageManager(), this, conf);
		getMessageManager().addTransMsgListener(requestHandler, getPort());
		routingTable.registerProximityListener(requestHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final HKademliaOperationFactory<H> getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final HierarchyRestrictableRoutingTable<H> getKademliaRoutingTable() {
		return this.routingTable;
	}

}
