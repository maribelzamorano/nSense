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
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.napster.NapsterOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.overlay.dht.napster.operations.GetPredecessorOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;
import de.tud.kom.p2psim.impl.skynet.SkyNetUtilities;
import de.tud.kom.p2psim.impl.skynet.addressresolution.NapsterAddressResolutionImpl;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util.DefaultLookupResult;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util.DefaultProcessNextLevelResult;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class NapsterTreeHandlerDelegator implements TreeHandlerDelegator {

	private static Logger log = SimLogger
			.getLogger(NapsterTreeHandlerDelegator.class);

	private SkyNetNodeInterface skyNetNode;

	private NapsterClientNode ownOverlayNode;

	private boolean keyResponsibility;

	private SkyNetID predecessor;

	private SkyNetID ownID;

	private SkyNetID skyNetCoKey;

	private BigDecimal left;

	private BigDecimal right;

	private int iter;

	public NapsterTreeHandlerDelegator() {
		keyResponsibility = false;
	}

	@Override
	public void setSkyNetNode(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
	}

	@Override
	public void setOwnOverlayNode(OverlayNode ownOverlayNode) {
		this.ownOverlayNode = (NapsterClientNode) ownOverlayNode;
	}

	// //////////////////////////////////////////////////////////////
	// Methods for looking up the parent-coordinator of a coordinator by the
	// calculated coordinatorKey
	// //////////////////////////////////////////////////////////////

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
		OverlayContact contact = (OverlayContact) op.getResult();
		SkyNetID skyNetID = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				((NapsterOverlayContact) contact).getOverlayID());
		return new DefaultLookupResult(contact, skyNetID);
	}

	@Override
	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback callback) {
		NapsterOverlayID napsterCoKey = NapsterAddressResolutionImpl
				.getInstance((int) SkyNetConstants.OVERLAY_ID_SIZE)
				.getOverlayID(coordinatorKey);
		ownOverlayNode.nodeLookup(napsterCoKey, callback);
	}

	// //////////////////////////////////////////////////////////////
	// Methods for processing the next level of the SkyNet-Tree
	// //////////////////////////////////////////////////////////////

	@Override
	public void processNextLevel(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter,
			OperationCallback callback) {
		this.ownID = ownID;
		this.skyNetCoKey = skyNetCoKey;
		this.left = left;
		this.right = right;
		this.iter = iter;
		// special case: the responsibility-interval is cut into two intervals
		if (ownID.getID().compareTo(predecessor.getID()) < 1) {
			if (predecessor.getID().compareTo(skyNetCoKey.getID()) == -1
					|| skyNetCoKey.getID().compareTo(ownID.getID()) < 1) {
				keyResponsibility = true;
			} else {
				keyResponsibility = false;
			}
		}
		// normal case: the responsibility-interval is not divided
		else {
			if ((predecessor.getID().compareTo(skyNetCoKey.getID()) == -1)
					&& (skyNetCoKey.getID().compareTo(ownID.getID()) < 1)) {
				keyResponsibility = true;
			} else {
				keyResponsibility = false;
			}
		}
		callback.calledOperationSucceeded(null);
	}

	@Override
	public void processNextLevelOperationFailed(Operation op) {
		// not needed
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
		ownOverlayNode.getPredecessor(callback);

	}

	@Override
	public void calculateResponsibilityIntervalOperationFailed(Operation op) {
		log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "----NO CHANCE TO GET PREDECESSOR----");
	}

	@Override
	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation op) {
		SkyNetID predecessorID = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				((GetPredecessorOperation) op).getResult().getOverlayID());
		log.info(" The client " + skyNetNode.getSkyNetNodeInfo().toString()
				+ " has the Predecessor with the id "
				+ predecessorID.getPlainSkyNetID());
		predecessor = predecessorID;
		return predecessorID;
	}

}
