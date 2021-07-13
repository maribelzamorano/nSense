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


package de.tud.kom.p2psim.impl.skynet;

import java.math.BigDecimal;

import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * This class implements {@link SkyNetNodeInfo}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetNodeInfoImpl implements SkyNetNodeInfo {

	private SkyNetID skyNetID;

	private SkyNetID coordinatorKey;

	private TransInfo transInfo;

	private int level;

	private int observedLevel = -1;

	public TransInfo getTransInfo() {
		return transInfo;
	}

	public void setTransInfo(TransInfo transInfo) {
		this.transInfo = transInfo;
	}

	public SkyNetNodeInfoImpl(SkyNetID id, SkyNetID key, TransInfo transInfo,
			int level) {
		skyNetID = id;
		coordinatorKey = key;
		this.transInfo = transInfo;
		this.level = level;
	}

	public SkyNetID getSkyNetID() {
		return skyNetID;
	}

	public void setSkyNetID(SkyNetID skyNetID) {
		this.skyNetID = skyNetID;
	}

	public SkyNetID getCoordinatorKey() {
		return coordinatorKey;
	}

	public void setCoordinatorKey(SkyNetID coordinatorKey) {
		this.coordinatorKey = coordinatorKey;
	}

	public boolean isComplete() {
		if (skyNetID != null && coordinatorKey != null) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String ret = "SkyNetNodeInfo[";
		if (skyNetID != null) {
			ret += "SkyNetID = " + skyNetID.getPlainSkyNetID() + "; ";
		}
		if (coordinatorKey != null) {
			ret += "C-Key = " + coordinatorKey.getPlainSkyNetID() + "; ";
		}
		if (transInfo != null) {
			ret += transInfo.toString();
		}
		return ret + "; level = " + level + "]";
	}

	public double getDistance(NetPosition netPosition) {
		BigDecimal foreignID = ((SkyNetNodeInfoImpl) netPosition).getSkyNetID()
				.getID();
		return this.getSkyNetID().getID().subtract(foreignID).abs()
				.doubleValue();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public SkyNetNodeInfoImpl clone() {
		return new SkyNetNodeInfoImpl(skyNetID, coordinatorKey, transInfo,
				level);
	}

	@Override
	public SkyNetID getOverlayID() {
		return getSkyNetID();
	}

	@Override
	public int getObservedLevelFromRoot() {
		return observedLevel;
	}

	@Override
	public void setObservedLevelFromRoot(int level) {
		observedLevel = level;
	}

}
