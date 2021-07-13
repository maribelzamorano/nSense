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

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.KBRKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.Node;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class KBRKademliaNodeFactory extends AbstractNodeFactory {

	private final static Logger log = SimLogger
			.getLogger(KademliaNodeFactory.class);

	public KBRKademliaNodeFactory() {
		log.info("Using KBRKademliaNodeFactory.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Node<HKademliaOverlayID> buildNode(
			final HKademliaOverlayID id, final short port, final Host host) {

		final TransLayer msgMgr = host.getTransLayer();
		final KademliaOverlayContact<HKademliaOverlayID> itsContact = new KademliaOverlayContact<HKademliaOverlayID>(
				id, msgMgr.getLocalTransInfo(port));
		final KBRKademliaNode<HKademliaOverlayID> newNode = new KBRKademliaNode<HKademliaOverlayID>(
				itsContact, msgMgr, config);
		return newNode;
	}

}
