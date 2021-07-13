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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;

/**
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordOverlayUtil {

	/**
	 * This method returns the responsible node for key
	 * 
	 * @param peerList
	 *            : the current participants
	 * @param key
	 *            : searching key
	 * @return responsible node contact for the key
	 */
	public static ChordContact getResponsibleNodeContact(
			List<ChordContact> peerList, BigInteger key) {

		ArrayList<ChordContact> copyList = new ArrayList<ChordContact>(peerList);
		ArrayList<ChordContact> sortedContacts = new ArrayList<ChordContact>(
				copyList);
		Collections.sort(sortedContacts);

		for (int index = 0; index < sortedContacts.size(); index++) {
			if (sortedContacts.get(index).getOverlayID().getValue().compareTo(
					key) >= 0) {
				return sortedContacts.get(index);
			}
		}
		// return the first node
		return sortedContacts.get(0);
	}

	/**
	 * This method returns the responsible node for a key
	 * 
	 * @param peerList
	 *            : the current participants
	 * @param key
	 *            : searching key
	 * @return responder node for the key
	 */
	public static ChordNode getResponsibleNode(List<ChordNode> peerList,
			BigInteger key) {

		ArrayList<ChordContact> contactList = new ArrayList<ChordContact>();
		for (ChordNode node : peerList) {
			contactList.add(node.getLocalChordContact());
		}
		ChordContact contact = getResponsibleNodeContact(contactList, key);
		for (ChordNode node : peerList) {
			if (node.getLocalChordContact().equals(contact)) {
				return node;
			}
		}
		return null;
	}

}
