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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.AbstractKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;

/**
 * KademliaRoutingTable for Standard-Kademlia. Permits to store, lookup and mark
 * contacts as unresponsive.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaRoutingTable<T extends KademliaOverlayID> implements
		RoutingTable<T> {

	/**
	 * Static instance of a Set returned to clients with lookup results. Shared
	 * among all routing table instances!
	 */
	protected static final Set sharedResultSet = new HashSet();

	/**
	 * (Pseudo) root of the routing tree.
	 */
	protected final PseudoRootNode<T> pseudoRoot;

	/**
	 * Handler for changes to the set of the K closest nodes around own ID.
	 */
	protected final ProximityHandler<T> proxHandler;

	/**
	 * Configuration values ("constants").
	 */
	protected final RoutingTableConfig config;

	/**
	 * Constructs a new routing table (according to standard Kademlia) with the
	 * given contact information of the owning node (it will be inserted into
	 * the routing table).
	 * 
	 * @param ownContact
	 *            the KademliaOverlayContact that identifies the node that this
	 *            routing table belongs to.
	 * @param conf
	 *            a RoutingTableConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public KademliaRoutingTable(final KademliaOverlayContact<T> ownContact, final RoutingTableConfig conf, AbstractKademliaNode<T> owningOverlayNode) {
		this.config = conf;
		pseudoRoot = new PseudoRootNode<T>(ownContact.getOverlayID(), config, owningOverlayNode);
		proxHandler = new ProximityHandler<T>(ownContact.getOverlayID(), conf);
		addContact(ownContact);
	}

	public KademliaRoutingTable(final KademliaOverlayContact<T> ownContact, final RoutingTableConfig conf) {
		this(ownContact, conf, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContact(final KademliaOverlayContact<T> contact) {
		final AddNodeVisitor<T> addVis = AddNodeVisitor.getAddNodeVisitor(
				contact, null, proxHandler, config);
		pseudoRoot.accept(addVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localLookup(final KademliaOverlayKey id, final int num,
			final Collection<KademliaOverlayContact<T>> result) {
		result.clear();
		final GenericLookupNodeVisitor<T> lookupVis = GenericLookupNodeVisitor
				.getGenericLookupNodeVisitor(id, num, null, result);
		pseudoRoot.accept(lookupVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<KademliaOverlayContact<T>> localLookup(
			final KademliaOverlayKey id, final int num) {
		localLookup(id, num, sharedResultSet);
		return sharedResultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markUnresponsiveContact(final T id) {
		final MarkUnresponsiveNodeVisitor<T> unresVis = MarkUnresponsiveNodeVisitor
				.getMarkUnresponsiveNodeVisitor(id, null, proxHandler, config);
		pseudoRoot.accept(unresVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setLastLookupTime(final KademliaOverlayKey key,
			final long time) {
		final SetLastLookupTimeNodeVisitor<T> setVis = SetLastLookupTimeNodeVisitor
				.getSetLastLookupTimeNodeVisitor(key, time);
		pseudoRoot.accept(setVis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Map<KademliaOverlayKey, Integer> getRefreshBuckets(
			final long notLookedUpSince) {
		final Map<KademliaOverlayKey, Integer> result = new HashMap<KademliaOverlayKey, Integer>();
		final RefreshNodeVisitor<T> refVis = RefreshNodeVisitor
				.getRefreshNodeVisitor(notLookedUpSince, result, config);
		pseudoRoot.accept(refVis);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void registerProximityListener(
			final de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.routingtable.RoutingTable.ProximityListener<T> newListener) {
		proxHandler.registerProximityListener(newListener);
	}

	@Override
	public Collection<OverlayContact> getNeighbors() {
		Node<T> rootNode = pseudoRoot.getRoot();
		Collection<? extends OverlayContact> cs = rootNode.getAllSubContacts();
		return Collections.unmodifiableCollection(cs);
	}
	
}
