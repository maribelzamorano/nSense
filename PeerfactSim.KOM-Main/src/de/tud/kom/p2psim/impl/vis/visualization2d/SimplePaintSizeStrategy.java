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


package de.tud.kom.p2psim.impl.vis.visualization2d;

import java.util.HashMap;

import de.tud.kom.p2psim.impl.vis.api.metrics.Metric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayEdgeMetric;
import de.tud.kom.p2psim.impl.vis.api.metrics.overlay.OverlayNodeMetric;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayEdge;
import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class SimplePaintSizeStrategy implements IPaintSizeStrategy {

	public HashMap<Metric, Float> maxValueLookAhead = new HashMap<Metric, Float>();

	public HashMap<Metric, Float> minValueLookAhead = new HashMap<Metric, Float>();

	@Override
	public float computeStrokeFor(OverlayEdgeMetric m, VisOverlayEdge e,
			float normalSize, float maxSize) {

		if (m != null) {

			try {

				String mValue = m.getValue(e);

				if (mValue == null)
					return normalSize;

				float size = normalSize + getSizeFromMetricValue(m, mValue)
						* maxSize;

				return size;

			} catch (NumberFormatException ex) {
				return normalSize;
			}
		} else
			return normalSize;
	}

	@Override
	public int computeNodeSizeFor(OverlayNodeMetric m, VisOverlayNode n,
			int minSize, int maxSize) {

		if (m != null) {

			try {

				String mValue = m.getValue(n);

				if (mValue == null)
					return minSize;

				return (int) Math.rint(minSize
						+ getSizeFromMetricValue(m, mValue) * maxSize);

			} catch (NumberFormatException ex) {
				// Ist kein numerischer Wert.
				return minSize;
			}
		} else
			return minSize;

	}

	public float getSizeFromMetricValue(Metric m, String metricValue)
			throws NumberFormatException {
		float float_val = Float.valueOf(metricValue);
		updateMaxValue(m, float_val);
		updateMinValue(m, float_val);

		float minVal = minValueLookAhead.get(m);
		float maxVal = maxValueLookAhead.get(m);

		return (float_val - minVal) / (maxVal - minVal);
	}

	private void updateMinValue(Metric m, float float_val) {
		if (!minValueLookAhead.containsKey(m)
				|| minValueLookAhead.get(m) > float_val) {
			minValueLookAhead.put(m, float_val);
		}
	}

	protected void updateMaxValue(Metric m, float float_val) {
		if (!maxValueLookAhead.containsKey(m)
				|| maxValueLookAhead.get(m) < float_val) {
			maxValueLookAhead.put(m, float_val);
		}
	}

}
