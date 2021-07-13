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


package de.tud.kom.p2psim.impl.skynet.analyzing.writers;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Coor2RootEntry {

	private String metricsName;

	private double coordinatorAverageValue;

	private double interpolatedCoordAvgValue;

	private double rootAverageValue;

	private double interpolationFactor;

	public Coor2RootEntry(String metricsName, double coordinatorAverageValue,
			double interpolatedCoordAvgValue, double rootAverageValue,
			double interpolationFactor) {
		this.metricsName = metricsName;
		this.coordinatorAverageValue = coordinatorAverageValue;
		this.interpolatedCoordAvgValue = interpolatedCoordAvgValue;
		this.rootAverageValue = rootAverageValue;
		this.interpolationFactor = interpolationFactor;
	}

	public String getMetricsName() {
		return metricsName;
	}

	public double getCoordinatorAverageValue() {
		return coordinatorAverageValue;
	}

	public double getInterpolatedCoordAvgValue() {
		return interpolatedCoordAvgValue;
	}

	public double getRootAverageValue() {
		return rootAverageValue;
	}

	public double getInterpolationFactor() {
		return interpolationFactor;
	}

	public double getAbsoluteError() {
		return coordinatorAverageValue - rootAverageValue;
	}

	public double getRelativeError() {
		return getAbsoluteError() / rootAverageValue;
	}

	@Override
	public String toString() {
		return "Name = " + metricsName + "; coordinator avg = "
				+ coordinatorAverageValue + "; interpolated coordinator avg"
				+ interpolatedCoordAvgValue + "; root avg = "
				+ rootAverageValue + "; interpolation factor = "
				+ interpolationFactor + "; absolute error = "
				+ getAbsoluteError() + "; relative error" + getRelativeError();
	}

}
