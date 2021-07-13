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


package de.tud.kom.p2psim.impl.overlay.dht.can.operations;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This operation is used for the evaluation. It sends a lookup a message a
 * schedules itself again after a certain time
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupPeriodicallyOperation extends
		AbstractOperation<CanNode, Object> {

	private OperationCallback callback;

	CanNode node = this.getComponent();

	/**
	 * starts lookups every CanConfig.waitTimeBetweenLookups intervalls.
	 * 
	 * @param component
	 *            node which starts the lookups
	 * @param callback
	 */
	public LookupPeriodicallyOperation(CanNode component,
			OperationCallback callback) {
		super(component);
		this.callback = callback;
	}

	@Override
	public void execute() {
		String lookup = new String();
		for (int i = 0; i < 48; i++) {
			lookup = lookup + Simulator.getRandom().nextInt(9);
		}
		DataID id = new DataID(new BigInteger(lookup));
		while (id.getId().longValue() < 0)
			id = new DataID(String.valueOf((Simulator.getRandom()).nextLong()));

		node.getDataOperation.addNewLookupValue(id.getId());

		LookupOperation lookupOperation = new LookupOperation(node, id,
				callback);
		lookupOperation.scheduleImmediately();

		this.scheduleWithDelay(CanConfig.waitTimeBetweenLookups);
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
