/*
 * Copyright (c) 2005-2011 KOM – Multimedia Communications Lab
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

package de.tu.darmstadt.dvs.nSense.overlay;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tu.darmstadt.dvs.nSense.overlay.util.Constants;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The bootstrapManager is one instance, that know all online nodes. It is used
 * as "server" in nSense, that give a random number of Nodes, that are online in
 * the overlay.
 * 
 * @author Maribel Zamorano
 * @version 09/11/2013
 */
public class NSenseBootstrapManager implements BootstrapManager<NSenseNode> {

	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(NSenseBootstrapManager.class);

	private final List<TransInfo> bootstrapNodes = new Vector<TransInfo>();

	private static NSenseBootstrapManager singeltonInstance;

	private NSenseID lastAssigned = Constants.EMPTY_NSENSE_ID;

	public static NSenseBootstrapManager getInstance() {
		if (singeltonInstance == null)
			singeltonInstance = new NSenseBootstrapManager();
		return singeltonInstance;
	}

	@Override
	public void registerNode(NSenseNode node) {
		log.info("Register node: " + node.getOverlayID());
		if (bootstrapNodes.contains(node.getTransInfo())) {
			log.warn("Duplicate register of a node: "
					+ node.getOverlayID().getUniqueValue());
		}
		bootstrapNodes.add(node.getTransInfo());
	}

	@Override
	public void unregisterNode(NSenseNode node) {
		log.info("Unregister node: " + node.getOverlayID());
		if (!bootstrapNodes.contains(node.getTransInfo()))
			log.warn("Unregister node, that not exists: "
					+ node.getOverlayID().getUniqueValue());
		bootstrapNodes.remove(node.getTransInfo());
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		return getBootstrapInfo(Configuration.NUMBER_SECTORS);
	}

	/**
	 * Gets a list of random contacts back. The list has the length of number.
	 * 
	 * @param number
	 *            The number of contacts
	 * @return A list of {@link TransInfo}.
	 */
	public List<TransInfo> getBootstrapInfo(int number) {
		List<TransInfo> randomTransInfos = new Vector<TransInfo>();
		for (int i = 0; i < number; i++) {
			int randInt = Simulator.getRandom().nextInt(bootstrapNodes.size());
			randomTransInfos.add(bootstrapNodes.get(randInt));
		}
		return randomTransInfos;
	}

	/**
	 * Create and get the next unique NSenseID return. The returned value is the
	 * new {@link #lastAssigned}.
	 * 
	 * @return A new unique NSenseID
	 */
	synchronized public NSenseID getNextUniquePSenseID() {
		if (lastAssigned == Constants.EMPTY_NSENSE_ID) {
			lastAssigned = new NSenseID(0);
		} else {
			lastAssigned = new NSenseID(lastAssigned.getUniqueValue() + 1);
		}
		if (log.isDebugEnabled())
			log.debug("New unique NSense ID created: "
					+ lastAssigned.getUniqueValue());
		return lastAssigned;
	}

	public boolean anyNodeAvailable() {
		return !bootstrapNodes.isEmpty();
	}

}
