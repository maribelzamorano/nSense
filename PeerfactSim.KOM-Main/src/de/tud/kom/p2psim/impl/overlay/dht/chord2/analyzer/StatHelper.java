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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class StatHelper<T extends Number & Comparable<T>> {

	public double[] computeAverageAndStandardDeviation(List<T> values) {
		double sum = 0;

		for (T v : values) {
			sum += v.doubleValue();
		}
		double avg = 0;
		if (values.size() > 0) {
			avg = sum / values.size();
		}

		double[] avgArr = { avg };

		double[] returnArr = concat(avgArr,
				computeStandardDeviation(values, avg));

		return returnArr;
	}

	private double[] computeStandardDeviation(List<T> values, double average) {
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		/*
		 * Compute standard deviation
		 */
		double sumSquareDDMinusAvg = 0;
		if (values.size() > 0) {
			for (T dd : values) {

				double ddMinusAvg = dd.doubleValue() - average;
				sumSquareDDMinusAvg += ddMinusAvg * ddMinusAvg;
			}
			standardDeviation = Math.sqrt(sumSquareDDMinusAvg / values.size());
		}

		Collections.sort(values);
		for (int i = 0; i < values.size(); i++) {
			Double d = values.get(i).doubleValue();

			if (d >= average) {
				List<T> underAvg = values.subList(0, i);
				List<T> overAvg = values.subList(i + 1, values.size());

				/*
				 * Compute standard deviation for values under and over the
				 * average separately.
				 */
				double sumOfSquares = 0;
				for (T dUnder : underAvg) {
					sumOfSquares += (dUnder.doubleValue() - average)
							* (dUnder.doubleValue() - average);
				}
				if (underAvg.size() > 0)
					standardDeviationMinus = Math.sqrt(sumOfSquares
							/ underAvg.size());

				sumOfSquares = 0;
				for (T dOver : overAvg) {
					sumOfSquares += (dOver.doubleValue() - average)
							* (dOver.doubleValue() - average);
				}
				if (overAvg.size() > 0)
					standardDeviationPlus = Math.sqrt(sumOfSquares
							/ overAvg.size());

				break;
			}

		}

		double[] returnArr = { standardDeviation, standardDeviationMinus,
				standardDeviationPlus };

		return returnArr;
	}

	public double computeMedian(List<T> values) {
		if (values.size() == 0)
			return 0;

		Collections.sort(values);

		double median = values.get((int) Math.floor(values.size() / 2))
				.doubleValue();

		return median;
	}

	/**
	 * Concats the given arrays to a new arrays
	 * 
	 * @param arrays
	 * @return
	 */
	private static double[] concat(double[]... arrays) {

		int targetSize = 0;

		for (double[] arr : arrays) {
			targetSize += arr.length;
		}

		double[] newArr = new double[targetSize];

		int nextStarting = 0;
		for (int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, newArr, nextStarting,
					arrays[i].length);

			nextStarting += arrays[i].length;
		}

		return newArr;
	}

}
