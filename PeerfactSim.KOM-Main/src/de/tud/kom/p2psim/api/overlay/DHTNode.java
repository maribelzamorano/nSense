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
import java.util.Set;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.dht.DHTListenerSupported;

/**
 * This interface provides a common API for structured peer-to-peer overlays.
 * 
 * @author Sebastian Kaune, refined by Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public interface DHTNode<T extends OverlayContact> extends
		JoinLeaveOverlayNode, DHTListenerSupported {
	/**
	 * Store the given document with the given key at the responsible node. The
	 * result (provided to the caller object) will be an OverlayContact of the
	 * node which performed the store operation.
	 * 
	 * @param key
	 *            - key of the mapping to store
	 * @param obj
	 *            - value of the mapping to store
	 * @param callback
	 *            - callback which will receive the operation result. The result
	 *            is a Set of OverlayContacts of nodes that stored the object.
	 * @return operation id
	 */
	public int store(OverlayKey key, DHTObject obj,
			OperationCallback<Set<T>> callback);

	/**
	 * Get the value stored with the given. The result will be an instance of
	 * DHTObject or a null reference if such a value was not found.
	 * 
	 * @param key
	 *            - key of the value to look up
	 * @param callback
	 *            - callback which will receive the operation result, which is
	 *            the wanted DHTObject if present in the DHT, null otherwise
	 * @return operation id
	 * 
	 */
	public int valueLookup(OverlayKey key, OperationCallback<DHTObject> callback);

	/**
	 * Get the node responsible for the given key, if a document with such a key
	 * does not exist. The result will be an overlay contact.
	 * 
	 * @param key
	 *            - key of the OverlayNode to lookup
	 * @param callback
	 *            - callback which will receive the operation result. The result
	 *            is an ordered List of OverlayContacts. The list's order should
	 *            describe the distance to the queried OverlayKey in an
	 *            ascending manner.
	 * @param returnSingleNode
	 *            - whether only one peer should be returned.
	 * @return operation id
	 */
	public int nodeLookup(OverlayKey key, OperationCallback<List<T>> callback,
			boolean returnSingleNode);

}
