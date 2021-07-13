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

package de.tud.kom.p2psim.impl.overlay.ido.mercury;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.overlay.IDGenerationHelper;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * {@link ComponentFactory} for the IDO with Mercury.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 * 
 */
public class MercuryIDONodeFactory implements ComponentFactory {
	// TODO: Collisionen testen...
	@Override
	public Component createComponent(Host host) {

		// generate a ID for the node.
		MercuryIDOID id = null;
		do {
			String ip = host.getNetLayer().getNetID().toString();
			String random = new Integer(Simulator.getRandom().nextInt())
					.toString();
			id = new MercuryIDOID(
					Integer.parseInt((IDGenerationHelper.getSHA1Hash(ip
							+ random, 31).toString()), 10));
		} while (id.equals(MercuryIDOID.EMPTY_ID));

		MercuryIDONode node = new MercuryIDONode(id, host);
		return node;
	}
}
