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

package de.tud.kom.p2psim.impl.overlay.ido.mercury.operation;

import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.MercuryIDOConfiguration;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.MercuryIDONode;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.NeighborStorage;

/**
 * Clean old entries from the {@link NeighborStorage}. Old entries are: <br>
 * 
 * <ul>
 * <li>Not more in AOI of the node</li>
 * <li>Time is expired of the information. This mean, the information is to old.
 * </li>
 * </ul>
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class CleanOperation extends AbstractOperation<MercuryIDONode, Object> {

	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public CleanOperation(MercuryIDONode node) {
		super(node);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (node.getStorage() != null) {
			node.getStorage().removeExpiredNodeInfos(
					MercuryIDOConfiguration.TIME_TO_VALID_OF_NODE_INFOS);
			node.getStorage().removeNotInAOINodeInfos(node.getPosition(),
					node.getAOI());
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// Nothing to get Back.
		return null;
	}

}
