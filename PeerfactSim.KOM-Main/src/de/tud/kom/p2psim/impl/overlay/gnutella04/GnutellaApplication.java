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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FileFactory;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingDocument;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.PushOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.QueryOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ScheduleApplicationOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ScheduleConnectOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ScheduleStateOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaApplication extends AbstractApplication {

	private GnutellaOverlayNode node;

	// propability for a upload, if updateRessource is called
	private double propUp;

	// propability for a ressource to be deleted, if updateRessource is called
	private double propDel;

	// propability for a download, if updateRessource is called
	private double propDown;

	// time between query and download
	private long downloadDelay;

	public GnutellaApplication(GnutellaOverlayNode node, double propUp,
			double propDel, double propDown, long downloadDelay) {
		this.node = node;
		this.propUp = propUp;
		this.propDel = propDel;
		this.propDown = propDown;
		this.downloadDelay = downloadDelay;
	}

	public void registerBootstrap() {
		GnutellaBootstrapManager.getInstance().registerNode(node);
	}

	public void startScheduleConnecting(long delay) {
		ScheduleConnectOperation scheduleConnectOperation = new ScheduleConnectOperation(
				node, delay, new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						// 
					}

					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
		scheduleConnectOperation.scheduleWithDelay((long) (Simulator
				.getRandom().nextDouble() * BigInteger.valueOf(delay)
				.doubleValue()));
	}

	public void startScheduleApplication(long delay) {
		ScheduleApplicationOperation scheduleApplicationOperation = new ScheduleApplicationOperation(
				this, delay, new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						//
					}

					public void calledOperationSucceeded(Operation<Object> op) {
						//
					}
				});
		scheduleApplicationOperation.scheduleWithDelay((long) (Simulator
				.getRandom().nextDouble() * BigInteger.valueOf(delay)
				.doubleValue()));
	}

	public void startScheduleState(long delay) {
		ScheduleStateOperation scheduleStateOperation = new ScheduleStateOperation(
				this.node, delay, new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						// 
					}

					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
		scheduleStateOperation.scheduleImmediately();
	}

	public void schedule(ScheduleApplicationOperation operation) {
		// upload
		if (Simulator.getRandom().nextDouble() < propUp) {
			this.node.addDocument(FileFactory.getInstance().getFile());
		}
		// delete
		List<FilesharingDocument> documents = new LinkedList<FilesharingDocument>(
				this.node.getDocuments());
		for (FilesharingDocument document : documents) {
			if (Simulator.getRandom().nextDouble() < propDel) {
				this.node.removeDocument(document);
			}
		}
		// download
		if (Simulator.getRandom().nextDouble() < propDown) {
			FilesharingKey key = FileFactory.getInstance().getKey();
			QueryOperation queryOperation = new QueryOperation(this.node, key,
					new OperationCallback<Object>() {
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			queryOperation.scheduleImmediately();
			BigInteger descriptor = queryOperation.getDescriptor();
			PushOperation pushOperation = new PushOperation(this.node,
					descriptor, key, new OperationCallback<Object>() {
						public void calledOperationFailed(Operation<Object> op) {
							// 
						}

						public void calledOperationSucceeded(
								Operation<Object> op) {
							// 
						}
					});
			pushOperation.scheduleWithDelay(downloadDelay);
		}
	}

}
