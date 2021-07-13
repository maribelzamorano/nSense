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

import java.util.Set;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSPublishDocStructOp;

/**
 * In the most structured overlays, every resource has to be published for itself.
 * This abstract overlay handler allows the publishing of multiple resources one-by-one.
 * 
 * @author Leo Nobach  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public abstract class StructuredHandler extends AbstractOverlayHandler {

	/**
	 * After a key publish was started, the next one is delayed by 
	 * this value to avoid a congestion.
	 */
	static final int distributionDelay = 100000; // 10ms

	// Allows to "smoothly" announce new documents to the network.

	@Override
	public void publishResources(Set<Integer> resources) {
		int delayInc = 0;
		for (Integer resource : resources) {
			FSPublishDocStructOp publishOp = new FSPublishDocStructOp(this
					.getFSApplication(), new OperationCallback<Object>() {

				@Override
				public void calledOperationFailed(Operation op) {
					// TODO Auto-generated method stub

				}

				@Override
				public void calledOperationSucceeded(Operation op) {
					// TODO Auto-generated method stub

				}

			}, this, resource);
			publishOp.scheduleWithDelay(delayInc);
			delayInc += distributionDelay;
		}
	}

	/**
	 * Publishes a single resource in the overlay network
	 * @param resourceKey : the integer rank of the resource.
	 */
	public abstract void publishSingleResource(int resourceKey);

}
