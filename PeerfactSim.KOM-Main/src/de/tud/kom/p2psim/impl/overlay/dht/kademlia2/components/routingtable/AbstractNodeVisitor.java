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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;

/**
 * This class implements the visit method for PseudoRootNode, because requests
 * for that type are always forwarded to the actual root node stored in the
 * PseudoRootNode.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
abstract class AbstractNodeVisitor<T extends KademliaOverlayID> implements
		NodeVisitor<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final Node<T> node) {
		throw new UnsupportedOperationException("This type of node ("
				+ node.getClass() + ") is not supported by this visitor.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void visit(final PseudoRootNode<T> node) {
		// The pseudo root forwards all requests to the actual root node.
		node.root.accept(this);
	}

}
