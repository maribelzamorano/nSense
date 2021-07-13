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


package de.tud.kom.p2psim.impl.application.filesharing2.overlays;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.dht.SimpleDHTObject;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordIDFactory;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordKey;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.ValueLookupOperation;

/**
 * Filesharing2 overlay handler for the Chord2 overlay implementation by Minh
 * Hoang Nguyen. For the generation of Chord overlay keys, the rank of the
 * resource is hashed, with a salt that is uniquely generated for each
 * simulation. This ensures hash consistency, but ensures the usage of different
 * keys for every simulation made.
 * 
 * @author Leo Nobach, adapted for chord2 by Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Chord2Handler extends StructuredHandler {

	ChordNode node;

	public Chord2Handler(ChordNode node) {
		this.node = node;
	}

	@Override
	public void join() {
		node.join(Operations.EMPTY_CALLBACK);
	}

	@Override
	public void leave() {
		node.leave(Operations.EMPTY_CALLBACK);
	}

	@Override
	public void lookupResource(int key) {

		ChordKey chordKey = ChordIDFactory.getInstance()
				.getChordID(String.valueOf(key)).getCorespondingKey();

		LookupCallback callback = new LookupCallback();
		this.lookupStarted(node.getLocalChordContact(), callback);
		node.valueLookup(chordKey, callback);
	}

	@Override
	public void publishSingleResource(int resourceKey) {

		ChordKey chordKey = ChordIDFactory.getInstance()
				.getChordID(String.valueOf(resourceKey)).getCorespondingKey();

		SimpleDHTObject obj = new SimpleDHTObject();
		obj.setKey(chordKey);

		StoreCallback callback = new StoreCallback();
		this.publishStarted(node.getLocalChordContact(), resourceKey, callback);
		node.store(chordKey, obj, callback);
	}

	class StoreCallback implements OperationCallback {

		@Override
		public void calledOperationFailed(Operation op) {
			// TODO Auto-generated method stub

		}

		@Override
		public void calledOperationSucceeded(Operation op) {
			Chord2Handler.this.publishSucceeded(node.getLocalChordContact(),
					null, 0, this);
		}

	}

	class LookupCallback implements OperationCallback {

		@Override
		public void calledOperationFailed(Operation op) {
			// Nothing to do
		}

		@Override
		public void calledOperationSucceeded(Operation op) {
			ValueLookupOperation lookupOp = (ValueLookupOperation) op;
			int hops = lookupOp.getLookupHopCount();
			Chord2Handler.this.lookupSucceeded(node.getLocalChordContact(),
					this, hops);
		}

	}

}
