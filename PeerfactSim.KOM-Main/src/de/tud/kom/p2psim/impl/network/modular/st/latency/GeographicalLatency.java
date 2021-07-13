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


package de.tud.kom.p2psim.impl.network.modular.st.latency;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.network.modular.ModularNetLayer;
import de.tud.kom.p2psim.impl.network.modular.common.GeoToolkit;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB;
import de.tud.kom.p2psim.impl.network.modular.st.LatencyStrategy;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Applies a latency derived from the geographical distance of two hosts.
 * The latency is. 31ms * 0.01ms/km * distance between the hosts in km.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class GeographicalLatency implements LatencyStrategy {

	static Logger log = SimLogger.getLogger(GeographicalLatency.class);
	
	@Override
	public long getMessagePropagationDelay(NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {
		
		if (db == null) throw new IllegalArgumentException("The Geographical Latency strategy can not access any network " +
				"measurement database. You may not have loaded it in the config file.");
		
		NetMeasurementDB.Host hSender = nlSender.getDBHostMeta();
		NetMeasurementDB.Host hReceiver = nlReceiver.getDBHostMeta();
		
		double distance = GeoToolkit.getDistance(hSender.getLatitude(), hSender.getLongitude(), hReceiver.getLatitude(), hReceiver.getLongitude());
		
		log.debug("Distance between " + nlSender + " and " + nlReceiver + " is " + distance + " meters.");
		
		return Math.round(getStaticPart(msg, nlSender, nlReceiver, db) + (getGeoDistFactor() * distance) * Simulator.MILLISECOND_UNIT);
	}

	protected double getGeoDistFactor() {
		return 0.00001d;
	}

	protected double getStaticPart(NetMessage msg, ModularNetLayer nlSender,
			ModularNetLayer nlReceiver, NetMeasurementDB db) {
		return 31d;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		//No simple/complex types to write back
	}

}
