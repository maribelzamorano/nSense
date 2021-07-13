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

package de.tud.kom.p2psim.impl.network.modular.subnet;

import de.tud.kom.p2psim.api.network.NetLayer;
import de.tud.kom.p2psim.impl.network.modular.AbstractModularSubnet;
import de.tud.kom.p2psim.impl.network.modular.IStrategies;
import de.tud.kom.p2psim.impl.network.modular.ModularNetLayer;

/**
 * Simple Subnet (former ModularSubnet), default if no Subnet is specified.
 * Subnets allow for routing of messages and different transmission paths like
 * for example WiFi and Ad-Hoc. This subnet implements the "big cloud"
 * assumption and specifies no network topology.
 * 
 * @author Leo Nobach (moved into this package and slightly modified by Bjoern <peerfact@kom.tu-darmstadt.de>
 *         Richerzhagen)
 * @version 05/06/2011
 */
public class SimpleModularSubnet extends AbstractModularSubnet {

	public SimpleModularSubnet(IStrategies strategies) {
		super(strategies);
	}

	@Override
	protected boolean isConnectionPossible(ModularNetLayer nlSender,
			ModularNetLayer nlReceiver) {
		return true; // All connections are possible (no routing)
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// no types to write back
	}

	@Override
	protected void netLayerWentOnline(NetLayer net) {
		// nothing to do here.
	}

	@Override
	protected void netLayerWentOffline(NetLayer net) {
		// nothing to do here.
	}

}
