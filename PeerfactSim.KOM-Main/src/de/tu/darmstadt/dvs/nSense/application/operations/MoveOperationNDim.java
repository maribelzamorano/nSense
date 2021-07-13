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

package de.tu.darmstadt.dvs.nSense.application.operations;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.application.IDOApplicationNDim;
import de.tu.darmstadt.dvs.nSense.application.IDONodeNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tu.darmstadt.dvs.nSense.overlay.util.Configuration;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.application.ido.operations.MoveOperation;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation execute a movement for a player. The player will be moved in
 * the virtual world. The player will be simulated in the
 * {@link IDOApplicationNDim} .
 * 
 * <p>
 * The new position will be get to the node (@link {@link IDONodeNDim}) of the
 * application, that should be disseminate this position to other players.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 * 
 */
public class MoveOperationNDim extends
		AbstractOperation<IDOApplicationNDim, Object> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(MoveOperation.class);

	/**
	 * The node, that disseminate the new position
	 */
	private IDONodeNDim node;

	/**
	 * The application, that create this event.
	 */
	private IDOApplicationNDim app;

	/**
	 * Stores the app and the node for this operation.
	 * 
	 * @param app
	 *            The application, that call this operation.
	 * @param callback
	 *            A callback instance.
	 */
	public MoveOperationNDim(IDOApplicationNDim app,
			OperationCallback<Object> callback) {
		super(app, callback);
		this.app = app;
		this.node = app.getNode();
	}

	/**
	 * Derive new position and disseminate the new Position. Additionally the
	 * position is stored in the application.
	 */
	protected void execute() {
		if (node.getPeerStatus() == PeerStatus.PRESENT) {
			VectorN newPosition = IDOApplicationNDim.getMoveModel()
					.getNextPosition(app);
			VectorN newPositionReduced = new VectorN(
					new float[Configuration.DIMENSION]);
			for (int i = 0; i < Configuration.DIMENSION; i++) {
				newPositionReduced.setValue(i, newPosition.getValue(i));
			}

			node.disseminatePosition(newPositionReduced);
			node.setPositionDimensions(newPosition);

			app.setPlayerPositionDimensions(newPosition);
			app.setPlayerPosition(newPositionReduced);
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// nothing to get back
		return null;
	}

	/**
	 * Stops the operation. The operation will be not executed.
	 */
	public void stopOperation() {
		if (log.isDebugEnabled())
			log.debug("The Move Operation is stopped for node: "
					+ node.getOverlayID());
		operationFinished(false);
	}

}
