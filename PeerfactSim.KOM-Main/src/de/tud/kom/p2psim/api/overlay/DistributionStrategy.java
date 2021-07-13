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



package de.tud.kom.p2psim.api.overlay;

import java.util.List;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.transport.TransInfo;

/**
 * Instance of a Content Distribution Strategy.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @version 1.0, 11/25/2007
 */
public interface DistributionStrategy extends OverlayNode {
	/**
	 * Calling this method will make the strategy to download the document
	 * identified by the given key from the given peers. Depending on the
	 * strategy implementation one, some or all of the provided peers will be
	 * involved.
	 * 
	 * @param key
	 *            - identifies the requested document
	 * @param peers
	 *            - addresses of hosts which should have copies of the requested
	 *            document
	 * @param callback
	 *            - callback for this operation
	 * @return operation id
	 */
	public int downloadDocument(OverlayKey key, List<TransInfo> peers,
			OperationCallback callback);

	/**
	 * The address at which this strategy waits for incoming download requests.
	 * 
	 * @return trans info address of this component.
	 */
	public TransInfo getTransInfo();

}
