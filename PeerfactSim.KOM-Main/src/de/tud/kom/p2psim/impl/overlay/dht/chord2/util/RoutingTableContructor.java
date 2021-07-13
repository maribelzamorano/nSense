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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class contains utility methods to deal with RoutingTable
 * 
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class RoutingTableContructor {

	private static Logger log = SimLogger.getLogger(RoutingTableContructor.class);
	
	
	/**
	 * This method is used to reconstruct FingerTable from 
	 * a set of contacts
	 * 
	 * @param node 
	 * @param nodeList: the current participants
	 * @return the finger table for the node if nodeList is valid
	 */
	public static ChordContact[] getFingerTable(ChordContact node,
			Set<ChordContact> nodeList) {

		ArrayList<ChordContact> sortedContact = new ArrayList<ChordContact>(
				nodeList);
		int bitLength = ChordID.KEY_BIT_LENGTH;
		Collections.sort(sortedContact);

		BigInteger overCount = new BigInteger("0");
		BigInteger twoPowBitLength = new BigInteger("2").pow(bitLength);
		ChordContact[] result = new ChordContact[ChordID.KEY_BIT_LENGTH];
		int index = 0;
		for (int i = 0; i < ChordID.KEY_BIT_LENGTH; i++) {
			BigInteger point = new BigInteger("2").pow(i);
			point = point.add(node.getOverlayID().getValue());
			while (sortedContact.get(index).getOverlayID().getValue().add(overCount).compareTo(point) < 0) {
				if (index < sortedContact.size() - 1) {
					index++;
				} else {
					index = 0;
					overCount = twoPowBitLength;
				}

			}
			result[i] = sortedContact.get(index);
			
		}
		return result;
	}

	/**
	 * Get different contact form set of contacts
	 * @param contactList
	 * @return
	 */
	public static Set<ChordContact> getDistinctContactList(
			ChordContact[] contactList) {
		
		Set<ChordContact> distinctContacts = new HashSet(Arrays.asList(contactList));
		if(distinctContacts.contains(null)){
			log.info("contact list contains null");
			distinctContacts.remove(null);
		}
		return distinctContacts;
	}
}
