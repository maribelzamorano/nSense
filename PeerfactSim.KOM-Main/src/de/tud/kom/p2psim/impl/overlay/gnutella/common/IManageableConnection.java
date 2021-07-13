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


package de.tud.kom.p2psim.impl.overlay.gnutella.common;

import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;

/**
 * Part of a connection that can be managed from outside the connection
 * manager
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public interface IManageableConnection<TContact extends GnutellaLikeOverlayContact, TConnectionMetadata> extends IConnection<TContact, TConnectionMetadata> {

	/**
	 * Called when the connection attempt succeeded
	 */
	public void connectionSucceeded();
	
	/**
	 * Called when the connection attempt failed
	 */
	public void connectionFailed();
	
	/**
	 * Called when the connection attempt timeouted
	 */
	public void connectionTimeouted();
	
}
