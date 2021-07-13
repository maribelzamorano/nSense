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

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.IDGenerationHelper;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.dht.KBRForwardInformationImpl;
import de.tud.kom.p2psim.impl.overlay.dht.KBRLookupProvider;
import de.tud.kom.p2psim.impl.overlay.dht.KBRMsgHandler;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.setup.KademliaSetup;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.setup.StaticConfig;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayKey;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * This class provides access to the <code>KademliaNode</code> via the
 * <code>KBR</code> interface.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the concrete implementation of overlay id used by the scenario
 * @version 05/06/2011
 */
public class KBRKademliaNodeGlobalKnowledge<T extends KademliaOverlayID>
		extends KademliaNode<T> implements KBR<T, KademliaOverlayContact<T>> {

	private final static Logger log = SimLogger
			.getLogger(KBRKademliaNodeGlobalKnowledge.class);

	private KBRListener kbrListener;

	private KBRLookupProvider<T, KademliaOverlayContact<T>> kbrLookupProvider;

	public KBRKademliaNodeGlobalKnowledge(KademliaOverlayContact<T> myContact,
			TransLayer msgMgr, ComponentsConfig conf) {
		super(myContact, msgMgr, conf);

	}

	@Override
	public boolean isRootOf(OverlayKey key) {

		List<KademliaOverlayContact<T>> closestAroundKey = local_lookup(key,
				getLocalConfig().getBucketSize());

		/*
		 * This node is possibly the root if no other node in his routing table
		 * is "nearer"
		 */
		return isNodeCloserThanTheOthers(key, closestAroundKey);
	}

	private boolean isNodeCloserThanTheOthers(OverlayKey key,
			Collection<KademliaOverlayContact<T>> closestAroundKey) {
		BigInteger kademliaKeyAsBigInteger = ((KademliaOverlayKey) key)
				.getBigInt();

		// Compute the distance of the this node to the key
		BigInteger myDistance = getLocalOverlayContact().getOverlayID().toKey()
				.getBigInt().xor(kademliaKeyAsBigInteger);

		// Check if there is a node that is "nearer" than this node itself
		for (KademliaOverlayContact<T> contact : closestAroundKey) {
			BigInteger distance = contact.getOverlayID().toKey().getBigInt()
					.xor(kademliaKeyAsBigInteger);
			if (myDistance.compareTo(distance) > 0) {
				return false;
			}
		}

		/*
		 * There was no node that is "nearer", so this node thinks it is the
		 * root
		 */
		return true;
	}

	@Override
	public List<KademliaOverlayContact<T>> local_lookup(OverlayKey key, int num) {

		LinkedList<KademliaOverlayContact<T>> contactList = new LinkedList<KademliaOverlayContact<T>>();

		Set<KademliaOverlayContact<T>> contactSet = getKademliaRoutingTable()
				.localLookup((KademliaOverlayKey) key, num);

		/*
		 * FIXME: This is just a workaround!
		 * 
		 * The following lines are a hack to be able to route deterministic to
		 * the globally closest peers in the scenario. It seems as if Kademlia
		 * does not guarantee the routing to the global nearest peer in the
		 * overlay.
		 * 
		 * With this lines a node that does not know any closer node (and
		 * therefore would think that it is the root of a key) uses global
		 * knowledge to find the real root.
		 */
		if (isNodeCloserThanTheOthers(key, contactSet)) {
			List<Host> allHosts = GlobalOracle.getHosts();

			BigInteger kademliaKeyAsBigInteger = ((KademliaOverlayKey) key)
					.getBigInt();

			// Compute the distance of the this node to the key
			BigInteger myDistance = getLocalOverlayContact().getOverlayID()
					.toKey().getBigInt().xor(kademliaKeyAsBigInteger);

			/*
			 * Find the really nearest node to the key with global knowledge
			 */
			BigInteger nearestDistance = myDistance;
			KademliaOverlayContact<T> nearestContact = null;

			for (Host host : allHosts) {
				KBR<T, KademliaOverlayContact<T>> node = (KBR<T, KademliaOverlayContact<T>>) host
						.getOverlay(KBR.class);

				BigInteger hisDistance = node.getLocalOverlayContact()
						.getOverlayID().toKey().getBigInt().xor(
								kademliaKeyAsBigInteger);

				if (hisDistance.compareTo(nearestDistance) < 0) {
					nearestDistance = hisDistance;
					nearestContact = (KademliaOverlayContact<T>) node
							.getLocalOverlayContact();
				}
			}

			if (nearestDistance.compareTo(myDistance) != 0) {
				contactList.add(nearestContact);
				if (isInRoute)
					log
							.debug("Use global knowledge to find the nearest peer for a key.");
			} else {
				contactList.addAll(contactSet);
				if (isInRoute)
					log.debug("Global knowledge was not necessary.");
			}

		} else {
			contactList.addAll(contactSet);
		}

		return contactList;
	}

	@Override
	public List<KademliaOverlayContact<T>> neighborSet(int num) {
		LinkedList<KademliaOverlayContact<T>> neighbors = new LinkedList<KademliaOverlayContact<T>>();

		// Extract neighbors from routing table by performing a local_lockup
		// for the key generated from the OverlayID of this node.
		Set<KademliaOverlayContact<T>> neighborSet = getKademliaRoutingTable()
				.localLookup(getTypedOverlayID().toKey(), num);

		neighbors.addAll(neighborSet);
		return neighbors;
	}

	@Override
	public T[] range(KademliaOverlayContact<T> contact, int rank) {

		// For the following computation we need the node itself, rank nodes
		// inside the interval and the two exclusive borders of the interval
		int numberOfNeededNodes = rank + 3;

		Set<KademliaOverlayContact<T>> neighborSet = getKademliaRoutingTable()
				.localLookup(getTypedOverlayID().toKey(), numberOfNeededNodes);

		BigInteger xorDistanceToUpper = BigInteger.ZERO;
		BigInteger xorDistanceToLower = BigInteger.ZERO;

		BigInteger localKeyAsBigInteger = getTypedOverlayID().toKey()
				.getBigInt();

		// Find the distances to the nodes at the border of the interval

		for (KademliaOverlayContact<T> contactInSet : neighborSet) {
			BigInteger contactKeyAsBigInteger = contactInSet.getOverlayID()
					.toKey().getBigInt();

			BigInteger distance = localKeyAsBigInteger
					.xor(contactKeyAsBigInteger);

			if (contactKeyAsBigInteger.compareTo(localKeyAsBigInteger) == -1) {
				// The contacts key is smaller than the local key

				if (distance.compareTo(xorDistanceToLower) == 1) {
					// Current distance is the new greatest distance to a lower
					// key
					xorDistanceToLower = distance;
				}
			} else if (contactKeyAsBigInteger.compareTo(localKeyAsBigInteger) == 1) {
				// The contacts key is greater than the local key

				if (distance.compareTo(xorDistanceToUpper) == 1) {
					// Current distance is the new greatest distance to a upper
					// key
					xorDistanceToUpper = distance;
				}
			}
		}

		/*
		 * Compute the range for return
		 * 
		 * Lower bound of the range = (key of local node) - [(distance to lowest
		 * node in set of neighbors)/2]
		 * 
		 * Upper bound of the range = (key of local node) + [(distance to
		 * greatest node in set of neighbors)/2]
		 */

		KademliaOverlayID[] range = new KademliaOverlayID[2];

		BigInteger lowerBound = localKeyAsBigInteger
				.subtract(xorDistanceToLower.divide(BigInteger
						.valueOf(new Long(2))));

		BigInteger upperBound = localKeyAsBigInteger.add(xorDistanceToUpper
				.divide(BigInteger.valueOf(new Long(2))));

		range[0] = new KademliaOverlayID(lowerBound, KademliaSetup.getConfig());
		range[1] = new KademliaOverlayID(upperBound, KademliaSetup.getConfig());

		return (T[]) range;
	}

	@Override
	public List<KademliaOverlayContact<T>> replicaSet(OverlayKey key,
			int maxRank) {
		return local_lookup(key, maxRank);
	}

	private boolean isInRoute = false;

	@Override
	public void route(OverlayKey key, Message msg,
			KademliaOverlayContact<T> hint) {
		OverlayContact nextHop = null;
		if (hint != null) {
			nextHop = hint;
		} else if (key != null) {
			isInRoute = true;
			nextHop = local_lookup(key, 1).get(0);
			isInRoute = false;
			// Inform the monitors about an initiated query
			Simulator.getMonitor().queryStarted(getLocalOverlayContact(), msg);
		} else {
			log.error("Both key and hint are null!!");
			return;
		}
		if (kbrListener != null) {
			KBRForwardInformation info = new KBRForwardInformationImpl(key, msg, nextHop);
			kbrListener.forward(info);
			key = info.getKey();
			msg = info.getMessage();
			nextHop = info.getNextHopAgent();
		} else {
			log
					.error("There is no KBRListener to notify. Please register your Application (use method: setKBRListener).");

		}
		ForwardMsg fm = new ForwardMsg(getOverlayID(), nextHop.getOverlayID(),
				key, msg);
		getTransLayer().send(fm, nextHop.getTransInfo(), getPort(),
				TransProtocol.UDP);

	}

	@Override
	public void setKBRListener(KBRListener listener) {
		this.kbrListener = listener;
		KBRMsgHandler<T, KademliaOverlayContact<T>> msgHandler = new KBRMsgHandler<T, KademliaOverlayContact<T>>(
				this, this, kbrListener);

		kbrLookupProvider = msgHandler.getLookupProvider();
	}

	@Override
	public OverlayKey getNewOverlayKey(int rank) {
		return new KademliaOverlayKey(rank, new StaticConfig());
	}

	@Override
	public KademliaOverlayContact<T> getLocalOverlayContact() {
		short port = getHost().getOverlay(KBR.class).getPort();
		TransInfo transInfo = getHost().getTransLayer().getLocalTransInfo(port);

		return new KademliaOverlayContact<T>(getTypedOverlayID(), transInfo);
	}

	@Override
	public void hadContactTo(OverlayContact<T> contact) {
		getKademliaRoutingTable().addContact(
				(KademliaOverlayContact<T>) contact);
	}

	@Override
	public OverlayKey getRandomOverlayKey() {

		BigInteger idValue = IDGenerationHelper.getSHA1Hash(
				((Integer) Simulator.getRandom().nextInt()).toString(),
				KademliaSetup.getConfig().getIDLength());

		return new KademliaOverlayID(idValue, KademliaSetup.getConfig())
				.toKey();
	}

	@Override
	public KademliaOverlayContact getOverlayContact(OverlayID id,
			TransInfo transinfo) {
		return new KademliaOverlayContact((KademliaOverlayID) id, transinfo);
	}

	public KBRLookupProvider<T, KademliaOverlayContact<T>> getKbrLookupProvider() {
		return kbrLookupProvider;
	}

}
