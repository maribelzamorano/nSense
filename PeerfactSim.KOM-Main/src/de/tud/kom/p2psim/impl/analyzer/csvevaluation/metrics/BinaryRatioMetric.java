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

import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 * Returns ratio of positives in percent.
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class BinaryRatioMetric implements Metric {

	static final int PERCENT = 100;

	int positive = 0;

	int negative = 0;

	protected void addPositive() {
		positive++;
	}

	protected void addNegative() {
		negative++;
	}

	@Override
	public String getMeasurementFor(long time) {
		double result = Double.NaN;
		if (!(positive == 0 && negative == 0))
			result = (double) positive / (positive + negative) * PERCENT;
		reset();
		return NumberFormatToolkit.floorToDecimalsString(result, 3);
	}

	public void reset() {
		positive = 0;
		negative = 0;
	}

}
