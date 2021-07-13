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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.distribution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Efficient implementation of IDistribution
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <Identifier>
 * @version 05/06/2011
 */
public class Distribution<Identifier> implements IDistribution<Identifier> {

	public SortedMap<Long, ValueAmount> valueAmounts = new TreeMap<Long, ValueAmount>();

	public Map<Identifier, Long> values = new HashMap<Identifier, Long>();

	public void setValue(Identifier id, long value) {
		if (id == null)
			throw new IllegalArgumentException(
					"The identifier must not be null.");
		if (values.containsKey(id)) {
			long oldVal = values.get(id);
			values.put(id, value);
			changeValueAmount(oldVal, false);
			changeValueAmount(value, true);
		} else {
			values.put(id, value);
			changeValueAmount(value, true);
		}
	}

	private void changeValueAmount(long value, boolean increase) {

		ValueAmount valAmount = valueAmounts.get(value);

		if (valAmount != null) {
			if (increase) {
				valAmount.increase();
			} else {
				if (valAmount.getAmount() == 1)
					valueAmounts.remove(value);
				else
					valAmount.decrease();
			}
		} else {
			if (increase) {
				valueAmounts.put(value, new ValueAmount());
			} else {
				throw new IllegalStateException(
						"ValueAmount may not be decreased since it is 0");
			}
		}
	}

	public void remove(Identifier id) {
		if (values.containsKey(id)) {
			changeValueAmount(values.get(id), false);
			values.remove(id);

		}
	}

	/*
	 * public void getAllDistributionParameters(IDistributionReceiver rcv) { int
	 * j = 0; for (int val : valueAmounts.keySet()) { ValueAmount amount =
	 * valueAmounts.get(val); for (int i = 0; i < amount.getAmount(); i++){
	 * rcv.receiveDistributionValue((double)j/values.size(), val); j++; } }
	 * //System.out.println(j + "=" + getDistributionSize()); }
	 */

	public DistResultStream getResultStream() {
		return new DistResultStream();
	}

	public int getDistributionSize() {
		return values.size();
	}

	public class DistResultStream implements IDistResultStream {

		Iterator<Long> valuesIt;

		int valuesSize;

		long actualValue;

		int actualValueRemaining = -1;

		public DistResultStream() {
			valuesIt = valueAmounts.keySet().iterator();
			valuesSize = values.size();
		}

		public int getDistSize() {
			return valuesSize;
		}

		public long getNextValue() {
			if (actualValueRemaining > 0) {
				actualValueRemaining--;
				return actualValue;
			}
			actualValue = valuesIt.next();
			actualValueRemaining = valueAmounts.get(actualValue).getAmount() - 1;
			return actualValue;
		}

	}

	protected class ValueAmount {
		int amount = 1;

		public void increase() {
			amount++;
		}

		public void decrease() {
			amount--;
		}

		public int getAmount() {
			return amount;
		}
	}

}
