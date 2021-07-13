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
 * Visitor for Nodes according to the GoF design pattern "Visitor". Can be used
 * to add functionality to the routing table, or implement different variations
 * of methods without having to change the routing table itself.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
interface NodeVisitor<T extends KademliaOverlayID> {

	/**
	 * The callback function if a Node of an "unknown" subtype is visited (for
	 * example in test cases when stubs are used).
	 * 
	 * @param node
	 *            the currently visited Node.
	 */
	public void visit(Node<T> node);

	/**
	 * The callback function if a PseudoRootNode is visited.
	 * 
	 * @param node
	 *            the currently visited PseudoRootNode.
	 */
	public void visit(PseudoRootNode<T> node);

	/**
	 * The callback function if a BranchNode is visited.
	 * 
	 * @param node
	 *            the currently visited BranchNode.
	 */
	public void visit(BranchNode<T> node);

	/**
	 * The callback function if a LeafNode is visited.
	 * 
	 * @param node
	 *            the currently visited LeafNode.
	 */
	public void visit(LeafNode<T> node);

}
