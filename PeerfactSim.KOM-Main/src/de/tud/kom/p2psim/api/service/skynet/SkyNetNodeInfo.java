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


package de.tud.kom.p2psim.api.service.skynet;

import java.io.Serializable;

import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;

/**
 * This interface seizes the suggestion made by {@link OverlayContact} for the
 * SkyNet over-overlay. The <code>SkyNetNodeInfo</code>-object tries to
 * concentrate all information which is needed to address a host and the SkyNet
 * over-overlay on that host. Therefore, it encapsulates the
 * <code>TransInfo</code>-object and the <code>SkyNetID</code>-object of a host
 * in the P2P-system. Besides these two objects, <code>SkyNetNodeInfo</code>
 * also contains the information of the Coordinator in the SkyNet-tree, for
 * which the SkyNet-node is responsible. The information of a Coordinator
 * comprises the Coordinator-Key and the level of the Coordinator in the tree.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetNodeInfo extends NetPosition, OverlayContact<SkyNetID>,
		Serializable {

	/**
	 * This method returns the ID of the SkyNet-node, to which this
	 * <code>SkyNetNodeInfo</code>-object belongs.
	 * 
	 * @return the <code>SkyNetID</code>-object of the SkyNet-node
	 */
	public SkyNetID getSkyNetID();

	/**
	 * This method returns the key of the Coordinator, for which a SkyNet-node,
	 * to which this <code>SkyNetNodeInfo</code>-object belongs, is responsible.
	 * 
	 * @return the <code>SkyNetID</code>-object of the Coordinator
	 */
	public SkyNetID getCoordinatorKey();

	public TransInfo getTransInfo();

	/**
	 * This method returns the level of the Coordinator in the SkyNet-tree, for
	 * which a SkyNet-node, to which this <code>SkyNetNodeInfo</code>-object
	 * belongs, is responsible.
	 * 
	 * @return the level of the Coordinator in the SkyNet-tree
	 */
	public int getLevel();

	/**
	 * Determines if all variables of the <code>SkyNetNodeInfo</code>-object are
	 * initialized. If at least one the variables contains no reference, the
	 * method returns <code>false</code>.
	 * 
	 * @return <code>true</code>, if this object contains all required
	 *         information, <code>false</code> otherwise.
	 */
	public boolean isComplete();

	public void setSkyNetID(SkyNetID skyNetID);

	public void setCoordinatorKey(SkyNetID coordinatorKey);

	public void setTransInfo(TransInfo transInfo);

	public void setLevel(int level);

	public void setObservedLevelFromRoot(int level);

	public int getObservedLevelFromRoot();

	public SkyNetNodeInfo clone();
}
