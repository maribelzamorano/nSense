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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.LookupComplexityAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.LookupStore;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.MessageTimer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.callbacks.OperationTimer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.ChordOverlayUtil;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation tries to find the responsible node for a specified key
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupOperation extends AbstractChordOperation<List<ChordContact>> {

	final static Logger log = SimLogger.getLogger(LookupOperation.class);

	private final ChordNode masterNode;

	private final ChordID target;

	private ChordContact responsibleContact = null;

	private int lookupId;

	private int lookupHopCount;

	private int redoCounter = 0;

	public LookupOperation(ChordNode component, ChordID target,
			OperationCallback<List<ChordContact>> callback) {

		super(component, callback);
		this.masterNode = component;
		this.target = target;
		lookupId = this.getOperationID();
	}

	public LookupOperation(ChordNode component, ChordID target,
			OperationCallback<List<ChordContact>> callback, int lookupId) {

		this(component, target, callback);
		this.lookupId = lookupId;
	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			// scheduleOperationTimeout(ChordConfiguration.LOOKUP_TIMEOUT);

			// register in LookupStore
			log.debug("start lookup id = " + lookupId + " redo = "
					+ redoCounter);
			if (redoCounter == 0) {
				if (ChordConfiguration.DO_CHORD_EVALUATION)
					LookupStore.getInstance().registerNewLookup(
							masterNode.getLocalChordContact(), lookupId,
							Simulator.getCurrentTime());
			}
			// Start Operation Timer
			new OperationTimer(this, ChordConfiguration.OPERATION_TIMEOUT);

			ChordRoutingTable routingTable = masterNode.getChordRoutingTable();

			if (routingTable.responsibleFor(target)) {
				// itself is responsible for the key

				deliverResult(masterNode.getLocalChordContact(), target,
						getLookupId(), 0);

			} else {

				// Get maximum finger that precedes the id
				ChordContact precedingFinger = routingTable
						.getClosestPrecedingFinger(target);

				// if no finger precedes the id
				if (precedingFinger.equals(masterNode.getLocalChordContact())) {
					// forward to direct successor

					ChordContact succ = routingTable.getSuccessor();

					LookupMessage msg = new LookupMessage(
							masterNode.getLocalChordContact(),
							routingTable.getSuccessor(), target, lookupId, 0);

					MessageTimer msgTimer = new MessageTimer(masterNode, msg,
							succ);
					masterNode.getTransLayer().sendAndWait(msg,
							succ.getTransInfo(), masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);

				} else {
					// forward to the found preceding finger

					LookupMessage msg = new LookupMessage(
							masterNode.getLocalChordContact(), precedingFinger,
							target, lookupId, 0);

					MessageTimer msgTimer = new MessageTimer(masterNode, msg,
							precedingFinger);
					masterNode.getTransLayer().sendAndWait(msg,
							precedingFinger.getTransInfo(),
							masterNode.getPort(),
							ChordConfiguration.TRANSPORT_PROTOCOL, msgTimer,
							ChordConfiguration.MESSAGE_TIMEOUT);
				}
			}
		} else {
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(false);
		}
	}

	public void timeoutOccurred() {

		if (!isFinished()) {
			if (redoCounter < ChordConfiguration.OPERATION_MAX_REDOS) {
				log.info("lookup redo id = " + this.getOperationID()
						+ " times = " + redoCounter);
				redoCounter++;
				this.execute();
			} else {
				log.debug("look up aborted id = " + lookupId + " redotime = "
						+ redoCounter);

				masterNode.removeLookupOperation(getLookupId());
				operationFinished(false);
			}
		}
	}

	public void deliverResult(ChordContact responsibleContact,
			ChordID targetKey, int lookupOperationID, int hopCount) {
		lookupHopCount = hopCount;

		log.debug("lookup finish id = " + getLookupId() + " redo = "
				+ redoCounter);

		if (ChordConfiguration.DO_CHORD_EVALUATION)
			analyzeLookupResult(responsibleContact, targetKey,
					lookupOperationID, hopCount);
		this.responsibleContact = responsibleContact;

		if (!isFinished()) {
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(true);
		}

		/*
		 * FIXME: Used to inform the ChordComplexityAnalyzer about a
		 * successfully finished lookup. This was the easiest way to access this
		 * data. If the analyzer is not used anymore, this can be removed.
		 */
		if (LookupComplexityAnalyzer.getInstance() != null)
			LookupComplexityAnalyzer.getInstance().lookupFinished(hopCount);
	}

	private void analyzeLookupResult(ChordContact responsibleContact,
			ChordID targetKey, int lookupOperationID, int hopCount) {

		// CHECK without this block in order to speed up simulation

		ChordNode responder = ChordOverlayUtil.getResponsibleNode(masterNode
				.getBootstrapManager().getAvailableNodes(), targetKey.getValue());
		boolean valid = responder.getLocalChordContact().equals(
				responsibleContact);
		if (!valid) {
			log.debug("incorrect lookup result" + " key = " + targetKey
					+ " correct responder " + responder + " found = "
					+ responsibleContact);
		}
		LookupStore.getInstance().lookupFinished(lookupOperationID,
				Simulator.getCurrentTime(), hopCount, valid);
	}

	@Override
	public List<ChordContact> getResult() {
		LinkedList<ChordContact> respNodes = new LinkedList<ChordContact>();
		respNodes.add(responsibleContact);
		return respNodes;
	}

	public int getLookupId() {
		return lookupId;
	}

	public ChordID getTarget() {
		return target;
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}

}
