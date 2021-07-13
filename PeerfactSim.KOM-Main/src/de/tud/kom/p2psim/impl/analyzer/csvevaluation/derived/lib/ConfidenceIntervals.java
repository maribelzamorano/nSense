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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConfidenceIntervals implements IYValueSet {

	private double confidence;

	int decimals;

	static final String separator = " 	";

	List<Double> values = new ArrayList<Double>();

	boolean sorted = false;

	public ConfidenceIntervals(double confidence, int decimals) {
		this.confidence = confidence;
		this.decimals = decimals;
	}

	protected void prepareValues() {
		if (!sorted) {
			Collections.sort(values);
			sorted = true;
		}
	}

	public double getQuantile(double alpha) {
		prepareValues();
		int size = values.size();

		if (size == 0)
			return Double.NaN;

		double kDouble = (size-1) * alpha;
		int k = (int) Math.floor(kDouble);
		double g = kDouble - k;

		double lowerValue = values.get(k);

		if (values.size() <= k + 1)
			return lowerValue;

		double upperValue = values.get(k + 1);

		//System.out.println("size=" + size + " k=" + kDouble + " g=" + g + " lowerValue=" + lowerValue);
		
		return (1 - g) * lowerValue + g * upperValue;

	}

	public double getMedian() {
		return getQuantile(0.5d);
	}

	public double getLowerBound() {
		return getQuantile(1 - confidence);
	}

	public double getUpperBound() {
		return getQuantile(confidence);
	}

	@Override
	public void addValue(double value) {
		values.add(value);
	}

	@Override
	public String printCaptionForFile() {
		return "lower 	median 	upper";
	}

	@Override
	public String printForFile() {
		return NumberFormatToolkit.floorToDecimalsString(getLowerBound(), decimals) + separator + NumberFormatToolkit.floorToDecimalsString(getMedian(), decimals) + separator
				+ NumberFormatToolkit.floorToDecimalsString(getUpperBound(), decimals);
	}
	
	public static class ConfidenceIntervalsFactory implements IYValueSetFactory<ConfidenceIntervals> {

		private int decimals;
		private double confidence;

		public ConfidenceIntervalsFactory(double confidence, int decimals) {
			this.confidence = confidence;
			this.decimals = decimals;
		}
		
		@Override
		public ConfidenceIntervals newIYValueSet() {
			return new ConfidenceIntervals(confidence, decimals);
		}
		
	}

	@Override
	public int getNumberOfValues() {
		return values.size();
	}

}
