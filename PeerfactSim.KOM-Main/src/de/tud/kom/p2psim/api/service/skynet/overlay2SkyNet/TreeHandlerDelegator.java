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


package de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet;

import java.math.BigDecimal;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.util.LookupResult;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.util.ProcessNextLevelResult;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public interface TreeHandlerDelegator {

	public void setSkyNetNode(SkyNetNodeInterface skyNetNode);

	public void setOwnOverlayNode(OverlayNode ownOverlayNode);

	// //////////////////////////////////////////////////////////////
	// Methods for looking up the parent-coordinator of a coordinator by the
	// calculated coordinatorKey
	// //////////////////////////////////////////////////////////////

	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback callback);

	public void lookupOperationFailed(Operation op);

	public LookupResult lookupOperationSucceeded(Operation op);

	// //////////////////////////////////////////////////////////////
	// Methods for processing the next level of the SkyNet-Tree
	// //////////////////////////////////////////////////////////////

	public void processNextLevel(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter,
			OperationCallback callback);

	public void processNextLevelOperationFailed(Operation op);

	public ProcessNextLevelResult processNextLevelOperationSucceeded(
			Operation op);

	// //////////////////////////////////////////////////////////////
	// Methods for determining the responsibility-interval of a node on the
	// current overlay and its mapping to the ID-space of SkyNet
	// //////////////////////////////////////////////////////////////

	public void calculateResponsibilityInterval(SkyNetID id,
			OperationCallback callback);

	public void calculateResponsibilityIntervalOperationFailed(Operation op);

	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation op);
}
