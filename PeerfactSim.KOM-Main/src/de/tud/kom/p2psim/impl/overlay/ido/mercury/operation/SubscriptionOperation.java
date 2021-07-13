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

import java.awt.Point;
import java.util.Vector;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.ido.mercury.MercuryIDONode;
import de.tud.kom.p2psim.impl.service.mercury.filter.IMercuryFilter;
import de.tud.kom.p2psim.impl.service.mercury.filter.IMercuryFilter.OPERATOR_TYPE;

/**
 * This operation execute in time intervals a subscription for the given node.<br>
 * 
 * This is needed, because the node change his position. Therefore it change the
 * subscription.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class SubscriptionOperation extends
		AbstractOperation<MercuryIDONode, Object> {
	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public SubscriptionOperation(MercuryIDONode node,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (node.isPresent()) {
			Point position = node.getPosition();
			Vector<IMercuryFilter> filters = new Vector<IMercuryFilter>();
			filters.add(node.getService().createFilter(
					node.getService().getAttributeByName("x"),
					position.x - node.getAOI(), OPERATOR_TYPE.greater));
			filters.add(node.getService().createFilter(
					node.getService().getAttributeByName("x"),
					position.x + node.getAOI(), OPERATOR_TYPE.smaller));
			filters.add(node.getService().createFilter(
					node.getService().getAttributeByName("y"),
					position.y - node.getAOI(), OPERATOR_TYPE.greater));
			filters.add(node.getService().createFilter(
					node.getService().getAttributeByName("y"),
					position.y + node.getAOI(), OPERATOR_TYPE.smaller));
			node.getService().subscribe(filters);
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// do nothing
		return null;
	}

	/**
	 * Stop this operation.
	 */
	public void stop() {
		operationFinished(false);
	}
}
