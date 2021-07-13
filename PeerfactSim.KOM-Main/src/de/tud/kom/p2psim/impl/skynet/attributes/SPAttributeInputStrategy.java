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


package de.tud.kom.p2psim.impl.skynet.attributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.service.skynet.InputStrategy;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SubCoordinatorInfo;
import de.tud.kom.p2psim.api.service.skynet.SupportPeer;
import de.tud.kom.p2psim.impl.skynet.SkyNetHostProperties;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.attributes.messages.AttributeUpdateMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class handles the incoming <i>Attribute-Updates</i> from all
 * Sub-Coordinators as Support Peer. It is much more simpler than the
 * corresponding class <code>AttributeInputStrategy</code> for a Coordinator.
 * Since the methods of this class have the same names as in
 * <code>AttributeInputStrategy</code> and contain nearly the same
 * functionality, please refer to {@link AttributeInputStrategy}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SPAttributeInputStrategy implements InputStrategy {

	private static Logger log = SimLogger
			.getLogger(SPAttributeInputStrategy.class);

	private SupportPeer supportPeer;

	private AttributeStorage attributeStorage;

	// fields for SupportPeer-Logic and for determining the amount of received
	// AttributeEntries

	// private int actualEntryRequest;

	private int tTreshold;

	public SPAttributeInputStrategy(SupportPeer supportPeer,
			AttributeStorage attributeStorage) {
		this.supportPeer = supportPeer;
		this.attributeStorage = attributeStorage;
		// actualEntryRequest = 0;
		tTreshold = 0;
	}

	public void reset() {
		// actualEntryRequest = 0;
		tTreshold = 0;
	}

	public void processUpdateMessage(Message msg, long timestamp) {
		AttributeUpdateMsg message = (AttributeUpdateMsg) msg;
		SkyNetNodeInfo skyNetNodeInfo = message.getSenderNodeInfo();
		AttributeSubCoordinatorInfo subCoInfo = new AttributeSubCoordinatorInfo(
				skyNetNodeInfo, message.getNumberOfUpdates(), timestamp,
				supportPeer.getSPAttributeUpdateStrategy().getUpdateInterval(),
				message.getNumberOfMaxEntries(), message.getContent(), message
						.isSenderSP());
		addSubCoordinator(subCoInfo);
	}

	public void writeOwnDataInStorage() {
		// not needed
	}

	// ----------------------------------------------------------------------
	// Methods for adding or refreshing SubCoordinators including the data in
	// AttributeStorage, which is delivered by this coordinators
	// ----------------------------------------------------------------------

	public void addSubCoordinator(SubCoordinatorInfo subCo) {
		tTreshold = ((SkyNetHostProperties) supportPeer.getHost()
				.getProperties()).getTTresholdSP();
		AttributeSubCoordinatorInfo subCoordinator = (AttributeSubCoordinatorInfo) subCo;
		BigDecimal subCoordinatorID = subCoordinator.getNodeInfo()
				.getSkyNetID().getID();

		// Check if a oldSubCoordinator exists or not and put the newer entry or
		// the complete new SubCo to the list of SubCos

		HashMap<BigDecimal, AttributeSubCoordinatorInfo> subCoList = attributeStorage
				.getListOfSubCoordinatorsOfSP();
		subCoList.remove(subCoordinatorID);
		subCoList.put(subCoordinatorID, subCoordinator);

		Iterator<BigDecimal> subCoIter = subCoList.keySet().iterator();
		int ter = 0;
		AttributeSubCoordinatorInfo s = null;
		while (subCoIter.hasNext()) {
			s = subCoList.get(subCoIter.next());
			ter += s.getRequestedEntries();
		}
		// if (ter > actualEntryRequest) {
		if (ter > tTreshold) {
			log.warn(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "received too much entries as SupportPeer");
		}
		// }
	}
}
