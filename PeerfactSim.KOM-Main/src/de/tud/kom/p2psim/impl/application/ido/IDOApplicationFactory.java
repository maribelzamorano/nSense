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


package de.tud.kom.p2psim.impl.application.ido;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.impl.application.ido.moveModels.IMoveModel;
import de.tud.kom.p2psim.impl.application.ido.moveModels.IPositionDistribution;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * The Factory class for the IDO application of the hosts.
 * 
 * Over this class, should be set the <code>moveModel</code>,
 * <code>PositionDistribution</code> and the <code>intervalBetweenMove</code>.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class IDOApplicationFactory implements ComponentFactory {
	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(IDOApplicationFactory.class);

	/**
	 * The time between two moves, that should be used in the application.
	 */
	private long intervalBetweenMove = 200 * Simulator.MILLISECOND_UNIT;

	/**
	 * Create an IDOApplication and gets the IDOnode from the host to the
	 * application.
	 * 
	 * @param host
	 *            One host
	 */
	@Override
	public Component createComponent(Host host) {
		IDONode node = (IDONode) host.getOverlay(IDONode.class);
		IDOApplication app = new IDOApplication(node, intervalBetweenMove);

		if (log.isDebugEnabled())
			log.debug("create IDO-application " + app);
		return app;
	}

	/**
	 * Sets the move model in the {@link IDOApplication}, that should be used to
	 * move the players.
	 * 
	 * @param model
	 *            The move model
	 */
	public static void setMoveModel(IMoveModel model) {
		IDOApplication.setMoveModel(model);
	}

	/**
	 * Sets the position distribution model in {@link IDOApplication}, that
	 * should be used to distribute the players on start.
	 * 
	 * @param posDistribution
	 *            The position distribution model
	 */
	public static void setPositionDistribution(
			IPositionDistribution posDistribution) {
		IDOApplication.setPositionDistribution(posDistribution);
	}

	/**
	 * Sets the interval between two moves.
	 * 
	 * @param time
	 *            The time between two moves.
	 */
	public void setIntervalBetweenMove(long time) {
		this.intervalBetweenMove = time;
	}

}
