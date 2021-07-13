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

package de.tud.kom.p2psim.impl.overlay.ido;

import java.awt.Point;
import java.util.List;

import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.overlay.IDONode;
import de.tud.kom.p2psim.api.overlay.IDONodeInfo;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.application.ido.IDOApplication;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;

/**
 * Abstract class for IDO implementation. It provides a set of functions which
 * are helpfully for a implementation of an IDO node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 * 
 * @param <T extens OverlayID> Instances of a {@link OverlayID} can be used in
 *        this abstract class.
 */
public abstract class AbstractIDONode<T extends OverlayID> extends
		AbstractOverlayNode<T> implements IDONode, ConnectivityListener {

	/**
	 * The position of the node in the virtual world
	 */
	private Point position;

	/**
	 * The AOI radius of the node
	 */
	private int aoi;

	/**
	 * @param peerId
	 *            An OverlayID for this node.
	 * @param port
	 *            The port for the incoming messages.
	 * @param aoi
	 *            The AOI radius of this node.
	 */
	protected AbstractIDONode(T peerId, short port, int aoi) {
		super(peerId, port);
		setAOI(aoi);
	}

	/**
	 * Gets the position of this node return.
	 * 
	 * @return The actually position of this node.
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Sets the position of the node.
	 * 
	 * @param position
	 *            The new position for this node.
	 */
	protected void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Sets the AOI radius for this node
	 * 
	 * @param aoi
	 *            the AOI radius
	 */
	public void setAOI(int aoi) {
		this.aoi = aoi;
	}

	/**
	 * Gets the AOI radius for this node
	 * 
	 * @return The AOI radius
	 */
	public int getAOI() {
		return aoi;
	}

	/**
	 * Gets the associated application for this node.
	 * 
	 * @return The associated application for this node.
	 */
	public IDOApplication getApplication() {
		if (getHost() != null && getHost().getApplication() != null
				&& getHost().getApplication() instanceof IDOApplication)
			return (IDOApplication) getHost().getApplication();
		return null;
	}

	/**
	 * Node leave the overlay.
	 * 
	 * @param crash
	 *            If <code>true</code> then, the node goes offline without to
	 *            execute a routine. Otherwise it can execute a routine.
	 */
	public abstract void leave(boolean crash);

	/**
	 * Join with the given position to the overlay.
	 * 
	 * @param position
	 *            The position on the map, where the node join.
	 */
	public abstract void join(Point position);

	/**
	 * Disseminate the position to the nodes, that are interested of this
	 * information. Additionally sets the position for this node.
	 * 
	 * @param position
	 *            The position, which should be disseminated
	 */
	public abstract void disseminatePosition(Point position);

	/**
	 * Gets a list of nodes back, that the node knows. Thats are nodes, which
	 * are in the AOI and Nodes, which are used for the connectivity of the
	 * overlay.
	 * 
	 * @return A list of {@link IDONodeInfo}.
	 */
	public abstract List<IDONodeInfo> getNeighborsNodeInfo();

	/**
	 * Gets the {@link BootstrapManager} back.
	 * 
	 * @return The {@link BootstrapManager} of this node.
	 */
	public abstract BootstrapManager getBootstrapManager();

	/**
	 * Sets the {@link BootstrapManager} for the node.
	 * 
	 * @param bootstrapManager
	 *            The {@link BootstrapManager} for the node.
	 */
	public abstract void setBootstrapManager(BootstrapManager bootstrapManager);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tud.kom.p2psim.api.overlay.OverlayNode#getNeighbors()
	 */
	public INeighborDeterminator getNeighbors() {
		return null;
	}
}
