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

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
public class MessagesLookBack implements Metric {

	public MessagesLookBack() {
		LiveMonitoring.addProgressValue(this.new MessagesValue());
	}

	Queue<Long> msgs = new LinkedBlockingQueue<Long>();

	static final long second = 1000000;

	// Warning: Using Gnutella or Gia, the following value should not be higher
	// than 1 sec.
	protected static final long timeUnitsLookBack = 1 * second; // 1 sec

	long lastInsertedTime = 0;

	String lastMeasurementResult = "n/a";

	@Override
	public String getMeasurementFor(long time) {
		cleanupQueue(time);
		String result = NumberFormatToolkit.floorToDecimalsString((double) msgs
				.size()
				/ timeUnitsLookBack * second, 3);
		lastMeasurementResult = result;
		return result;
	}

	@Override
	public String getName() {
		return "msgs/sec";
	}

	public void messageSent(Message msg) {

		long atTime = Simulator.getCurrentTime();

		cleanupQueue(atTime);

		if (atTime < lastInsertedTime)
			throw new AssertionError(
					"Time message sent was before last inserted message: "
							+ atTime + " < " + lastInsertedTime);
		this.lastInsertedTime = atTime;

		msgs.add(atTime);
		// System.out.println("Size: " + msgs.size() + " Adding: " + atTime);
	}

	private void cleanupQueue(long actualTime) {

		long deleteUntil = actualTime - timeUnitsLookBack;

		for (;;) {
			synchronized (msgs) {
				try {
					long msgTime = msgs.element();
					if (msgTime > deleteUntil)
						return;
					msgs.remove();
					// System.out.println("Size: " + msgs.size() + " Removing: "
					// + msgTime);
				} catch (NoSuchElementException e) {
					// Queue is empty, ok.
					return;
				}
			}
		}
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
			return lastMeasurementResult;
		}

	}

}
