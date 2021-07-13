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


package de.tud.kom.p2psim.impl.network.modular.st.positioning;

import java.util.List;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetPosition;
import de.tud.kom.p2psim.impl.network.modular.common.GNPToolkit;
import de.tud.kom.p2psim.impl.network.modular.db.NetMeasurementDB;
import de.tud.kom.p2psim.impl.network.modular.st.PositioningStrategy;

/**
 * Applies the (virtual) GNP position as the host's position
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GNPPositioning implements PositioningStrategy {

	@Override
	public NetPosition getPosition(Host host, NetMeasurementDB db,
			NetMeasurementDB.Host hostMeta) {

		if (hostMeta == null)
			throw new IllegalStateException(
					"The GNP positioning strategy needs a measurement database to work properly.");

		return new GNPPosition(hostMeta);

	}

	public static class GNPPosition implements NetPosition {

		private List<Double> coords;

		public GNPPosition(NetMeasurementDB.Host hostMeta) {
			this.coords = hostMeta.getCoordinates();
		}

		@Override
		public double getDistance(NetPosition netPosition) {
			if (!(netPosition instanceof GNPPosition))
				throw new AssertionError(
						"Can not calculate distances between different position classes: "
								+ this.getClass() + " and "
								+ netPosition.getClass());
			GNPPosition other = (GNPPosition) netPosition;

			return GNPToolkit.getDistance(this.coords, other.coords);
		}

		public List<Double> getCoords() {
			return coords;
		}

	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
