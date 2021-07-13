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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.util.toolkits.BigIntegerHelpers;

/**
 * A branch node that has exactly 2^
 * {@link RoutingTableConfig#getRoutingTreeOrder()} children. Does not store
 * contacts itself, but delegates all calls to these subtrees.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
class BranchNode<T extends KademliaOverlayID> extends AbstractNode<T> implements
		ParentNode<T> {

	/**
	 * Map of children of this branch node. The key is a BigInteger with the
	 * {@link RoutingTableConfig#getRoutingTreeOrder()} bits suffix of each of
	 * the children's prefix. See
	 * {@link AbstractRoutingTreeNode#getDiscriminantPrefixBits()} for details.
	 */
	final protected Map<BigInteger, Node<T>> children;

	/**
	 * Creates a new branch node with initially no children. These have to
	 * register themselves via {@link BranchNode#registerNewChild(Node)}.
	 * 
	 * This node will register itself as a child of <code>parent</code> and use
	 * <code>prefix</code> as prefix.
	 * 
	 * @param prefix
	 *            the prefix that all children of this node have in common.
	 * @param parent
	 *            the parent of this node.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	protected BranchNode(final BigInteger prefix, final ParentNode<T> parent,
			final RoutingTableConfig conf) {
		super(prefix, parent, conf);
		children = new HashMap<BigInteger, Node<T>>(config
				.getRoutingTreeOrder(), 1.0f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void accept(final NodeVisitor<T> visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void registerNewChild(final Node<T> child) {
		children.put(getDiscriminantPrefixBits(child.getPrefix()), child);
	}

	/**
	 * Returns the child that is responsible for identifier <code>id</code>.
	 * 
	 * @param id
	 *            the BigInteger representation of the identifier for which the
	 *            responsible child is looked for.
	 * @return the child responsible for <code>id</code>.
	 */
	protected final Node<T> getResponsibleChild(final BigInteger id) {
		return children.get(getDiscriminantIDBits(id));
	}

	/**
	 * Finds the BTREE={@link RoutingTableConfig#getRoutingTreeOrder()} bits of
	 * the identifier <code>id</code> that are considered at this level (for
	 * example to select the child that is responsible for IDs with these BTREE
	 * bits).
	 * 
	 * For example, for BTREE=2, level=1 and key=101100, the wanted bits are 11.
	 * 
	 * @param id
	 *            the BigInteger value of the identifier from which the BTREE
	 *            bits used to find the appropriate child node at this level are
	 *            to be extracted.
	 * @return a BigInteger with the interesting bits (shifted to the right).
	 */
	protected final BigInteger getDiscriminantIDBits(final BigInteger idBigInt) {
		return BigIntegerHelpers.getNthBitstring(idBigInt, (config
				.getIDLength() / config.getRoutingTreeOrder())
				- getLevel() - 1, config.getRoutingTreeOrder());
	}

	/**
	 * Calculates the bits of the given prefix that differentiate a node from
	 * any other node at the same level in the same subtree.
	 * 
	 * @param nodePrefix
	 *            a BigInteger containing the prefix of the node.
	 * @return a BigInteger with the
	 *         {@link RoutingTableConfig#getRoutingTreeOrder()} rightmost bits
	 *         of the given prefix, that is the bits of this node's prefix that
	 *         differ from any other node's prefix at the same level in a common
	 *         subtree.
	 */
	protected final BigInteger getDiscriminantPrefixBits(
			final BigInteger nodePrefix) {
		return BigIntegerHelpers.getNRightmostBits(nodePrefix, config
				.getRoutingTreeOrder());
	}

	@Override
	public Set<KademliaOverlayContact<T>> getAllSubContacts() {
		Set<KademliaOverlayContact<T>> result = new HashSet<KademliaOverlayContact<T>>();
		for (Node<T> nd : children.values()) result.addAll(nd.getAllSubContacts());
		return result;
	}

}
