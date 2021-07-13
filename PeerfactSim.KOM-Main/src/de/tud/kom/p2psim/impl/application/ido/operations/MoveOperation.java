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

package de.tud.kom.p2psim.impl.application.ido.operations;

import java.awt.Point;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.impl.application.ido.IDOApplication;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This operation execute a movement for a player. The player will be moved in
 * the virtual world. The player will be simulated in the {@link IDOApplication}
 * .
 * 
 * <p>
 * The new position will be get to the node (@link {@link IDONode}) of the
 * application, that should be disseminate this position to other players.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class MoveOperation extends AbstractOperation<IDOApplication, Object> {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(MoveOperation.class);

	/**
	 * The node, that disseminate the new position
	 */
	private IDONode node;

	/**
	 * The application, that create this event.
	 */
	private IDOApplication app;

	/**
	 * Stores the app and the node for this operation.
	 * 
	 * @param app
	 *            The application, that call this operation.
	 * @param callback
	 *            A callback instance.
	 */
	public MoveOperation(IDOApplication app, OperationCallback<Object> callback) {
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
			Point newPosition = IDOApplication.getMoveModel().getNextPosition(
					app);
			node.disseminatePosition(newPosition);
			app.setPlayerPosition(newPosition);
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
