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


package de.tud.kom.p2psim.impl.skynet.attributes.messages;

import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.skynet.AbstractSkyNetMessage;

/**
 * This class defines a SkyNet-message, which is used as ACK to an
 * <code>SupportPeerUpdateMsg</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SupportPeerUpdateACKMsg extends AbstractSkyNetMessage {

	public SupportPeerUpdateACKMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, long skyNetMsgID, boolean senderSP) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, true, false,
				senderSP);
	}

}
