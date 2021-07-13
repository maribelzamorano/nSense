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

package de.tud.kom.p2psim.impl.util.oracle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.scenario.Composable;
import de.tud.kom.p2psim.api.scenario.Configurator;
import de.tud.kom.p2psim.api.scenario.HostBuilder;

/**
 * This class gives access to the hosts of the scenario. To work, it has to be
 * referenced in the configuration file after the host builder.
 * 
 * The purpose of this class is to enable a global knowledge for analyzing. It
 * is not meant to be used within any functional parts of simulated systems.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GlobalOracle implements Composable {

	private static HostBuilder hostBuilder;

	private static HashMap<NetID, Host> netIDtoHosts = new HashMap<NetID, Host>();

	private static List<Host> hosts = new LinkedList<Host>();

	@Override
	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);

		hosts = hostBuilder.getAllHosts();
		for (Host host : hosts) {
			netIDtoHosts.put(host.getNetLayer().getNetID(), host);
		}
	}

	/**
	 * @param id
	 * @return the host with the given <code>NetID</code>
	 */
	public static Host getHostForNetID(NetID id) {
		return netIDtoHosts.get(id);
	}

	/**
	 * @return the list with all hosts of the scenario
	 */
	public static List<Host> getHosts() {
		return new LinkedList<Host>(hosts);
	}
}
