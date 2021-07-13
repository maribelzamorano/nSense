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

import java.util.List;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.DHTObject;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupReplyMessage;

/**
 * This operation is used to realize the value lookup functionality of the
 * DHTNode interface. It retrieves a stored instance of DHTObject if the
 * responsible peer holds it in its store. Therefore it initializes a lookup for
 * the key and then sends a ValueLookupMessage to the responsible peer, returned
 * by the lookup.
 * 
 * The operation expects message losses and does retransmissions when needed.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ValueLookupOperation extends AbstractChordOperation<DHTObject>
		implements OperationCallback<List<ChordContact>>, TransMessageCallback {

	/*
	 * The key of the object to be looked up
	 */
	private final ChordID targetKey;

	/*
	 * The number of unsuccessful retries to lookup the responsible peer using a
	 * LookupOperation
	 */
	private int lookupRetryCount = 0;

	/*
	 * The value lookup message to be directly sent to the responsible peer
	 */
	private ValueLookupMessage valLookupMsg;

	/*
	 * The number of unsuccessful retries to send the ValueLookupMessage to the
	 * responsible peer
	 */
	private int valLookupMsgSentRetryCount = 0;

	/*
	 * The result of the lookup. The field is Null until a result was retrieved.
	 */
	private DHTObject lookupResult;

	/*
	 * The number of hops the lookup for the responsible peer took.
	 */
	private int lookupHopCount;

	public ValueLookupOperation(ChordNode component, ChordID targetKey,
			OperationCallback<DHTObject> callback) {
		super(component, callback);

		this.targetKey = targetKey;
	}

	/*
	 * AbstractChordOperation methods
	 */

	@Override
	protected void execute() {
		if (!getComponent().isPresent())
			return;

		scheduleOperationTimeout(ChordConfiguration.OPERATION_TIMEOUT);
		getComponent().overlayNodeLookup(targetKey, this);
	}

	@Override
	public DHTObject getResult() {
		if (!isError())
			return lookupResult;
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
			ChordContact targetContactForMsg = targets.get(0);

			if (op instanceof LookupOperation)
				lookupHopCount = ((LookupOperation) op).getLookupHopCount();

			valLookupMsg = new ValueLookupMessage(getComponent()
					.getLocalChordContact(), targetContactForMsg, targetKey);

			sendLookupMsg(valLookupMsg);
		}
	}

	private void sendLookupMsg(ValueLookupMessage msg) {
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
		if (msg instanceof ValueLookupReplyMessage) {
			lookupResult = ((ValueLookupReplyMessage) msg).getObject();
			operationFinished(true);
		} else
			operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		if (valLookupMsgSentRetryCount <= ChordConfiguration.MESSAGE_RESEND) {
			valLookupMsgSentRetryCount++;
			sendLookupMsg(valLookupMsg);
		} else {
			operationFinished(false);
		}
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}

}
