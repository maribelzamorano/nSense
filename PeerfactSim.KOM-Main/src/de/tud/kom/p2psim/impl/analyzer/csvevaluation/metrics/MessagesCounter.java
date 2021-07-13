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

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MessagesCounter implements Metric {

	public MessagesCounter() {
		LiveMonitoring.addProgressValue(this.new MessagesValue());
	}

	int messages = 0;

	long measurementStart = -1;

	double lastMeasurementResult = 0;

	@Override
	public String getMeasurementFor(long time) {
		long currentTime = Simulator.getCurrentTime();
		if (measurementStart == -1) {
			measurementStart = currentTime;
			return "NaN";
		}

		double result = (messages / (double) (currentTime - measurementStart) * Simulator.SECOND_UNIT);
		measurementStart = currentTime;
		lastMeasurementResult = result;
		messages = 0;

		return NumberFormatToolkit.floorToDecimalsString(result, 1);

	}

	public void messageSent(Message msg) {
		messages++;
	}

	@Override
	public String getName() {
		return "Messages/sec";
	}

	/**
	 * A field in the progress window displaying the result of this operation
	 * 
	 * @author
	 * 
	 */
	public class MessagesValue implements ProgressValue {

		@Override
		public String getName() {
			return "Messages per second";
		}

		@Override
		public String getValue() {
			return NumberFormatToolkit.floorToDecimalsString(
					lastMeasurementResult, 2);
		}

	}

}
