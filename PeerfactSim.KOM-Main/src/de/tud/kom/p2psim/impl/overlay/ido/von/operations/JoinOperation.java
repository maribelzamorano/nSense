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

package de.tud.kom.p2psim.impl.overlay.ido.von.operations;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonConfiguration;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonContact;
import de.tud.kom.p2psim.impl.overlay.ido.von.VonNode;
import de.tud.kom.p2psim.impl.overlay.ido.von.messages.InitialQueryMsg;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation handles the joining of a new node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JoinOperation extends AbstractOperation<VonNode, Object> implements
		TransMessageCallback {

	final static Logger log = SimLogger.getLogger(JoinOperation.class);

	private int queryCommID;

	public JoinOperation(VonNode node, OperationCallback callback) {
		super(node, callback);

	}

	@Override
	protected void execute() {
		log.debug(getComponent().getOverlayID() + " initiated join");

		scheduleOperationTimeout(VonConfiguration.OP_TIMEOUT_JOIN);

		VonNode node = getComponent();

		InitialQueryMsg qm = new InitialQueryMsg(new VonContact(
				node.getVonID(), node.getTransInfo()), node.getPosition(),
				node.getAOI());

		node.getMsgHandler().sendInitialQueryMsgToBootstrap(qm);
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		// Will never be called as we do not use sendAndWait!

		log.error(getComponent().getVonID() + " msg timeout in JoinOperation.");
		operationFinished(false);
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// Will never be called as we do not use sendAndWait!
		if (this.queryCommID == commId)
			operationFinished(true);
		else {
			operationFinished(false);
		}
	}

	/**
	 * Inform waiting JoinOperation about the arrival of the PeerMsg
	 */
	public void peerMsgReceived() {
		operationFinished(true);
	}

	public void churnDuringJoin() {
		log.error(getComponent().getVonID()
				+ " could not complete join due to churn.");
		operationFinished(false);
	}
}
