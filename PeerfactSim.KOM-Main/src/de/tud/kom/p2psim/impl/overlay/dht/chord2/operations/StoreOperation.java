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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.StoreMessage;

/**
 * This operation is used to realize the store functionality of the DHTNode
 * interface. It stores a given instance of DHTObject at the node responsible
 * for a given key. Therefore it initializes a lookup for the key and then sends
 * a StoreMessage to the responsible peer, returned by the lookup.
 * 
 * The operation expects message losses and does retransmissions when needed.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class StoreOperation extends AbstractChordOperation<Set<ChordContact>>
		implements OperationCallback<List<ChordContact>>, TransMessageCallback {

	/*
	 * The key of the object to be stored
	 */
	private final ChordID targetKey;

	/*
	 * The object to be stored
	 */
	private final DHTObject object;

	/*
	 * The number of unsuccessful retries to lookup the responsible peer
	 */
	private int lookupRetryCount = 0;

	/*
	 * The number of unsuccessful retries to send the StoreMessage to the
	 * responsible peer
	 */
	private int storeMessageSentRetryCount = 0;

	/*
	 * The value store message to be directly sent to the responsible peer
	 */
	private StoreMessage storeMsg;

	public StoreOperation(ChordNode component, ChordID targetKey,
			DHTObject object, OperationCallback<Set<ChordContact>> callback) {
		super(component, callback);

		this.targetKey = targetKey;
		this.object = object;
	}

	/*
	 * AbstractChordOperation methods
	 */

	@Override
	protected void execute() {
		if (!getComponent().isPresent())
			return;

		// checking if the requesting node itself is responsible to store the
		// data item (then create dummy storeMsg, as result), otherwise the
		// message is routed.
		if (getComponent().isRootOf(new ChordKey(targetKey.getValue()))) {
			getComponent().getDHT().addDHTEntry(targetKey.getCorespondingKey(),
					object);
			storeMsg = new StoreMessage(getComponent().getLocalChordContact(),
					getComponent().getLocalChordContact(), targetKey, object);
			operationFinished(true);
		} else {
			scheduleOperationTimeout(ChordConfiguration.OPERATION_TIMEOUT);
			getComponent().overlayNodeLookup(targetKey, this);
		}
	}

	@Override
	public Set<ChordContact> getResult() {
		if (!isError() || storeMsg != null) {
			Set<ChordContact> receivers = new HashSet<ChordContact>();
			receivers.add(storeMsg.getReceiverContact());
			return receivers;
		}
		return null;
	}

	/*
	 * OperationCallback methods
	 */

	@Override
	public void calledOperationFailed(Operation<List<ChordContact>> op) {

		if (lookupRetryCount <= ChordConfiguration.OPERATION_MAX_REDOS) {
			lookupRetryCount++;
			getComponent().overlayNodeLookup(targetKey, this);
		} else
			operationFinished(false);
	}

	@Override
	public void calledOperationSucceeded(Operation<List<ChordContact>> op) {
		List<ChordContact> targets = op.getResult();
		if (!targets.isEmpty()) {
			ChordContact targetContactForStoreMsg = op.getResult().get(0);

			storeMsg = new StoreMessage(getComponent().getLocalChordContact(),
					targetContactForStoreMsg, targetKey, object);

			sendStoreMsg(storeMsg);
		}
	}

	private void sendStoreMsg(StoreMessage msg) {
		getComponent().getTransLayer().sendAndWait(msg,
				msg.getReceiverContact().getTransInfo(),
				getComponent().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				ChordConfiguration.MESSAGE_TIMEOUT);
	}

	/*
	 * TransMessageCallback methods
	 */

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		operationFinished(true);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (storeMessageSentRetryCount <= ChordConfiguration.MESSAGE_RESEND) {
			storeMessageSentRetryCount++;
			sendStoreMsg(storeMsg);
		} else {
			operationFinished(false);
		}
	}

}
