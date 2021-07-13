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


package de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.util;

import java.math.BigDecimal;

import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.util.ProcessNextLevelResult;
import de.tud.kom.p2psim.impl.skynet.SkyNetID;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class DefaultProcessNextLevelResult implements ProcessNextLevelResult {

	private SkyNetID ownID;

	private SkyNetID skyNetCoKey;

	private BigDecimal left;

	private BigDecimal right;

	private int iter;

	private boolean keyResponsibility;

	public DefaultProcessNextLevelResult(boolean keyResponsibility,
			SkyNetID ownID, SkyNetID skyNetCoKey, BigDecimal left,
			BigDecimal right, int iter) {
		this.ownID = ownID;
		this.skyNetCoKey = skyNetCoKey;
		this.left = left;
		this.right = right;
		this.iter = iter;
		this.keyResponsibility = keyResponsibility;
	}

	public SkyNetID getOwnID() {
		return ownID;
	}

	public SkyNetID getSkyNetCoKey() {
		return skyNetCoKey;
	}

	public BigDecimal getLeft() {
		return left;
	}

	public BigDecimal getRight() {
		return right;
	}

	public int getIter() {
		return iter;
	}

	public boolean isKeyResponsibility() {
		return keyResponsibility;
	}

}
