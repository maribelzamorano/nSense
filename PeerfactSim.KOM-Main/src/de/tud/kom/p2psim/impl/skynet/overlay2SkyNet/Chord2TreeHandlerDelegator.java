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


package de.tud.kom.p2psim.impl.skynet.overlay2SkyNet;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.TreeHandlerDelegator;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.util.LookupResult;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.util.ProcessNextLevelResult;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.addressresolution.Chord2AddressResolutionImpl;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util.DefaultLookupResult;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util.DefaultProcessNextLevelResult;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Chord2TreeHandlerDelegator implements TreeHandlerDelegator {

	private static Logger log = SimLogger
			.getLogger(Chord2TreeHandlerDelegator.class);

	private SkyNetNodeInterface skyNetNode;

	private ChordNode ownOverlayNode;

	private boolean keyResponsibility;

	private SkyNetID ownID;

	private SkyNetID skyNetCoKey;

	private BigDecimal left;

	private BigDecimal right;

	private int iter;

	public Chord2TreeHandlerDelegator() {
		keyResponsibility = false;
	}

	@Override
	public void setOwnOverlayNode(OverlayNode ownOverlayNode) {
		this.ownOverlayNode = (ChordNode) ownOverlayNode;
	}

	@Override
	public void lookupOperationFailed(Operation op) {
		log.error(Simulator.getFormattedTime(Simulator.getCurrentTime())
				+ " "
				+ skyNetNode.getSkyNetNodeInfo().getTransInfo().getNetId()
						.toString()
				+ " ----NO CHANCE TO LOOKUP PARENTCOORDINATOR----");
	}

	@Override
	public LookupResult lookupOperationSucceeded(Operation op) {
		OverlayContact contact = ((List<ChordContact>) op.getResult()).get(0);
		SkyNetID skyNetID = Chord2AddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				((ChordContact) contact).getOverlayID());
		return new DefaultLookupResult(contact, skyNetID);
	}

	@Override
	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback callback) {
		ChordID overlayKey = Chord2AddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getOverlayID(
				coordinatorKey);
		ownOverlayNode.overlayNodeLookup(overlayKey, callback);
	}

	@Override
	public void setSkyNetNode(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	@Override
	public void processNextLevel(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter,
			OperationCallback callback) {
		this.ownID = ownID;
		this.skyNetCoKey = skyNetCoKey;
		this.left = left;
		this.right = right;
		this.iter = iter;
		ChordID coordinatorID = Chord2AddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE)
				.getOverlayID(skyNetCoKey);

		ChordID predecessorID = ownOverlayNode.getChordRoutingTable()
				.getPredecessor().getOverlayID();
		if (predecessorID != null) {
			if (predecessorID.equals(ownOverlayNode.getOverlayID())) {
				log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "The first node in the ring, or an error");
			}
			keyResponsibility = ownOverlayNode.getChordRoutingTable()
					.responsibleFor(coordinatorID);
			// keyResponsibility = coordinatorID.between(predecessorID,
			// ownOverlayNode.getChordID());
		} else {
			log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "Unknown Predecessor");
			keyResponsibility = false;
		}

		callback.calledOperationSucceeded(null);
	}

	@Override
	public void processNextLevelOperationFailed(Operation op) {
		// not needed within this implementation
	}

	@Override
	public ProcessNextLevelResult processNextLevelOperationSucceeded(
			Operation op) {
		return new DefaultProcessNextLevelResult(keyResponsibility, ownID,
				skyNetCoKey, left, right, iter);
	}

	// //////////////////////////////////////////////////////////////
	// Methods for determining the responsibility-interval of a node on the
	// current overlay and its mapping to the ID-space of SkyNet
	// //////////////////////////////////////////////////////////////

	@Override
	public void calculateResponsibilityInterval(SkyNetID id,
			OperationCallback callback) {
		// This is just dummy-method to obtain the same structure as for the
		// other overlays. Within this function there exists only the
		// function-call of processNextLevel(...)
		callback.calledOperationSucceeded(null);

	}

	@Override
	public void calculateResponsibilityIntervalOperationFailed(Operation op) {
		// not needed

	}

	@Override
	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation op) {
		// This is just dummy-method to obtain the same structure as for the
		// other overlays. Within this function there exists only the
		// function-call of processNextLevel(...)
		return null;
	}

}
