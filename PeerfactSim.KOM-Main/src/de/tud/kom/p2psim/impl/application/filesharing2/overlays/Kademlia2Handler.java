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
import de.tud.kom.p2psim.impl.application.filesharing2.FilesharingApplication;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.ExponentialIntervalModel;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.MaxMinDistIntervalModel;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.PeriodicCapableOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.PeriodicCapableOperation.IntervalModel;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.AbstractKademliaNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.lookup.DataLookupOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * Filesharing2 overlay handler for the Kademlia overlay implementation by Tobias Lauinger.
 * For the generation of Kademlia overlay keys, the rank of the resource is hashed, 
 * with a salt that is uniquely generated for each simulation. This guarantees rank&lt;=&gt;key consistency,
 * but ensures the usage of different keys for every simulation that is made.
 * <br />
 * The keys are periodically republished according to the Kademlia specification.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class Kademlia2Handler extends StructuredHandler {

	AbstractKademliaNode node;

	public static long republishIntervalMin = 80 * Simulator.MINUTE_UNIT;

	public static long republishIntervalMax = 90 * Simulator.MINUTE_UNIT; // In
																			// Kademlia,
																			// after
																			// 50/60
																			// minutes,
																			// resources
																			// are
																			// republished.

	public static IntervalModel republishIntervalModel = new MaxMinDistIntervalModel(
			republishIntervalMin, republishIntervalMax);

	public Kademlia2Handler(AbstractKademliaNode node) {
		this.node = node;
	}

	@Override
	public void publishSingleResource(int resourceKey) {
		publishResourceOnce(resourceKey);
		new RepublishOperation(resourceKey, this.getFSApplication(),
				Operations.getEmptyCallback())
				.schedulePeriodically(republishIntervalModel);
	}

	protected void publishResourceOnce(int resourceKey) {
		OperationCallback callback = this.new PublishCallback();
		node.storeRank(resourceKey, "Value of " + resourceKey, callback);
		this.publishStarted(node.getLocalContact(), resourceKey, callback);
	}

	@Override
	public void join() {
		node.connect();
		new RandomWorkloadOp(this.getFSApplication(), Operations.getEmptyCallback())
				.schedulePeriodically(new ExponentialIntervalModel(
						1 * Simulator.MINUTE_UNIT));
	}

	@Override
	public void leave() {
		node.disconnect();
	}

	@Override
	public void lookupResource(int key) {
		OperationCallback callback = this.new LookupCallback();
		node.lookupRank(key, callback);
		this.lookupStarted(node.getLocalContact(), callback);
	}

	public class PublishCallback implements OperationCallback {

		@Override
		public void calledOperationFailed(Operation op) {
			// System.out.println("Kademlia store failed. " +
			// ((AbstractKademliaOperation)op).getState());
			System.out
					.println("STORE Failed: " + op.getClass().getSimpleName());
		}

		@Override
		public void calledOperationSucceeded(Operation op) {
			publishSucceeded(node.getLocalContact(), null, 0, this);
		}

	}

	public class LookupCallback implements OperationCallback {

		@Override
		public void calledOperationFailed(Operation op) {
			DataLookupOperation lookupOp = (DataLookupOperation) op;
			System.out.println("LOOKUP Failed: " + lookupOp.getState() + ", "
					+ lookupOp.getData());
		}

		@Override
		public void calledOperationSucceeded(Operation op) {
			DataLookupOperation lookupOp = (DataLookupOperation) op;
			lookupSucceeded(node.getLocalContact(), this, lookupOp
					.getHopCount());
		}

	}

	public class RepublishOperation extends
			PeriodicCapableOperation<FilesharingApplication, Object> {

		int rank;

		public RepublishOperation(int rank, FilesharingApplication component,
				OperationCallback<Object> callback) {
			super(component, callback);
			this.rank = rank;
		}

		@Override
		protected void executeOnce() {
			publishResourceOnce(rank);
		}

		@Override
		public Object getResult() {
			return null;
		}

	}

	public class RandomWorkloadOp extends
			PeriodicCapableOperation<FilesharingApplication, Object> {

		int c = 5;

		public RandomWorkloadOp(FilesharingApplication component,
				OperationCallback<Object> callback) {
			super(component, callback);
		}

		@Override
		protected void executeOnce() {
			if (this.getComponent().getHost().getNetLayer().isOnline())
				node.lookupRank(Simulator.getRandom().nextInt(10000));
			c--;
			if (c <= 0)
				this.stop();
		}

		@Override
		public Object getResult() {
			return null;
		}

	}

}
