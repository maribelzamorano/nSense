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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics;

import java.util.HashSet;
import java.util.Set;

import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Hosts implements Metric {

	public Hosts() {
		LiveMonitoring.addProgressValue(this.new HostsProgress());
	}

	Set<NetID> hostsOnline = new HashSet<NetID>();

	Set<NetID> hostsOffline = new HashSet<NetID>();

	@Override
	public String getMeasurementFor(long time) {
		return String.valueOf(hostsOnline.size());
	}

	@Override
	public String getName() {
		return "Hosts";
	}

	public boolean hostIsOnline(NetID id) {
		return hostsOnline.contains(id);
	}

	public void hostSeen(NetID id) {
		if (!hostsOffline.contains(id))
			hostsOnline.add(id);
	}

	public void hostRemoved(NetID id) {
		hostsOffline.remove(id);
		hostsOnline.remove(id);
	}

	public void goneOffline(NetID id) {
		hostsOnline.remove(id);
		hostsOffline.add(id);
	}

	public void goneOnline(NetID id) {
		hostsOffline.remove(id);
		hostsOnline.add(id);
	}

	/**
	 * A field in the progress window displaying the result of this operation
	 * 
	 * @author
	 * 
	 */
	public class HostsProgress implements ProgressValue {

		@Override
		public String getName() {
			return "Hosts online: ";
		}

		@Override
		public String getValue() {
			return String.valueOf(hostsOnline.size() + ", off: "
					+ hostsOffline.size());
		}

	}

}
