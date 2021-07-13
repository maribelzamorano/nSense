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

package de.tud.kom.p2psim.impl.overlay.ido.cs.messages;

import java.util.List;
import java.util.Vector;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.ido.cs.ClientNodeInfo;
import de.tud.kom.p2psim.impl.overlay.ido.cs.util.CSConstants.MSG_TYPE;

/**
 * The update Messages from the Server to the clients. It contains a list of
 * {@link ClientNodeInfo}s, which are in the area of interest radius of the
 * client.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class UpdatePositionClientMessage extends CSAbstractMessage {

	/**
	 * A list of {@link ClientNodeInfo}s.
	 */
	List<ClientNodeInfo> nodeInfos;

	/**
	 * Sets the list of ClientNodeInfos and the message type.
	 * 
	 * @param nodeInfos
	 *            A list of ClientNodeInfos.
	 */
	public UpdatePositionClientMessage(List<ClientNodeInfo> nodeInfos) {
		super(MSG_TYPE.UPDATE_POSITION_CLIENT_MESSAGE);
		if (nodeInfos != null)
			this.nodeInfos = nodeInfos;
		else
			this.nodeInfos = new Vector<ClientNodeInfo>();
	}

	@Override
	public long getSize() {
		int size = 0;
		if (nodeInfos.size() > 0) {
			int temp = nodeInfos.get(0).getTransmissionSize();
			size = nodeInfos.size() * temp;
		}
		return super.getSize() + size;
	}

	/**
	 * Gets a list of ClientNodeInfos, that are stored in the message.
	 * 
	 * @return A list of ClientNodeInfos.
	 */
	public List<ClientNodeInfo> getClientNodeInfos() {
		List<ClientNodeInfo> result = new Vector<ClientNodeInfo>(nodeInfos);
		return result;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UpdatePositionClientMessage) {
			UpdatePositionClientMessage u = (UpdatePositionClientMessage) o;
			return this.nodeInfos.equals(u.nodeInfos) && super.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ MsgType: ");
		temp.append(getMsgType());
		temp.append(", clientNodeInfos: ");
		temp.append(getClientNodeInfos());
		temp.append(" ]");
		return temp.toString();
	}

}
