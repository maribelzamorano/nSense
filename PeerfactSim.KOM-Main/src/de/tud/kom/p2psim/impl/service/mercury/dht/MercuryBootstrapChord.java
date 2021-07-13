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

package de.tud.kom.p2psim.impl.service.mercury.dht;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordIDFactory;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;
import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;
import de.tud.kom.p2psim.impl.service.mercury.MercuryService;
import de.tud.kom.p2psim.impl.service.mercury.attribute.AttributeType;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * Bootstrapping for Mercury in conjunction with Chord, needed to create
 * multiple Chord Rings
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryBootstrapChord implements MercuryBootstrap {

	private int count = 0;

	private List<ChordBootstrapManager> bootstrapHubs = 
		new Vector<ChordBootstrapManager>();
	
	private List<MercuryAttributePrimitive> attributesByInt =
		new Vector<MercuryAttributePrimitive>();

	/**
	 * MercuryChord Bootstrapper
	 */
	public MercuryBootstrapChord() {
		// intentionally left blank
	}

	/**
	 * Create Chord Bootstrappers for every attribute
	 */
	@Override
	public void setAttributes(List<MercuryAttributePrimitive> attributes) {
		for (MercuryAttributePrimitive actAttr : attributes) {
			bootstrapHubs.add(new ChordBootstrapManager());
			attributesByInt.add(actAttr);
		}
	}

	/**
	 * Distribute Contacts to an attribute
	 */
	@Override
	public MercuryBootstrapInfo getBootstrapInfo() {
		// int key = Simulator.getRandom().nextInt(this.attributes.size());
		count = count + 1;
		int key = count % 2;
		MercuryBootstrapInfo bsInfo = new MercuryBootstrapInfo(
				bootstrapHubs.get(key), attributesByInt.get(key), this);
		return bsInfo;
	}

	/**
	 * Utilize Chord Bootstrapping to get a contact in every other Attribute Hub
	 */
	@Override
	public List<MercuryContact> getRandomContactForEachAttribute() {
		List<MercuryContact> contacts = new Vector<MercuryContact>();
		for (int i = 0; i < bootstrapHubs.size(); i++) {
			if (!bootstrapHubs.get(i).getAvailableNodes().isEmpty()) {
				ChordContact node = bootstrapHubs.get(i)
						.getRandomAvailableNode();
				if (node != null) {
					MercuryAttributePrimitive attribute = attributesByInt
							.get(i);
					if (attribute.getType() == AttributeType.Integer) {
						Integer[] range = new Integer[2];
						range[0] = (Integer) attribute.getMin();
						range[1] = (Integer) attribute.getMax();
						contacts.add(new MercuryContact(attribute.getName(),
								node.getTransInfo(), range));
					} else {
						System.err
								.println("Other Types than Integer are not yet implemented in Bootstrapping");
					}

				}
			}
		}
		return contacts;
	}

	@Override
	public DHTNode createOverlayNode(MercuryBootstrapInfo bsInfo, Host host,
			short port) {
		return new ChordNode(host.getTransLayer(), port,
				(ChordBootstrapManager) bsInfo.getBootstrap());
	}

	@Override
	public void callbackOverlayID(MercuryService service) {
		ChordID newID = calcNewOverlayID(service);
		((ChordNode) service.getDHTNode()).setOverlayID(newID);
	}

	@Override
	public MercuryIDMapping getIDMapping() {
		return new MercuryIDMappingChord();
	}

	@Override
	public OverlayID[] getRange(INeighborDeterminator neighbors, MercuryService service) {
		if (neighbors == null) {
			return null;
		}
		
		Collection<OverlayContact> nlist = neighbors.getNeighbors();
		OverlayID[] range = new OverlayID[2];
		for (OverlayContact contact : nlist) {
			if (contact.getOverlayID().compareTo(
					service.getDHTNode().getOverlayID()) < 0) {
				range[0] = contact.getOverlayID();
				break;
			}
		}
		if (range[0] == null) {
			// wrap-around
			OverlayID max = service.getDHTNode().getOverlayID();
			for (OverlayContact contact : nlist) {
				if (contact.getOverlayID().compareTo(max) > 0) {
					max = contact.getOverlayID();
				}
			}
			range[0] = max;
		}
		if (range[0] == null) {
			range[0] = service.getDHTNode().getOverlayID();
		}
		range[0] = ChordIDFactory.getInstance().getChordID(
				((ChordID) range[0]).getValue().add(BigInteger.ONE));
		range[1] = service.getDHTNode().getOverlayID();
		return range;
	}

	/**
	 * Helper to get best Overlay ID in terms of equal distribution of ranges
	 * 
	 * @param ownService
	 * @return
	 */
	private ChordID calcNewOverlayID(MercuryService ownService) {
		List<BigInteger> idSpace = new Vector<BigInteger>();
		// Filter the onlineServices and Services for the same Hub
		for (Host host : GlobalOracle.getHosts()) {
			MercuryService service = host.getComponent(MercuryService.class);
			if (service != null
					&& ((ChordNode) service.getDHTNode()).getPeerStatus() != PeerStatus.ABSENT) {
				if (ownService.getOwnAttribute().equals(
						service.getOwnAttribute())) {
					idSpace.add((BigInteger) service.getDHTNode()
							.getOverlayID()
							.getUniqueValue());
				}
			}
		}
		Collections.sort(idSpace);

		BigInteger maxSpace = BigInteger.ONE;
		BigInteger newID = BigInteger.ONE;
		for (int i = 0; i < idSpace.size() - 1; i++) {
			BigInteger first = idSpace.get(i);
			BigInteger second = idSpace.get(i + 1);
			if (second.subtract(first).compareTo(maxSpace) > 0) {
				maxSpace = second.subtract(first);
				newID = first.add(second.subtract(first).divide(
						new BigInteger("2")));
			}
		}

		if (idSpace.size() == 0) {
			return new ChordID(BigInteger.ONE);
		}
		if (idSpace.size() == 1) {
			BigInteger max = new BigInteger("2").pow(ChordID.KEY_BIT_LENGTH);
			BigInteger maxHalf = max.divide(new BigInteger("2"));
			return new ChordID(idSpace.get(0).add(maxHalf).mod(max));
		}
		// überlauf noch vergleichen.
		if (idSpace.size() >= 2) {
			BigInteger first = idSpace.get(idSpace.size() - 1);
			BigInteger second = idSpace.get(0).add(
					new BigInteger("2").pow(ChordID.KEY_BIT_LENGTH));

			if (second.subtract(first).compareTo(maxSpace) > 0) {
				maxSpace = second.subtract(first);
				newID = first.add(
						second.subtract(first).divide(new BigInteger("2")))
						.mod(new BigInteger("2").pow(ChordID.KEY_BIT_LENGTH));
			}

		}

		return new ChordID(newID);

	}

}
