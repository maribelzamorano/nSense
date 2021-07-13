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


package de.tud.kom.p2psim.api.common;

import de.tud.kom.p2psim.api.network.NetLayer;

/**
 * A <code>ConnectivityListener</code> must be implemented to support
 * notification that the connectivity of a host changed, i.e. that the host went
 * online or offline. Components interested in the online status should register
 * themselves at host's <code>HostProperty</code> as
 * <code>ConnectivityListener</code>s to receive <code>ConnectivityEvent</code>
 * s.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 * @see ConnectivityEvent
 * @see HostProperties#addConnectivityListener(ConnectivityListener)
 * @see NetLayer#addConnectivityListener(ConnectivityListener)
 */
public interface ConnectivityListener  {
	/**
	 * Called when the connectivity of the host change changed.
	 * 
	 * @param ce
	 *            - connectivity event
	 */
	public void connectivityChanged(ConnectivityEvent ce);
}
