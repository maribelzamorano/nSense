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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.setup;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.KandyNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Factory for KandyNodes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KandyNodeFactory extends HAbstractNodeFactory {

	private final static Logger log = SimLogger
			.getLogger(KandyNodeFactory.class);

	public KandyNodeFactory() {
		log.info("Using KandyNodeFactory.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected KandyNode<HKademliaOverlayID> buildHierarchicalNode(
			final HKademliaOverlayID id, final short port,
			final TransLayer msgMgr) {
		final KademliaOverlayContact<HKademliaOverlayID> itsContact = new KademliaOverlayContact<HKademliaOverlayID>(
				id, msgMgr.getLocalTransInfo(port));
		final KandyNode<HKademliaOverlayID> newNode = new KandyNode<HKademliaOverlayID>(
				itsContact, msgMgr, config);
		return newNode;
	}

}
